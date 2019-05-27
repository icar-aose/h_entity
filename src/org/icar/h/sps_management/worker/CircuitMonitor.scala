package org.icar.h.sps_management.worker

import java.util.ResourceBundle
import scala.collection.mutable.ArrayBuffer

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, ActorSystem, Props}
import cartago.{ArtifactId, CartagoException}
import cartago.util.agent.CartagoBasicContext
import org.icar.h.core.Akka2Jade
import org.icar.musa.scenarios.sps.ReconfigurationScenario

import org.icar.h.sps_management.rpi_ina219._
import org.icar.h.sps_management.worker._

object CircuitMonitor {
  def props(bridge: Akka2Jade): Props = Props(classOf[CircuitMonitor], bridge)
}

class CircuitMonitor(val bridge: Akka2Jade) extends Actor with ActorLogging {

  //configure scenario

  val scenario = new ReconfigurationScenario
  scenario.open_switchers = ArrayBuffer[String]("switchswp1","switchswp2","switchswp3","switchswp4","switchswauxg1","switchf1")
  scenario.up_generators = ArrayBuffer[String]("mg1")

  //val scenario: ReconfigurationScenario = ReconfigurationScenario.scenario_circuit3_parsed_1
  var my_context: CartagoBasicContext = new CartagoBasicContext("my_agent")
  var my_device: ArtifactId = _
  val gui : Gui= new Gui()
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
    try {
      my_device = my_context.makeArtifact("device", "org.icar.h.Device")
      my_context.focus(my_device)

    } catch {
      case e: CartagoException =>
        e.printStackTrace();
    }
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
      for (i <- 0 to 3)
        {
          if(data.getCurrent(i) < -5 & sendH)
          {
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