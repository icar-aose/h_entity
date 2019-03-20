package org.icar.h.sps_management.worker

import akka.actor.{Actor, ActorLogging, Props}
import org.icar.h.Akka2Jade

object MissionManager {
  def props(bridge : Akka2Jade) : Props = Props(classOf[MissionManager],bridge)
}

class MissionManager extends Actor with ActorLogging {

  var missions : Map[String,String] = Map()

  override def preStart : Unit = {
    log.info("ready")
  }

  override def receive: Receive = {
    case GetMission(mission_ref) =>
      log.info("sending back the requested mission!")
      sender() ! Mission(mission_ref,missions(mission_ref))

    case _ ⇒
      println("PlanGen: unspecied message")

  }
}
