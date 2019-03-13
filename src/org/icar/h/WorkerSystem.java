package org.icar.h;



import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class WorkerSystem extends Agent {
	final private ActorSystem system = ActorSystem.create("worker-system");
	private ActorRef worker = null;
	
	  protected void setup() {
		  	System.out.println("I am the workers representative");
		  	
		  	Akka2Jade bridge = new Akka2Jade(this);
		    worker = system.actorOf(Worker.props(bridge), "root-worker");
		    
		    addBehaviour(new ForwardIncomingMessages());
	  }
	  
	  @SuppressWarnings("serial")
	private class ForwardIncomingMessages extends CyclicBehaviour {
		  
		@Override
		public void action() {
			  
			  ACLMessage msg = myAgent.receive();
			  if (msg != null) {
				  String content = msg.getContent();
				  worker.tell(content, null);
				  System.out.println("suca");
			 }
		  }
		  
	  }
	  
}
