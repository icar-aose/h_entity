package org.icar.h.sps_management.worker

import java.io.File
import java.util
import java.util.ResourceBundle

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSelection, ActorSystem, Props}
import org.icar.h.core.Akka2Jade
import org.icar.h.sps_management.EvaluateSol


object ReconfigurationEnactor {
   def props(bridge : Akka2Jade,worker_sps : ActorRef,sps_reconfigurator : ActorRef) : Props = Props(classOf[ReconfigurationEnactor],bridge,worker_sps,sps_reconfigurator)


class ReconfigurationEnactor(val bridge : Akka2Jade, worker_sps : ActorRef, sps_reconfigurator : ActorRef) extends Actor with ActorLogging {

  var actuator : String = ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("actuator.actor")
  var ActuatorActor : ActorSelection = null

  if(actuator.equals("true")) {
    ActuatorActor = context.actorSelection("akka.tcp://RemoteSystem@"+ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("actuator.actor.ip")+":5150/user/actuator") //IP of the PC remote
    println("That 's remote:" + ActuatorActor)
  }


  override def preStart : Unit = {
      log.info("ready")
    }
    
    override def receive: Receive = {
      case Enact(plan_reference) =>
        log.info("i'm plan enactor, now contact worker_sps for require the plan for solution: "+plan_reference)
        worker_sps ! GetPlan(plan_reference)
        worker_sps ! StopAll()
        sps_reconfigurator ! StopAll()

      case Plan(plan_reference,plan) =>
        log.info("Now enacting: "+plan_reference)
        //artifact for the execution of plan!!
        var acts : util.ArrayList[String] = EvaluateSol.solution_list(plan)
        if(actuator.equals("true"))
          ActuatorActor ! EnactPlan(plan_reference,acts)
        else log.info("Enacted")


      case _ =>
        println("Enactor: unspecified message")
    }
}

}