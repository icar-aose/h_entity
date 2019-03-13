package org.icar.h.sps_management.workers

import akka.actor.{Actor, ActorLogging, Props}
import org.icar.h.Akka2Jade;
import cartago.ArtifactId;
import cartago.CartagoException;
import cartago.Op;
import cartago.util.agent.CartagoBasicContext;

import cartago.events._;
import cartago.util.agent._;

object Worker_check {
   def props(bridge : Akka2Jade) : Props = Props(classOf[Worker_check],bridge)  
}

class Worker_check(val bridge : Akka2Jade) extends Actor with ActorLogging {
      
  var my_context : CartagoBasicContext = new CartagoBasicContext("my_agent")
	var my_device : ArtifactId = null
	var p : Percept  = null
		  
		  override def preStart : Unit = {
        log.info("ready")
        try
        {
          my_device = my_context.makeArtifact("device", "org.icar.h.Device");
		     // my_context.doAction(my_device, new Op("welcome"));
		    } 
        catch
        {
		      case e : CartagoException =>
		        e.printStackTrace();
		     }
        
          my_context.focus(my_device)

      }
    
      override def receive: Receive = {
        case "check_failure" ⇒
          println("i'm worker check failure!\n")
				  var p :Percept = null
				  do
				  {
				    //println("tick")
			 	    p = my_context.waitForPercept()
		        log.info("percept: "+p.getSignal);
				  }
				  while (!p.hasSignal());
				  bridge.sendHead("failure(f1)")
          
        case _ ⇒ println("unspecied message")
    }
}