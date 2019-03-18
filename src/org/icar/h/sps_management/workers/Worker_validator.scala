package org.icar.h.sps_management.workers

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.icar.h.Akka2Jade;


object Worker_validator {
   def props(bridge : Akka2Jade, worker_sps : ActorRef) : Props = Props(classOf[Worker_validator],bridge,worker_sps)  
}

class Worker_validator(val bridge : Akka2Jade, worker_sps : ActorRef) extends Actor with ActorLogging {
    var sol : String = null

  override def preStart : Unit = {
      log.info("ready")
    }
    
    override def receive: Receive = {
        case x : String ⇒
          if(x.startsWith("sol"))
          {
            log.info("i'm validator, now contact worker sps reconfigurator for require the solution: "+x)
            sol = x
            worker_sps ! x
          }
          else 
          { Thread.sleep(2000)
            log.info("Validator: Matlab test with solution "+x)
            bridge.sendHead("selected("+sol+")")
          }
        //case "
        
        case _ ⇒ println("unspecied message")
    }
}