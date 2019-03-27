package org.icar.h.sps_management.worker

import java.rmi.registry.LocateRegistry
import java.util

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.Timeout
import org.icar.musa.pmr._
import org.icar.musa.scenarios.sps.{Circuit, EvaluateSol}

import scala.concurrent.duration._
import scala.collection.mutable.Queue
import scala.concurrent.{Await, Future}
import akka.pattern.ask
import matMusa.MatRemote
import java.util.ResourceBundle

import org.icar.h.core.matlab._
import org.icar.h.core.Akka2Jade


object SPSPlanValidator {
   def props(bridge : Akka2Jade, worker_sps : ActorRef,circ_sens_ref : ActorRef) : Props = Props(classOf[SPSPlanValidator],bridge,worker_sps,circ_sens_ref)
}

class SPSPlanValidator(val bridge : Akka2Jade, worker_sps : ActorRef,circ_sens_ref : ActorRef) extends Actor with ActorLogging {
  case class CheckSolutionQueue()

  //queue solutions
  var solutions_to_be_validated = Queue[Plan]()

  //load circuit

  val properties: ResourceBundle = ResourceBundle.getBundle("org.icar.h.core.matlab.Simple")

  private val path: String = properties.getString("circuit.name")
  private val circuit = Circuit.load_from_file(path)


  //get current scenario
  implicit val timeout = Timeout(5 seconds)
  val future_scenario: Future[Any] = circ_sens_ref ? GetCurrentScenarioDescription()
  val scenario = Await.result(future_scenario, timeout.duration).asInstanceOf[CurrentScenarioDescription].scenario


  //MatRemote
  var stub: MatRemote = _

  val host = properties.getString("simulator.server.ip")
  val port = properties.getString("simulator.server.port").toInt

  override def preStart : Unit = {
    println("Connecting to Matlab...")
    try {
      val registry = LocateRegistry.getRegistry(host,port)
      stub = registry.lookup("MatRemote").asInstanceOf[MatRemote]
      println("Engine Matlab discovered")
      self ! CheckSolutionQueue()
    }
    catch {
      case e: Exception =>
        System.err.println("Client exception: " + e.toString)
        e.printStackTrace()
    }

    //send circuit to matlab Server set on Simple.properties

    SimpleClient.transfer()

    log.info("ready")
  }

    override def receive: Receive = {
      case Validate( plan ) =>
        log.info("i'm the validator, now contacting the sps reconfigurator for obtaining the solution: "+plan)
        worker_sps ! GetPlan(plan)

      case Plan(plan_ref,plan) =>
        solutions_to_be_validated += Plan(plan_ref,plan)

      case CheckSolutionQueue() =>

        // HERE THE CODE TO VALIDATE
        if (solutions_to_be_validated.nonEmpty) {
          val next : Plan = solutions_to_be_validated.dequeue

          log.info("Validator: executing Matlab script with the solution "+next.plan_reference)

          val result_of_validation = validate(next.plan)

          // IF THE PLAN IS CORRECT THEN...
          if (result_of_validation)
            bridge.sendHead("validated("+next.plan_reference+")")

        }

        Thread.sleep(100 )
        self ! CheckSolutionQueue()


      case _ =>
        log.error("unspecified message")
    }


  private def validate(sol: Solution): Boolean = {
    var result = new Array[Boolean](circuit.loads.size) //circuit.loads.size

    val w: Boolean = validate_well_formedness(sol, sol.start, List())

    if (w && stub != null) {
      val solution_for_matlab: util.ArrayList[String] = EvaluateSol.solution_list(sol)
      val all_switchers: util.ArrayList[String] = new util.ArrayList()
      for (s <- circuit.switcher)
        all_switchers.add(s.id.toLowerCase)
      val open_switchers: util.ArrayList[String] = new util.ArrayList()
      for (s <- scenario.open_switchers)
        open_switchers.add(s)

      //println("ALL SWITCHERS")
      //println(all_switchers)

      try {
        result = stub.evaluateSolution(solution_for_matlab, all_switchers, open_switchers)
        var xsize = 0

        println("result of generators:" + result(0))
        for (i <- 1 until result.length) {
          if (i != 10 && i != 20) {    //perchè nel circuito sono stati eliminati questi carichi quindi inesistenti
            if (result(i))
              print(1)
            else
              print(0)
          }
        }
        println("\n")
      }catch {
        case e: Exception =>
          println("Client exception: " + e.toString)
          e.printStackTrace()
      }
    }
    result(0)
  }



  private def validate_well_formedness(sol: Solution, item : WfItem, visited : List[WfItem]): Boolean = {
    if (!visited.contains(item)) {
      val out = sol.arcs_out_from(item)
      if (out.length<0 || out.length>1)
        false

      else
        item match {
          case x:WfStartEvent => validate_well_formedness(sol, out(0).to , x :: visited )
          case x:WfTask => validate_well_formedness(sol, out(0).to , x :: visited )
          case x:WfEndEvent => true
          case _ => false
        }

    } else

      false
  }




}
