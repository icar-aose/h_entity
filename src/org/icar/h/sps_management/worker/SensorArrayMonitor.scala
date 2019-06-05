package org.icar.h.sps_management.worker

import java.io.File

import akka.actor._
import com.typesafe.config.ConfigFactory
import org.icar.h.sps_management.rpi_ina219._
import processing.io._

class SensorArrayMonitor extends Actor with ActorLogging {

  var current : Array[Double] = new Array[Double](4)
  var data : AmpData = AmpData(current)

  private val SensorMonitor1 : ActorRef = context.actorOf(SensorMonitor.props(INA219.Address.ADDR_40), "sensor_monitor1")
  private val SensorMonitor2 : ActorRef = context.actorOf(SensorMonitor.props(INA219.Address.ADDR_41), "sensor_monitor2")
  private val SensorMonitor3 : ActorRef = context.actorOf(SensorMonitor.props(INA219.Address.ADDR_44), "sensor_monitor3")
  private val SensorMonitor4 : ActorRef = context.actorOf(SensorMonitor.props(INA219.Address.ADDR_45), "sensor_monitor4")

  var swm1Pin=27; //Relais 1 motore
  var swm1busPin=17; //Relais 2 motore
  var swmg1Pin=6; //Relais1 main gen 1
  var swmg1busPin=5; //Relais2 main gen 1
  var swL1Pin=20; //Relais 1 Load 1
  var swL1busPin=16; //Relais 2 Load 1
  var circuit_mon : ActorRef = _

  override def preStart() : Unit = {

    GPIO.pinMode(swm1Pin, GPIO.OUTPUT)
    GPIO.pinMode(swm1busPin, GPIO.OUTPUT)
    GPIO.pinMode(swmg1Pin, GPIO.OUTPUT)
    GPIO.pinMode(swmg1busPin, GPIO.OUTPUT)
    GPIO.pinMode(swL1Pin, GPIO.OUTPUT)
    GPIO.pinMode(swL1busPin, GPIO.OUTPUT)

    //inizializzazione stato delle uscite (questi relais funzionano in logica negata; uscita alta= relais spento cioè su nc e viceversa)

    GPIO.digitalWrite(swm1Pin, GPIO.HIGH)
    GPIO.digitalWrite(swm1busPin, GPIO.HIGH)
    GPIO.digitalWrite(swmg1Pin, GPIO.LOW)
    GPIO.digitalWrite(swmg1busPin, GPIO.HIGH)
    GPIO.digitalWrite(swL1Pin, GPIO.LOW)
    GPIO.digitalWrite(swL1busPin, GPIO.HIGH)

    log.info("Ready")
  }
   
  override def receive: Receive = {
    case StartCheckMonitor() =>
    {
      SensorMonitor1 ! Check()
      SensorMonitor2 ! Check()
      SensorMonitor3 ! Check()
      SensorMonitor4 ! Check()
      circuit_mon  = sender()
    }

    case AmpValue(value,adr) =>
      {
        adr match {
          case INA219.Address.ADDR_40 => data.setCurrent(value,0)
          case INA219.Address.ADDR_41 => data.setCurrent(value,1)
          case INA219.Address.ADDR_44 => data.setCurrent(value,2)
          case INA219.Address.ADDR_45 => data.setCurrent(value,3)
        }
      circuit_mon ! RaspDataVal(data)
      }


    case _ => println("Received unknown msg ")
  }
}


object SensorArrayMonitor{
  def main(args: Array[String]) {
    //get the configuration file from classpath
    val configFile = getClass.getClassLoader.getResource("org/icar/h/sps_management/worker/sensor_application.conf").getFile
    //parse the config
    val config = ConfigFactory.parseFile(new File(configFile))
    //create an actor system with that config
    val system = ActorSystem("RemoteSystem" , config)
    //create a remote actor from actorSystem
    val remote = system.actorOf(Props[SensorArrayMonitor], name="remote")
    println("remote is ready")


  }
}



