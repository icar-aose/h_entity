package org.icar.h.sps_management.worker

import akka.actor.{Actor, ActorLogging, NoSerializationVerificationNeeded, Props}
import org.icar.h.core.Akka2Jade
import org.icar.musa.scenarios.sps.Mission

import scala.collection.mutable.ArrayBuffer

object MissionManager {
  def props(bridge : Akka2Jade) : Props = Props(classOf[MissionManager],bridge)
}

class MissionManager(val bridge : Akka2Jade) extends Actor with ActorLogging {

  case class SetCurrentMission(mission_ref : String) extends NoSerializationVerificationNeeded


  //configure Mission
  val m = new Mission
  m.vitals =ArrayBuffer[String]("load1","load3")
  m.semivitals = ArrayBuffer[String]("load2","load4")
  m.nonvitals =ArrayBuffer[String]("load5")
  m.gen_pow += ("mg1"->20)
  m.gen_pow += ("auxg1"->20)


  var missions : Map[String,Mission] = Map(
   // "on_shore" -> Mission.circuit3_file_mission_1
    "on_shore" -> m
  )
  var current_mission_ref : String = ""

  override def preStart : Unit = {
    log.info("ready")

    self ! SetCurrentMission("on_shore")
  }

  override def receive: Receive = {
    case SetCurrentMission(mission_ref) =>
      log.info(s"set the current mission to $mission_ref")
      current_mission_ref = mission_ref
      bridge.sendHead(s"current_mission($mission_ref)" )

    case GetMissionDescription(mission_ref) =>
      log.debug("sending back the requested mission!")
      sender() ! MissionDescription(mission_ref,missions(mission_ref))

    case _ ⇒
      log.error("PlanGen: unspecified message")

  }
}
