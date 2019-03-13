package org.icar.h.sps_management;

import org.icar.h.Akka2Jade;
import org.icar.h.sps_management.workers.*;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class WorkerSystem extends Agent {
	final private ActorSystem system = ActorSystem.create("worker-system");
	private ActorRef worker_check = null;
	private ActorRef worker_sps = null;
	private ActorRef worker_validator = null;
	private ActorRef worker_plan = null;
	
	  protected void setup() {
		  	System.out.println("I am the workers representative and i create->worker_check,worker_sps,worker_validator,worker_plan");
		  	
		  	Akka2Jade bridge = new Akka2Jade(this);
		  	
		  	//worker for check failure
		    worker_check = system.actorOf(Worker_check.props(bridge), "worker_check");
		    
		    //worker for sps_reconfigurator
		    worker_sps = system.actorOf(Worker_sps.props(bridge), "worker_sps");
		    
		    //worker for validator matlab
		    worker_validator = system.actorOf(Worker_validator.props(bridge,worker_sps), "worker_validator");
		    
		    //worker for plan enactor
		    worker_plan = system.actorOf(Worker_plan.props(bridge,worker_sps), "worker_plan");
		    
		    addBehaviour(new ForwardIncomingMessages());
	  }
	  
	  @SuppressWarnings("serial")
	private class ForwardIncomingMessages extends CyclicBehaviour {
		  
		@Override
		public void action() {
				
			  int indB = -1;
			  int indF = -1;
			  String element = null;
			  ACLMessage msg = myAgent.receive();
			  if (msg != null) {

				  String content = msg.getContent();
				  
				  indB = content.indexOf('(');
				  indF = content.indexOf(')');
				  
				  if(indB!=-1) 
				  {
					element = content.substring(indB+1, indF);  
					content = content.substring(0,indB);
				  }

				  //System.out.println("new Message!:>>"+content);
				  //if(element != null)
					//  System.out.println("new Element!:>>"+element);
				  switch (content)
				  {
				  case "check_failure":
					  //System.out.println("i'm in check\n");
					  worker_check.tell(content,null);
					  break;
				  
				  case "sps_reconfigurator":
					  worker_sps.tell(element, null);
					  break;
					  
				  case "validator":
					  worker_validator.tell(element, null);
					  break;
					  
				  case "enact":
					  worker_plan.tell(element, null);
					  break;
				  }
				//  worker_check.tell(content,null);
				 // worker_sps.tell(content,null);
				 // worker_validator.tell(content,null);
				  
			 }
		  }
		  
	  }
	  
}
