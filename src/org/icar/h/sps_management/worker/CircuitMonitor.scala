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
  scenario.open_switchers = ArrayBuffer[String]("switchswp1","switchswp2","switchswp3","switchswp4","switchswp5","switchswp6","switchswauxg1")
  scenario.up_generators = ArrayBuffer[String]("mg1","auxg1")

  var fault : Set[String] = _

  val gui : AmperometerGui= new AmperometerGui()
  var sensorActor : String = ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("sensor.actor")

  var switcherActor : String = ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("switcher.actor")

  var sendH : Boolean = true
  var SensorArrayMonitor : ActorSelection = null
  var SwitcherMonitor : ActorSelection = null

  //Check if remote is active!
  if(sensorActor.equals("true")) {
    SensorArrayMonitor = context.actorSelection("akka.tcp://RemoteSystem@"+ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("sensor.remote.ip")+":5150/user/sensor") //IP of the PC remote
    println("That 's remote:" + SensorArrayMonitor)
  }

  if(switcherActor.equals("true")) {
    SwitcherMonitor = context.actorSelection("akka.tcp://RemoteSystem@"+ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("switcher.actor.ip")+":5160/user/switcher") //IP of the PC remote
    println("That 's remote:" + SwitcherMonitor)
  }
  override def preStart: Unit = {
    log.info("ready")

    if(switcherActor.equals("true"))
      SwitcherMonitor ! RequestUpdateScenario()

  }

  override def receive: Receive = {

    case CheckFailure(mission_ref) =>

      if(sensorActor.equals("true"))
        SensorArrayMonitor ! StartCheckMonitor()
      else
        self ! 10.0             //if you don't have raspberry

    case msg : Double =>        //if you don't have raspberry
      Thread.sleep(3000)
      bridge.sendHead("failure(f1)")


    case RaspDataVal(data) =>

      gui.testGui(data)
      for (i <- 0 to 0) {
        if (data.getCurrent(i) < 0 & sendH) {
          fault += "switchf1"

          bridge.sendHead("failure(f1)")
          sendH = false
        }
      }


//    case RequestUpdateScenario() =>     //update the scenario (state of switchers)
//      SwitcherMonitor ! RequestUpdateScenario()
//      Thread.sleep(5000)
//      self ! RequestUpdateScenario()

    case UpdateScenario(open_switchers) =>
      scenario.open_switchers = (open_switchers | fault).to[ArrayBuffer]
      println(scenario.open_switchers)

    case GetCurrentScenarioDescription() =>
      sender() ! CurrentScenarioDescription(scenario)


    case _ =>
      log.error("unspecified message")
  }
}