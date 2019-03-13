package org.icar.h;

import cartago.ArtifactId;
import cartago.CartagoException;
import cartago.Op;
import cartago.util.agent.CartagoBasicContext;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class Diplomat extends Agent {
	//private MessageTemplate template = MessageTemplate.and(
	//		MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF),
	//		MessageTemplate.MatchOntology("presence") );
	
	  protected void setup() {
	  	System.out.println("Hello World! My name is "+getLocalName());
	  	System.out.println("I am the diplomat");
	  	
		//addBehaviour(new mySend());
	  	
		CartagoBasicContext my_context = new CartagoBasicContext("my_agent");
		ArtifactId my_device;
		try {
			my_device = my_context.makeArtifact("device", "org.icar.h.Device");
			my_context.doAction(my_device, new Op("welcome"));
			my_context.focus(my_device);
		} catch (CartagoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	  
	  private class mySend extends OneShotBehaviour {
			public void action() {
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.setContent("alive");
				msg.addReceiver(new AID("head", AID.ISLOCALNAME));
				
				myAgent.send(msg);
				
				addBehaviour(new WakerBehaviour(myAgent, 5000) {
					protected void onWake() {
						addBehaviour(new mySend());
					}
				} );
			}
	  }
	  
}