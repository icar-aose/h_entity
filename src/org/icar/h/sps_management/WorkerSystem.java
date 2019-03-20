package org.icar.h.sps_management;

import org.icar.h.Akka2Jade;
import org.icar.h.Worker;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
public class WorkerSystem extends Agent {
	final private ActorSystem system = ActorSystem.create("worker-system");
	private ActorRef root = null;

	  protected void setup() {
		  	System.out.println("I am the jade/akka wrapper");
		  	
		  	Akka2Jade bridge = new Akka2Jade(this);

		  	root = system.actorOf(Root.props(bridge), "root-worker");

		    addBehaviour(new ForwardIncomingMessages());
	  }
	  
	  @SuppressWarnings("serial")
	private class ForwardIncomingMessages extends CyclicBehaviour {
		  
		@Override
		public void action() {

			ACLMessage msg = myAgent.receive();
			if (msg != null) {
				String content = msg.getContent();
				//System.out.println("received and forwaded: "+content);

				root.tell(content, null);
			}

		  }
		  
	  }
	  
}
