package org.icar.h.sps_management.worker

import java.rmi.registry.LocateRegistry
import java.util

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.util.Timeout
import org.icar.h.Akka2Jade
import org.icar.musa.pmr._
import org.icar.musa.scenarios.sps.{Circuit, EvaluateSol}

import scala.concurrent.duration._
import scala.collection.mutable.Queue
import scala.concurrent.{Await, Future}
import akka.pattern.ask
import matMusa.MatRemote

import java.util.ResourceBundle

import file_transfer_sps._


object SPSPlanValidator {
   def props(bridge : Akka2Jade, worker_sps : ActorRef,circ_sens_ref : ActorRef) : Props = Props(classOf[SPSPlanValidator],bridge,worker_sps,circ_sens_ref)
}

class SPSPlanValidator(val bridge : Akka2Jade, worker_sps : ActorRef,circ_sens_ref : ActorRef) extends Actor with ActorLogging {

  //queue solutions
  var solutions = Queue[Solution]()

  //load circuit

  val properties: ResourceBundle = ResourceBundle.getBundle("Simple")
  private val path: String = properties.getString("circuit.name")
  private val circuit = Circuit.load_from_file(path)


  //get current scenario
  implicit val timeout = Timeout(5 seconds)
  val future_scenario: Future[Any] = circ_sens_ref ? GetCurrentScenarioDescription()
  val scenario = Await.result(future_scenario, timeout.duration).asInstanceOf[CurrentScenarioDescription].scenario


  //MatRemote
  var stub: MatRemote = _

  val host = properties.getString("server.ip")
  val port = 1099

  override def preStart : Unit = {
      log.info("ready")

    println("Connecting to Matlab...")
    try {
      val registry = LocateRegistry.getRegistry(host,port)
      stub = registry.lookup("MatRemote").asInstanceOf[MatRemote]
      println("Engine Matlab discovered")
    }
    catch {
      case e: Exception =>
        System.err.println("Client exception: " + e.toString)
        e.printStackTrace()
    }

    //send circuit to matlab Server set on Simple.properties

    //SimpleClient.transfer()

    }

    override def receive: Receive = {
      case Validate( plan ) =>
        log.info("i'm the validator, now contacting the sps reconfigurator for obtaining the solution: "+plan)
        worker_sps ! GetPlan(plan)

      case Plan(plan_ref,plan) =>
        Thread.sleep(2000)
        solutions+=plan

        log.info("Validator: executing Matlab script with the solution "+plan_ref+" "+plan)
        // HERE THE CODE TO VALIDATE

        val next : Solution = solutions.dequeue
        val result_of_validation = validate(next)

        /* validation... */

        // IF THE PLAN IS CORRECT THEN...
        if (result_of_validation==true)
          bridge.sendHead("validated("+plan_ref+")")

      case _ =>
        log.error("unspecified message")
    }




  def validate(sol: Solution): Boolean = {
    var result = new Array[Boolean](25)


    val w : Boolean = validate_well_formedness(sol,sol.start,List())

    if (w && stub != null) {
      val solution_for_matlab: util.ArrayList[String] = EvaluateSol.solution_list(sol)
      val all_switchers : util.ArrayList[String] = new util.ArrayList()
      for (s <- circuit.switcher)
        all_switchers.add(s.id.toLowerCase)
      val open_switchers : util.ArrayList[String] = new util.ArrayList()
      for (s<-scenario.open_switchers)
        open_switchers.add(s)

      //println("ALL SWITCHERS")
      //println(all_switchers)

      try {
        result = stub.evaluateSolution(solution_for_matlab, all_switchers, open_switchers)
        var xsize = 0

        for (x <- result)
        {if (xsize == 0)
        {
          println("result of generators:"+x)
          xsize = 1
        }
        else
        {
          if(x)
            print(1)
          else
            print(0)
          xsize = xsize + 1
        }
        }
        println("\n")
      } catch {
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
