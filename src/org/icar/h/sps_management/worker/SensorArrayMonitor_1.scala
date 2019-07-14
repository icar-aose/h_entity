package org.icar.h.sps_management.worker

import java.io.File

import akka.actor._
import com.typesafe.config.ConfigFactory
import org.icar.h.sps_management.rpi_ina219._

class SensorArrayMonitor_1 extends Actor with ActorLogging {

  var current : Array[Double] = new Array[Double](4)
  var data : AmpData = AmpData(current)

  var data_fetch : Int = 0

  private val SensorMonitor1 : ActorRef = context.actorOf(SensorMonitor.props(INA219.Address.ADDR_40,0), "sensor_monitor1")
  private val SensorMonitor2 : ActorRef = context.actorOf(SensorMonitor.props(INA219.Address.ADDR_41,0), "sensor_monitor2")
  private val SensorMonitor3 : ActorRef = context.actorOf(SensorMonitor.props(INA219.Address.ADDR_44,0), "sensor_monitor3")
  private val SensorMonitor4 : ActorRef = context.actorOf(SensorMonitor.props(INA219.Address.ADDR_45,0), "sensor_monitor4")

  var circuit_mon : ActorRef = _

  override def preStart() : Unit = {

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

    case AmpValue(value,adr, index_rasp) =>
      {
        data_fetch+=1


        adr match {
          case INA219.Address.ADDR_40 => data.setCurrent(value,0)
          case INA219.Address.ADDR_41 => data.setCurrent(value,1)
          case INA219.Address.ADDR_44 => data.setCurrent(value,2)
          case INA219.Address.ADDR_45 => data.setCurrent(value,3)
        }

        if(data_fetch==4)
          {
            data_fetch = 0
            circuit_mon ! RaspDataVal(data,index_rasp)

          }
      }


    case _ => println("Received unknown msg ")
  }
}


object SensorArrayMonitor_1{
  def main(args: Array[String]) {
    //get the configuration file from classpath
    val configFile = getClass.getClassLoader.getResource("resources/sensor_application.conf").getFile
    //parse the config
    val config = ConfigFactory.parseFile(new File(configFile))
    //create an actor system with that config
    val system = ActorSystem("RemoteSystem" , config)
    //create a remote actor from actorSystem
    val remote = system.actorOf(Props[SensorArrayMonitor_1], name="sensor_1")
    println("remote is ready")


  }
}



