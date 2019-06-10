package org.icar.h.sps_management.worker

import java.io.File
import java.util

import akka.actor._
import cartago.{ArtifactId, CartagoService, NodeId, Op}
import cartago.util.agent.CartagoBasicContext
import com.typesafe.config.ConfigFactory
import org.icar.h.sps_management.EvaluateSol
import org.icar.h.sps_management.artifact.SwitcherArtifact

class ActuatorEnactor extends Actor with ActorLogging {

  val node: NodeId = CartagoService.startNode
  var my_context : CartagoBasicContext = new CartagoBasicContext("remote_agent")
  var my_device = my_context.makeArtifact("switcherArtifact", "org.icar.h.sps_management.artifact.SwitcherArtifact")

  override def preStart() : Unit = {

    log.info("Ready")
  }


  override def receive: Receive = {

    case EnactPlan(plan_reference,plan) =>
      {
        my_context.doAction(my_device, new Op("actPlan",plan_reference,plan))
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


