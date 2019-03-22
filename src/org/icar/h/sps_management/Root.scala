package org.icar.h.sps_management


import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.icar.h.Akka2Jade
import org.icar.h.sps_management.worker._
import jason.asSyntax.{Atom, StringTerm, Structure}

object Root {
  def props(bridge : Akka2Jade) : Props = Props(classOf[Root],bridge)
}

class Root(val bridge : Akka2Jade) extends Actor with ActorLogging {
  private val mission_manager : ActorRef = context.actorOf(MissionManager.props(bridge), "mission_manager")
  private lazy val sensor_checkers = context.actorOf(CircuitMonitor.props(bridge), "sensor_checkers")
  private lazy val sps_reconfigurator = context.actorOf(SPSPlanGenerator.props(bridge,mission_manager,sensor_checkers), "sps_reconfigurator")
  private lazy val plan_validator = context.actorOf(SPSPlanValidator.props(bridge,sps_reconfigurator), "plan_validator")
  private lazy val plan_executor : ActorRef = context.actorOf(ReconfigurationEnactor.props(bridge,sps_reconfigurator), "plan_executor")


  override def preStart : Unit = {
    log.info("ready")
  }

  override def receive: Receive = {
    case x : String ⇒
      val structure : Structure = Structure.parse(x)

      structure.getFunctor match {
        case "check_failure" if check_structure(structure,1) =>
          val par = get_structure_arg(structure,0)
          sensor_checkers ! CheckFailure(par)

        case "find_reconfigurations" if check_structure(structure,2) =>
          val par1 = get_structure_arg(structure,0)
          val par2 = get_structure_arg(structure,1)
          sps_reconfigurator ! FindSolutions(par1,par2)

        case "validate" if check_structure(structure,1) =>
          val par = get_structure_arg(structure,0)
          plan_validator ! Validate(par)

        case "enact" if check_structure(structure,1) =>
          val par = get_structure_arg(structure,0)
          plan_executor ! Enact(par)

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
      for (i <- 0 until arg_number if res==true)
        if (!structure.getTerm(i).isAtom)
          res = false

      res
    }

  }

  private def get_structure_arg(structure : Structure, index : Int) = structure.getTerm(index).asInstanceOf[Atom].getFunctor
}