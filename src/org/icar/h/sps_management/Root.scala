package org.icar.h.sps_management


import java.util.ResourceBundle

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, Props}
import org.icar.h.sps_management.worker._
import jason.asSyntax.{Atom, Structure}
import org.icar.h.core.Akka2Jade

object Root {
  def props(bridge : Akka2Jade) : Props = Props(classOf[Root],bridge)
}

class Root(val bridge : Akka2Jade) extends Actor with ActorLogging {
  private val mission_manager : ActorRef = context.actorOf(MissionManager.props(bridge), "mission_manager")
  private val sensor_checkers = context.actorOf(CircuitMonitor.props(bridge), "sensor_checkers")
  private val sps_reconfigurator = context.actorOf(SPSPlanGenerator.props(bridge,mission_manager,sensor_checkers), "sps_reconfigurator")
  private val plan_validator_rem = context.actorOf(SPSPlanValidatorRem.props(bridge, sps_reconfigurator,sensor_checkers), "plan_validator_rem")

  // private val plan_validator = context.actorOf(SPSPlanValidator.props(bridge,sps_reconfigurator,sensor_checkers), "plan_validator")
  private val plan_executor : ActorRef = context.actorOf(ReconfigurationEnactor.props(bridge,sps_reconfigurator,plan_validator_rem), "plan_executor")

  var faultEnactor : ActorSelection = null
  //actor Fault
  faultEnactor = context.actorSelection("akka.tcp://RemoteSystem@" + ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("sensor_2.remote.ip") + ":5151/user/fault") //IP of the PC remote
  println("That 's remote:" + faultEnactor)

  override def preStart : Unit = {
    log.info("ready")
  }

  override def receive: Receive = {
    case x : String ⇒
      val structure : Structure = Structure.parse(x)

      structure.getFunctor match {
        case "check_failure" if check_structure(structure,1) =>
          val par = get_structure_arg(structure,0)
          sensor_checkers ! CheckFailure()

        case "find_reconfigurations" if check_structure(structure,2) =>
          val par1 = get_structure_arg(structure,0)
          val par2 = get_structure_arg(structure,1)
          sps_reconfigurator ! FindSolutions(par1,par2)

        case "validate" if check_structure(structure,1) =>
          val par = get_structure_arg(structure,0)
          //plan_validator ! Validate(par)
          plan_validator_rem ! Validate(par)

        case "enact" if check_structure(structure,1) =>
          val par = get_structure_arg(structure,0)
          plan_executor ! Enact(par)

        case "enact_single" if check_structure(structure,1) =>
          val par = get_structure_arg(structure,0)
          plan_executor ! EnactSingle(par)

        case "fault" if check_structure(structure,1) =>
          val par = get_structure_arg(structure,0)
          faultEnactor ! EnactFault(par)
        case _ =>
          println("Root: unspecied message")

      }

    case _ ⇒
      println("Root: wrong serialization format")
  }

  private def check_structure(structure : Structure, arg_number : Int) : Boolean = {
    // prima controlla che il numero di argomenti coincida con quello indicato
    if (structure.getTerms.size()!=arg_number)
      false

    // poi controlla che ogni argomento sia un atomo
    else {
      var res = true
      for (i <- 0 until arg_number if res)
        if (!structure.getTerm(i).isAtom)
          res = false

      res
    }

  }

  private def get_structure_arg(structure : Structure, index : Int) = structure.getTerm(index).asInstanceOf[Atom].getFunctor
}