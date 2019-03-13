package org.icar.h
import akka.actor.{Actor, ActorLogging, ActorRef, Props}

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