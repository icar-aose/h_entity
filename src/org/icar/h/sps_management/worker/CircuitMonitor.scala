package org.icar.h.sps_management.worker

import java.util.ResourceBundle
import scala.collection.mutable.ArrayBuffer

import akka.actor.{Actor, ActorLogging, ActorSelection, Props}
import cartago.ArtifactId
import org.icar.h.core.Akka2Jade
import org.icar.musa.scenarios.sps.ReconfigurationScenario

import org.icar.h.sps_management.rpi_ina219._

object CircuitMonitor {
  def props(bridge: Akka2Jade): Props = Props(classOf[CircuitMonitor], bridge)
}

class CircuitMonitor(val bridge: Akka2Jade) extends Actor with ActorLogging {

  //configure scenario

  val scenario = new ReconfigurationScenario
  scenario.open_switchers = ArrayBuffer[String]("switchswp1","switchswp2","switchswp3","switchswp4","switchswp5","switchswp6","switchswauxg1","switchf1","switchf2")
  scenario.up_generators = ArrayBuffer[String]("mg1","auxg1")

  val gui : AmperometerGui= new AmperometerGui()
  var remote : String = ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("remote.actor")
  var sendH : Boolean = true
  var SensorArrayMonitor : ActorSelection = null


  //Check if remote is active!
  if(remote.equals("true")) {
    SensorArrayMonitor = context.actorSelection("akka.tcp://RemoteSystem@"+ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("actor.remote.ip")+":5150/user/remote") //IP of the PC remote
    println("That 's remote:" + SensorArrayMonitor)
  }

  override def preStart: Unit = {
    log.info("ready")

  }

  override def receive: Receive = {

    case CheckFailure(mission_ref) =>

      if(remote.equals("true"))
        SensorArrayMonitor ! StartCheckMonitor()
      else
        self ! 10.0             //if you don't have raspberry

    case msg : Double =>        //if you don't have raspberry
      Thread.sleep(3000)
      bridge.sendHead("failure(f1)")


    case RaspDataVal(data) =>

      gui.testGui(data)
      for (i <- 1 to 1) {
        if (data.getCurrent(i) < 0 & sendH) {
          bridge.sendHead("failure(f1)")
          sendH = false
        }
      }



    case GetCurrentScenarioDescription() =>
      sender() ! CurrentScenarioDescription(scenario)


    case _ =>
      log.error("unspecified message")
  }
}