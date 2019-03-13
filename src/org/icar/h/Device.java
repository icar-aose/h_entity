package org.icar.h;


import cartago.Artifact;
import cartago.OPERATION;

public class Device extends Artifact {
	
	void init() {
		System.out.println("I am an artifact");
		//defineObsProperty("count",0);
		execInternalOp("add");

	}
	@OPERATION
	void add() throws InterruptedException {

			Thread.sleep(5000);
    	//	ObsProperty prop = getObsProperty("count");
    	//	prop.updateValue(prop.intValue()+1);
    	//	System.out.println(prop);
    		signal("tick");
        
	}


}
