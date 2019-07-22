package org.icar.h.sps_management.worker

import java.io.File

import akka.actor._
import com.typesafe.config.ConfigFactory
import processing.io.GPIO

import scala.collection.mutable.ArrayBuffer

class SwitcherMonitor extends Actor with ActorLogging {

  val swm1Pin = 25 //Relais 1 motore 1
  val swm1busPin = 13 //Relais 2 motore 1
  val swm2Pin = 27 //Relais 1 motore 2
  val swm2busPin = 17 //Relais 2 motore 2
  val swmg1Pin = 6 //Relais1 main gen 1
  val swmg1busPin = 5 //Relais2 main gen 1
  val swL1Pin = 20 //Relais 1 Load 1
  val swL1busPin = 16 //Relais 2 Load 1
  val swL2Pin = 12 //Relais 1 Load 2
  val swL2busPin = 21 //Relais 2 Load 2
  val swauxg1Pin = 26 //Relais1 aux gen 1
  val swauxg1busPin = 19 //Relais2 aux gen 1

  val NamePinNum: Map[String, Int] = Map("swm1Pin" -> 25, "swm1busPin" -> 13, "swm2Pin" -> 27
    , "swm2busPin" -> 17, "swmg1Pin" -> 6, "swmg1busPin" -> 5,
    "swL1Pin" -> 20, "swL1busPin" -> 16, "swL2Pin" -> 12, "swL2busPin" -> 21,
    "swauxg1Pin" -> 26, "swauxg1busPin" -> 19)

  var open_switchers: ArrayBuffer[String] = new ArrayBuffer[String]

  var circuit_monitor: ActorRef = null

  override def preStart(): Unit = {

    log.info("Ready")
    import processing.io.GPIO
    GPIO.pinMode(swm1Pin, GPIO.OUTPUT)
    GPIO.pinMode(swm1busPin, GPIO.OUTPUT)
    GPIO.pinMode(swm2Pin, GPIO.OUTPUT)
    GPIO.pinMode(swm2busPin, GPIO.OUTPUT)
    GPIO.pinMode(swmg1Pin, GPIO.OUTPUT)
    GPIO.pinMode(swmg1busPin, GPIO.OUTPUT)
    GPIO.pinMode(swauxg1Pin, GPIO.OUTPUT)
    GPIO.pinMode(swauxg1busPin, GPIO.OUTPUT)
    GPIO.pinMode(swL1Pin, GPIO.OUTPUT)
    GPIO.pinMode(swL1busPin, GPIO.OUTPUT)
    GPIO.pinMode(swL2Pin, GPIO.OUTPUT)
    GPIO.pinMode(swL2busPin, GPIO.OUTPUT)
    self ! SwitcherMonitoring()

  }


  override def receive: Receive = {


    case SwitcherMonitoring() =>

      var readVal: Int = 0
      open_switchers.clear()

      for ((k, v) <- NamePinNum) {
        readVal = GPIO.digitalRead(v)

        k match {
          case "swm1busPin" => if (readVal == 1) open_switchers += "switchswp1" else open_switchers += "switchsws1"
          case "swm2busPin" => if (readVal == 1) open_switchers += "switchswp2" else open_switchers += "switchsws2"
          case "swmg1busPin" => if (readVal == 1) open_switchers += "switchswp3" else open_switchers += "switchsws3"
          case "swL1busPin" => if (readVal == 1) open_switchers += "switchswp4" else open_switchers += "switchsws4"
          case "swL2busPin" => if (readVal == 1) open_switchers += "switchswp5" else open_switchers += "switchsws5"
          case "swauxg1busPin" => if (readVal == 1) open_switchers += "switchswp6" else open_switchers += "switchsws6"
          case "swm1Pin" => if (readVal == 1) open_switchers += "switchsw1"
          case "swm2Pin" => if (readVal == 1) open_switchers += "switchsw2"
          case "swL1Pin" => if (readVal == 1) open_switchers += "switchsw3"
          case "swL2Pin" => if (readVal == 1) open_switchers += "switchsw4"
          case "swmg1Pin" => if (readVal == 1) open_switchers += "switchswmg1"
          case "swauxg1Pin" => if (readVal == 1) open_switchers += "switchswauxg1"
        }
      }

      println(open_switchers)
      if (circuit_monitor != null)
        circuit_monitor ! UpdateScenario(open_switchers)

      Thread.sleep(2000)
      self ! SwitcherMonitoring()


    case RequestUpdateScenario() =>
      circuit_monitor = sender()


    case _ => println("Received unknown msg ")

  }

}
  object SwitcherMonitor {
    def main(args: Array[String]) {
      //get the configuration file from classpath
      val configFile = getClass.getClassLoader.getResource("resources/switcher_monitor_application.conf").getFile
      //parse the config
      val config = ConfigFactory.parseFile(new File(configFile))
      //create an actor system with that config
      val system = ActorSystem("RemoteSystem", config)
      //create a remote actor from actorSystem
      val remote = system.actorOf(Props[SwitcherMonitor], name = "switcher")
      println("remote is ready")


    }
  }

