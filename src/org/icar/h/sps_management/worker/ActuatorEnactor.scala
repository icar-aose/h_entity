package org.icar.h.sps_management.worker

import java.io.File
import java.util

import akka.actor._
import cartago.{ArtifactId, Op}
import cartago.util.agent.CartagoBasicContext
import com.typesafe.config.ConfigFactory
import org.icar.h.sps_management.EvaluateSol
import org.icar.h.sps_management.artifact.SwitcherArtifact

class ActuatorEnactor extends Actor with ActorLogging {

  var my_context : CartagoBasicContext = new CartagoBasicContext("my_agent")
  var enactArt : SwitcherArtifact = new SwitcherArtifact
  var  my_device : ArtifactId = _

  my_device = my_context.makeArtifact("switcher", "org.icar.h.sps_management.artifact.SwitcherArtifact")

  override def preStart() : Unit = {

    log.info("Ready")
  }


  override def receive: Receive = {

    case EnactPlan(plan_reference,plan) =>
      {
        var acts : util.ArrayList[String] = EvaluateSol.solution_list(plan)

        my_context.doAction(my_device, new Op("actPlan",plan_reference,acts))
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


