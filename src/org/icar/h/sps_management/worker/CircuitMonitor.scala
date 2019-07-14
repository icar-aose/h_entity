package org.icar.h.sps_management.worker

import java.util.ResourceBundle
import scala.collection.mutable.ArrayBuffer

import akka.actor.{Actor, ActorLogging, ActorSelection, Props}
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

  var fault : ArrayBuffer[String] = new ArrayBuffer[String]


  var sensorActor : String = ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("sensor.actor")

  var switcherActor : String = ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("switcher.actor")

  var current : Array[Double] = new Array[Double](6)
  var DataMerged : AmpData = new AmpData(current)
  var data_fetch : Int = 0

  var sendH : Boolean = true
  var SensorArrayMonitor_1 : ActorSelection = null
  var SensorArrayMonitor_2 : ActorSelection = null
  var SwitcherMonitor : ActorSelection = null
  val r = scala.util.Random
  //Check if remote is active!
  if(sensorActor.equals("true")) {
    SensorArrayMonitor_1 = context.actorSelection("akka.tcp://RemoteSystem@"+ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("sensor_1.remote.ip")+":5150/user/sensor_1") //IP of the PC remote
    println("That 's remote:" + SensorArrayMonitor_1)
    SensorArrayMonitor_2 = context.actorSelection("akka.tcp://RemoteSystem@"+ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("sensor_2.remote.ip")+":5150/user/sensor_2") //IP of the PC remote
    println("That 's remote:" + SensorArrayMonitor_2)
  }

  if(switcherActor.equals("true")) {
    SwitcherMonitor = context.actorSelection("akka.tcp://RemoteSystem@"+ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("switcher.actor.ip")+":5160/user/switcher") //IP of the PC remote
    println("That 's remote:" + SwitcherMonitor)
  }

  val gui : FaultAmpGui= new FaultAmpGui(bridge)

  override def preStart: Unit = {
    log.info("ready")

    if(switcherActor.equals("true"))
      SwitcherMonitor ! RequestUpdateScenario()

  }

  override def receive: Receive = {

    case CheckFailure(mission_ref) =>

      if(sensorActor.equals("true")) {
        SensorArrayMonitor_1 ! StartCheckMonitor()
        SensorArrayMonitor_2 ! StartCheckMonitor()
      }
      else
        self ! 10.0             //if you don't have raspberry

    case msg : Double =>        //if you don't have raspberry

      while(true)
        {
          DataMerged.setCurrent(r.nextDouble(),0)
          DataMerged.setCurrent(r.nextDouble(),1)
          DataMerged.setCurrent(r.nextDouble(),2)
          DataMerged.setCurrent(r.nextDouble(),3)
          DataMerged.setCurrent(r.nextDouble(),4)
          DataMerged.setCurrent(r.nextDouble(),5)
          Thread.sleep(1000)

          gui.updateGui(DataMerged,scenario.open_switchers.toArray)
        }
      bridge.sendHead("failure(f1)")


    case RaspDataVal(data,index_rasp) =>

      data_fetch = data_fetch+1

      if(index_rasp==0)
      {
        DataMerged.setCurrent(data.getCurrent(0),0)
        DataMerged.setCurrent(data.getCurrent(1),1)
        DataMerged.setCurrent(data.getCurrent(2),2)
        DataMerged.setCurrent(data.getCurrent(3),3)
      }
      else
      {
        DataMerged.setCurrent(data.getCurrent(0),5)
        DataMerged.setCurrent(data.getCurrent(1),4)
      }

      if(data_fetch==2)
        {
          gui.updateGui(DataMerged,scenario.open_switchers.toArray)
          data_fetch=0
        }
      if(index_rasp==0)
        {
          if (data.getCurrent(0) < 0 & data.getCurrent(2) > 10 & sendH) {
            fault +="switchf1"
            fault +="switchf5"
            bridge.sendHead("failure(f1)")
            sendH = false
          }
          if (data.getCurrent(2) < 1 & sendH) {
            fault +="switchf3"
            fault +="switchf5"
            bridge.sendHead("failure(f3)")
            sendH = false
          }
        }



    case UpdateScenario(open_switchers_current) =>
     // println("arrivato: "+open_switchers_current)
      scenario.open_switchers = open_switchers_current ++ fault
     // println("salvato: "+scenario.open_switchers)

    case GetCurrentScenarioDescription() =>
      sender() ! CurrentScenarioDescription(scenario)


    case _ =>
      log.error("unspecified message")
  }
}