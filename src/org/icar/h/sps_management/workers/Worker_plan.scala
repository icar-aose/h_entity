package org.icar.h.sps_management.workers

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.icar.h.Akka2Jade;


object Worker_plan {
   def props(bridge : Akka2Jade,worker_sps : ActorRef) : Props = Props(classOf[Worker_plan],bridge,worker_sps)  
}

class Worker_plan(val bridge : Akka2Jade,worker_sps : ActorRef) extends Actor with ActorLogging {
   var sol : String = null

  override def preStart : Unit = {
      log.info("ready")
    }
    
    override def receive: Receive = {
        case x : String ⇒
           if(x.startsWith("sol"))
          {
            println("i'm plan enactor, now contact worker_sps for require the plan for solution: "+x)
            sol = x
            worker_sps ! x
          }
          else 
          {
            println("Now enact: "+x)
            Thread.sleep(2000)
          }
        case _ ⇒ println("unspecied message")
    }
}