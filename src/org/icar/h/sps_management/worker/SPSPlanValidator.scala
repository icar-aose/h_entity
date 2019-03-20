package org.icar.h.sps_management.worker

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.icar.h.Akka2Jade;
import file_transfer_sps._

object SPSPlanValidator {
   def props(bridge : Akka2Jade, worker_sps : ActorRef) : Props = Props(classOf[SPSPlanValidator],bridge,worker_sps)
}

class SPSPlanValidator(val bridge : Akka2Jade, worker_sps : ActorRef) extends Actor with ActorLogging {

  override def preStart : Unit = {
      log.info("ready")
    }
    
    override def receive: Receive = {
      case Validate( plan ) =>
        log.info("i'm the validator, now contacting the sps reconfigurator for obtaining the solution: "+plan)
        worker_sps ! GetSolution(plan)

      case Solution(plan_ref,plan) =>
        Thread.sleep(2000)


//        var x : SimpleClient = new SimpleClient
//        var arg : Array[String] =  new Array[String](2)
//        arg(0)="upload"
//        arg(1)="circuito3_exp2_4guasti_sc1_v0.slx"
//        x.transfer(arg)



        log.info("Validator: executing Matlab script with the solution "+plan_ref)
        bridge.sendHead("selected("+plan_ref+")")

      case _ =>
        println("Validator: unspecied message")
    }
}