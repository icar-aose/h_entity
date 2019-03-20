package org.icar.h.Template

import akka.actor.{Actor, ActorLogging, Props}

object SubWorker {
   def props : Props = Props(classOf[SubWorker])  
}

class SubWorker extends Actor with ActorLogging {
  
    override def preStart : Unit = {
      log.info("subworker  ready")
    }
    
    override def receive: Receive = {
        case x : String ⇒ println("received: "+x)
        case _ ⇒ println("unspecied message")
    }

  
}