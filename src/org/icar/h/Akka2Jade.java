package org.icar.h;


import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class Akka2Jade {
	private Agent myAgent;
	
	public Akka2Jade(Agent myAgent) {
		this.myAgent = myAgent;
	}
	
	public void sendHead(String msg) {
		myAgent.addBehaviour( new SendToHead(msg));
	}
	
	  @SuppressWarnings("unused")
	private class SendToHead extends OneShotBehaviour {
		  private String content = null;
		  
		public SendToHead(String content) {
			this.content = content;
		}

		@Override
		public void action() {
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(new AID("head",AID.ISLOCALNAME));
			msg.setLanguage("h-language");
			msg.setContent(content);
			//System.out.println("sent");
			myAgent.send(msg);
		}
		  
	  }

}