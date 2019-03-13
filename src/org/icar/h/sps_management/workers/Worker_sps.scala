package org.icar.h.sps_management.workers

import scala.collection.mutable.HashMap
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import org.icar.h.Akka2Jade;

object Worker_sps {
   def props(bridge : Akka2Jade) : Props = Props(classOf[Worker_sps],bridge)  
}

class Worker_sps(val bridge : Akka2Jade) extends Actor with ActorLogging {
   val hashMap: HashMap[String, String] = HashMap.empty[String,String]

  override def preStart : Unit = {
      log.info("ready")
    }
    
    override def receive: Receive = {
        case x : String ⇒
          if(x.startsWith("f"))
          {
            Thread.sleep(2000)    //find a solution
            println("i'm sps reconfigurator, find solution for the failures: "+ x+"\n")
            hashMap += ("solution1" -> "cap1_cap2_cap3")
            bridge.sendHead("discovered(solution1)")
          }
          else 
          {
            val x  = hashMap("solution1")
            sender() ! x
          }
        case _ ⇒ println("unspecied message")
    }
}