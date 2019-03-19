package org.icar.h.sps_management


import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.icar.h.{Akka2Jade, SubWorker}
import org.icar.h.sps_management.worker._
import jason.asSyntax.{Atom, StringTerm, Structure}

object Root {
  def props(bridge : Akka2Jade) : Props = Props(classOf[Root],bridge)
}

class Root(val bridge : Akka2Jade) extends Actor with ActorLogging {
  private lazy val worker_check = context.actorOf(PowerFailureMonitor.props(bridge), "sensor_checkers")
  private lazy val worker_sps = context.actorOf(SPSPlanGenerator.props(bridge), "sps_reconfigurator")
  private lazy val worker_validator = context.actorOf(SPSPlanValidator.props(bridge,worker_sps), "plan_validator")
  private lazy val worker_plan : ActorRef = context.actorOf(ReconfigurationEnactor.props(bridge,worker_sps), "plan_executor")


  override def preStart : Unit = {
    log.info("ready")
  }

  override def receive: Receive = {
    case x : String ⇒
      val structure : Structure = Structure.parse(x)

      structure.getFunctor match {
        case "check_failure" =>
          println("identified a check failure order")
          worker_check ! CheckFailure()

        case "find_reconfigurations" if structure.getTerm(0).isAtom =>
          val par = structure.getTerm(0)
          worker_sps ! FindSolutions(par.asInstanceOf[Atom].getFunctor)

        case "validate" if structure.getTerm(0).isAtom =>
          val par = structure.getTerm(0)
          worker_validator ! Validate(par.asInstanceOf[Atom].getFunctor)

        case "enact" if structure.getTerm(0).isAtom =>
          val par = structure.getTerm(0)
          worker_plan ! Enact(par.asInstanceOf[Atom].getFunctor)

        case _ =>
          println("Root: unspecied message")

      }

    case _ ⇒
      println("Root: wrong serialization format")
  }
}