package org.icar.h.Template

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.icar.h.Akka2Jade
object Worker {
   def props(bridge : Akka2Jade) : Props = Props(classOf[Worker],bridge)  
}

class Worker(val bridge : Akka2Jade) extends Actor with ActorLogging {
  var child_actor : ActorRef = null

  override def preStart : Unit = {
      log.info("ready")
      child_actor = context.actorOf(SubWorker.props, "SubWorker")
    }
    
    override def receive: Receive = {
        case x : String ⇒ 
          child_actor ! x
          bridge.sendHead("alive")
        case _ ⇒ println("unspecied message")
    }
}