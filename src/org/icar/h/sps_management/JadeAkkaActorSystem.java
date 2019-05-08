package org.icar.h.sps_management;

import akka.actor.BootstrapSetup;
import com.typesafe.config.Config;
import org.icar.h.core.Akka2Jade;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;


import java.io.File;
import java.lang.*;
import java.util.ResourceBundle;

import com.typesafe.config.ConfigFactory;

@SuppressWarnings("serial")
public class JadeAkkaActorSystem extends Agent {


	private ActorRef root = null;
	ActorSystem system = null;
	  protected void setup() {

		  String remote = ResourceBundle.getBundle("org.icar.h.sps_management.Boot").getString("remote");
		  /* config file Akka Remote*/
		  //THE ACTOR SYSTEM IS CREATE HERE
		  if(remote.equals("true"))
		  {
			   String configFile = getClass().getClassLoader().getResource("resources/local_application.conf").getFile();
			   Config config = ConfigFactory.parseFile(new File(configFile));

			   system = ActorSystem.create("worker-system",config);
		  }
		  else
		  	system = ActorSystem.create("worker-system");

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
