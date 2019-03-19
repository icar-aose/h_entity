package org.icar.h.sps_management;

import java.io.File;

import cartago.CartagoException;
import cartago.CartagoService;
import cartago.NodeId;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import jason.infra.jade.JadeAgArch;
import jason.mas2j.AgentParameters;


public class Boot {

	public static void main(String args[]) {

		// Start a new JADE runtime system		
		Runtime.instance().setCloseVM(true);		//	Causes the local JVM to be closed when the last container in this JVM terminates
		
		// Start a main-container on the localhost		 
		Properties props = new ExtendedProperties();
		props.setProperty(Profile.GUI, "true");
		ProfileImpl p = new ProfileImpl(props);
		AgentContainer ac = Runtime.instance().createMainContainer(p); //* RMI internal Message Transport Protocol, port number 1099, HTTP MTP.
		
		
		try {
			NodeId node = CartagoService.startNode();
			CartagoService.installInfrastructureLayer("default");

			
		//	AgentController diplomat  = ac.createNewAgent("diplomat", Diplomat.class.getName(), new Object[] {} );
		//	diplomat.start();

			AgentParameters ap = new AgentParameters();
            ap.asSource = new File("src/org/icar/h/sps_management/head.asl");
			AgentController head = ac.createNewAgent("head", JadeAgArch.class.getName(), new Object[] { ap });


			AgentController workersystem  = ac.createNewAgent("workersystem", WorkerSystem.class.getName(), new Object[] {} );
			workersystem.start();
			head.start();

		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CartagoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	
}
