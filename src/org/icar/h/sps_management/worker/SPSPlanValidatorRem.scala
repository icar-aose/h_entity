package org.icar.h.sps_management.worker

import java.rmi.registry.LocateRegistry
import java.util
import java.util.ResourceBundle

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, NoSerializationVerificationNeeded, Props}
import akka.pattern.ask
import akka.util.Timeout
import org.icar.h.core.Akka2Jade
import org.icar.h.core.matlab.matEngine.MatRemote
import org.icar.h.sps_management.EvaluateSol
import org.icar.musa.pmr._
import org.icar.musa.scenarios.sps.Circuit

import scala.collection.mutable.Queue
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object SPSPlanValidatorRem {
  def props(bridge : Akka2Jade, worker_sps : ActorRef,circ_sens_ref : ActorRef) : Props = Props(classOf[SPSPlanValidatorRem],bridge,worker_sps,circ_sens_ref)
}
class SPSPlanValidatorRem(val bridge : Akka2Jade, worker_sps : ActorRef,circ_sens_ref : ActorRef) extends Actor with ActorLogging {

  case class CheckSolutionQueue() extends NoSerializationVerificationNeeded

  //queue solutions
  var solutions_to_be_validated = Queue[Plan]()

  //load circuit

  val properties: ResourceBundle = ResourceBundle.getBundle("org.icar.h.sps_management.Boot")

  private val path: String = properties.getString("circuit.name")
  private val circuit = Circuit.load_from_file(path)


  //get current scenario
  implicit val timeout = Timeout(5 seconds)
  val future_scenario: Future[Any] = circ_sens_ref ? GetCurrentScenarioDescription()
  val scenario = Await.result(future_scenario, timeout.duration).asInstanceOf[CurrentScenarioDescription].scenario

  //sorted map value matlab


  var all_plans : Map[String,Solution] = Map.empty

  var result = new util.HashMap[String, java.lang.Double](circuit.loads.size + circuit.generators.size)
 // var result_actor_false : util.HashMap[String, Double] += util.HashMap("genresult"->1.0)
  var result_of_validation = true
  var remote: String = ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("simulator.matlab")
  var RemoteMatActor: ActorSelection = null

  //Check if remote is active!
  if (remote.equals("true")) {
    RemoteMatActor = context.actorSelection("akka.tcp://RemoteSystem@" + properties.getString("simulator.actor.ip") + ":" + Integer.parseInt(properties.getString("simulator.actor.port")) + "/user/remote_matlab")  //IP of the PC remote
    println("That 's remote:" + RemoteMatActor)


    //send file!!

    RemoteMatActor ! start()


  }


  override def preStart: Unit = {


    self ! CheckSolutionQueue()

    log.info("ready")
  }

  override def receive: Receive = {
    case Validate(plan_reference) =>
      log.debug("i'm the validator, now contacting the sps reconfigurator for obtaining the solution: " + plan_reference)
      worker_sps ! GetPlan(plan_reference)

    case Plan(plan_ref, plan) =>
      solutions_to_be_validated += Plan(plan_ref, plan)
      all_plans += (plan_ref -> plan)


    case CheckSolutionQueue() => {
      // HERE THE CODE TO VALIDATE
      if (solutions_to_be_validated.nonEmpty) {
        val next: Plan = solutions_to_be_validated.dequeue

        //log.info("Validator: executing Matlab script with the solution " + next.plan_reference)

        if (remote.equals("true")) {

          val w: Boolean = validate_well_formedness(next.plan, next.plan.start, List())
          if (w)
          {
            val solution_for_matlab: util.ArrayList[String] = EvaluateSol.solution_list(next.plan)
            val all_switchers: util.ArrayList[String] = new util.ArrayList()
            for (s <- circuit.switcher)
              all_switchers.add(s.id.toLowerCase)
            val open_switchers: util.ArrayList[String] = new util.ArrayList()
            for (s <- scenario.open_switchers)
              open_switchers.add(s)
            RemoteMatActor ! evaluateSolution(next.plan_reference, solution_for_matlab, all_switchers, open_switchers, circuit.loads.size)
          }
        }
      }
      // IF THE PLAN IS CORRECT THEN...

      Thread.sleep(100)
      self ! CheckSolutionQueue()
    }

    case ResultSolution(result, plan_reference) =>

      var xsize = 0
//      println("Plan: "+plan_reference+", result of generators:" + result.get("genResult"))
//      var list : util.Set[String]  = result.keySet()
//      var iter : util.Iterator[String] = list.iterator()
//      while(iter.hasNext)
//      {
//        var key = iter.next()
//        print(key+": ")
//        var value = result.get(key)
//        if(value > 0)
//          println(value)
//        else
//          println(-value)
//      }

      println("\n")
      if (result.get("genResult")==1) {
        var sol = all_plans(plan_reference)
        var selected : List[String] = solution_list_gui(sol,sol.start)
        bridge.sendHead("validated(" + plan_reference +",["+selected.mkString(",")+ "])")

        //GUI solution!!
      }


    case _ =>
      log.error("unspecified message")


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


  private def solution_list_gui (graph : Solution , e : WfItem) : List[String] = {

    var out  = graph.arcs_out_from(e)

    e match {
      case x : WfStartEvent => solution_list_gui(graph,out(0).to)
      case x : WfTask => "\""+x.cap.name+"\"" :: solution_list_gui(graph,out(0).to)
      case x : WfEndEvent => List.empty
    }
  }


}
