package org.icar.h.sps_management.worker

import java.io.File

import akka.actor._
import cartago.util.agent.CartagoBasicContext
import cartago.{CartagoService, NodeId, Op}
import com.typesafe.config.ConfigFactory
import processing.io.GPIO

import scala.collection.mutable.ArrayBuffer
class FaultEnactor extends Actor with ActorLogging {

  val node: NodeId = CartagoService.startNode
  var my_context : CartagoBasicContext = new CartagoBasicContext("remote_agent")
  var my_device = my_context.makeArtifact("faultArtifact", "org.icar.h.sps_management.artifact.FaultArtifact")
  private val swf1Pin = 26
  private val swf2Pin = 13

  override def preStart() : Unit = {

    log.info("Ready")
  }


  override def receive: Receive = {


    case EnactFault(fault) =>
    {
      //println("abilito il fault")
      my_context.doAction(my_device, new Op("actFault",fault))
    }

    case StatusFault() =>
      var fault : ArrayBuffer[String] = new ArrayBuffer[String]
      if(GPIO.digitalRead(swf1Pin)==0)
        fault += "switchf1"
      if(GPIO.digitalRead(swf2Pin)==0)
        fault += "switchf2"
      sender() ! UpdateFault(fault)





    case _ => println("Received unknown msg ")
  }
}


object FaultEnactor{
  def main(args: Array[String]) {
    //get the configuration file from classpath
    val configFile = getClass.getClassLoader.getResource("resources/fault_application.conf").getFile
    //parse the config
    val config = ConfigFactory.parseFile(new File(configFile))
    //create an actor system with that config
    val system = ActorSystem("RemoteSystem" , config)
    //create a remote actor from actorSystem
    val remote = system.actorOf(Props[FaultEnactor], name="fault")
    println("remote is ready")


  }
}


