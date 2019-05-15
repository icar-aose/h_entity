package org.icar.h.sps_management.worker


import processing.io._
import java.io.File

import akka.actor._
import com.typesafe.config.ConfigFactory

/**
  * Remote actor which listens on port 5150
  */
  
  
class RaspActorCheckFailureTest extends Actor {
   val ledPin = 17

   override def preStart() : Unit = {
    
    GPIO.pinMode(ledPin, GPIO.OUTPUT)
    GPIO.digitalWrite(ledPin, GPIO.HIGH)
    Thread.sleep(1000)
    GPIO.digitalWrite(ledPin, GPIO.LOW)
   }
   
  override def receive: Receive = {
    case msg: String => {
      println("remote received " + msg + " from " + sender)
      GPIO.digitalWrite(ledPin, GPIO.HIGH)
      Thread.sleep(10000)
      sender ! 10.10
    }
    case _ => println("Received unknown msg ")
  }
}

object RaspActorCheckFailureTest{
 def main(args: Array[String]) {
   //get the configuration file from classpath
   val configFile = getClass.getClassLoader.getResource("remote_application.conf").getFile
   //parse the config
   val config = ConfigFactory.parseFile(new File(configFile))
   //create an actor system with that config
   val system = ActorSystem("RemoteSystem" , config)
   //create a remote actor from actorSystem
   val remote = system.actorOf(Props[RaspActorCheckFailureTest], name="remote")
   println("remote is ready")


 }
}


