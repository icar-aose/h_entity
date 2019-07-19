package org.icar.h.sps_management.worker.test

import akka.actor.{ActorRef, ActorSystem}
import cartago.{CartagoService, NodeId}
import org.icar.h.core.Akka2Jade
import org.icar.h.sps_management.Root
import org.icar.h.sps_management.worker._

object TestSPSPlanGen extends App {
  val system = ActorSystem("TestSystem")
  val bridge = new Akka2Jade(null )


  val node = CartagoService.startNode
  CartagoService.installInfrastructureLayer("default")


  val mission_manager : ActorRef = system.actorOf(MissionManager.props(bridge), "mission_manager")
  val sensor_checkers = system.actorOf(CircuitMonitor.props(bridge), "sensor_checkers")
  //val sps_reconfigurator = system.actorOf(SPSPlanGenerator.props(bridge,mission_manager,sensor_checkers), "sps_reconfigurator")

  Thread.sleep(2000 )

  //sps_reconfigurator ! FindSolutions("on_shore","f1")



}
