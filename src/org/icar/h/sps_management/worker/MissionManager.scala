package org.icar.h.sps_management.worker

import akka.actor.{Actor, ActorLogging, Props}
import org.icar.h.Akka2Jade
import org.icar.musa.scenarios.sps.Mission

object MissionManager {
  def props(bridge : Akka2Jade) : Props = Props(classOf[MissionManager],bridge)
}

class MissionManager(val bridge : Akka2Jade) extends Actor with ActorLogging {
  case class SetCurrentMission(mission_ref : String)

  var missions : Map[String,Mission] = Map(
    "on_shore" -> Mission.circuit3_mission_1
  )
  var current_mission_ref : String = ""

  override def preStart : Unit = {
    log.info("ready")

    self ! SetCurrentMission("on_shore")
  }

  override def receive: Receive = {
    case SetCurrentMission(mission_ref) =>
      println(s"set the current mission to $mission_ref")
      current_mission_ref = mission_ref
      bridge.sendHead(s"current_mission($mission_ref)" )

    case GetMissionDescription(mission_ref) =>
      log.info("sending back the requested mission!")
      sender() ! MissionDescription(mission_ref,missions(mission_ref))

    case _ ⇒
      println("PlanGen: unspecied message")

  }
}
