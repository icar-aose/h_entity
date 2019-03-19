package org.icar.h.sps_management.worker

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.icar.h.Akka2Jade;


object ReconfigurationEnactor {
   def props(bridge : Akka2Jade,worker_sps : ActorRef) : Props = Props(classOf[ReconfigurationEnactor],bridge,worker_sps)
}

class ReconfigurationEnactor(val bridge : Akka2Jade, worker_sps : ActorRef) extends Actor with ActorLogging {


  override def preStart : Unit = {
      log.info("ready")
    }
    
    override def receive: Receive = {
      case Enact(plan_reference) =>
        log.info("i'm plan enactor, now contact worker_sps for require the plan for solution: "+plan_reference)
        worker_sps ! GetSolution(plan_reference)

      case Solution(plan_reference,plan) =>
        log.info("Now enacting: "+plan_reference)
        Thread.sleep(2000)

      case _ =>
        println("Enactor: unspecied message")
    }
}