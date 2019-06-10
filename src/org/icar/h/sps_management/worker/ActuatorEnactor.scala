package org.icar.h.sps_management.worker

import java.io.File
import java.util
import processing.io._
import akka.actor._
import com.typesafe.config.ConfigFactory
import org.icar.h.sps_management.EvaluateSol

import org.icar.h.sps_management.artifact.EnactArtifact

class ActuatorEnactor extends Actor with ActorLogging {

  var enactArt : EnactArtifact = new EnactArtifact

  var swm1Pin =27  //Relais 1 motore 1
  var swm1busPin=17 //Relais 2 motore 1
  var swm2Pin=25 //Relais 1 motore 2
  var swm2busPin=13//Relais 2 motore 2
  var swmg1Pin=6 //Relais1 main gen 1
  var swmg1busPin=5 //Relais2 main gen 1
  var swL1Pin=20 //Relais 1 Load 1
  var swL1busPin=16 //Relais 2 Load 1
  var swL2Pin=12 //Relais 1 Load 2
  var swL2busPin=21 //Relais 2 Load 2
  var swL2plusPin=23 //Relais 3 plus Load 2
  var swauxg1Pin=26 //Relais1 aux gen 1
  var swauxg1busPin=19 //Relais2 aux gen 1


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
  GPIO.pinMode(swL2plusPin, GPIO.OUTPUT)
  GPIO.pinMode(swL2busPin, GPIO.OUTPUT)

 // GPIO.digitalWrite(swm1Pin, GPIO.HIGH)


  override def preStart() : Unit = {

    log.info("Ready")
  }
   
  override def receive: Receive = {

    case EnactPlan(plan_reference,plan) =>
      {
        var acts : util.ArrayList[String] = EvaluateSol.solution_list(plan)

        enactArt.actPlan(plan_reference,acts)

      }



    case _ => println("Received unknown msg ")
  }
}


object ActuatorEnactor{
  def main(args: Array[String]) {
    //get the configuration file from classpath
    val configFile = getClass.getClassLoader.getResource("org/icar/h/sps_management/worker/actuator_application.conf").getFile
    //parse the config
    val config = ConfigFactory.parseFile(new File(configFile))
    //create an actor system with that config
    val system = ActorSystem("RemoteSystem" , config)
    //create a remote actor from actorSystem
    val remote = system.actorOf(Props[ActuatorEnactor], name="actuator")
    println("remote is ready")


  }
}


