package org.icar.h.sps_management.artifact;

import cartago.Artifact;
import cartago.OPERATION;
import processing.io.*;

import java.util.ArrayList;


public class SwitcherArtifact extends Artifact {


	int swm1Pin =27; //Relais 1 motore 1
	int swm1busPin=17; //Relais 2 motore 1
	int swm2Pin=25; //Relais 1 motore 2
	int swm2busPin=13;//Relais 2 motore 2
	int swmg1Pin=6; //Relais1 main gen 1
	int swmg1busPin=5; //Relais2 main gen 1
	int swL1Pin=20; //Relais 1 Load 1
	int swL1busPin=16; //Relais 2 Load 1
	int swL2Pin=12; //Relais 1 Load 2
	int swL2busPin=21; //Relais 2 Load 2
	int swL2plusPin=23; //Relais 3 plus Load 2
	int swauxg1Pin=26; //Relais1 aux gen 1
	int swauxg1busPin=19; //Relais2 aux gen 1

	void init () {

		GPIO.pinMode(swm1Pin, GPIO.OUTPUT);
		GPIO.pinMode(swm1busPin, GPIO.OUTPUT);
		GPIO.pinMode(swm2Pin, GPIO.OUTPUT);
		GPIO.pinMode(swm2busPin, GPIO.OUTPUT);
		GPIO.pinMode(swmg1Pin, GPIO.OUTPUT);
		GPIO.pinMode(swmg1busPin, GPIO.OUTPUT);
		GPIO.pinMode(swauxg1Pin, GPIO.OUTPUT);
		GPIO.pinMode(swauxg1busPin, GPIO.OUTPUT);
		GPIO.pinMode(swL1Pin, GPIO.OUTPUT);
		GPIO.pinMode(swL1busPin, GPIO.OUTPUT);
		GPIO.pinMode(swL2Pin, GPIO.OUTPUT);
		GPIO.pinMode(swL2plusPin, GPIO.OUTPUT);
		GPIO.pinMode(swL2busPin, GPIO.OUTPUT);
	}


	@OPERATION
	public void actPlan(String plan_reference, ArrayList acts )  {

		if(GPIO.digitalRead(swmg1Pin)==0)
		{
			System.out.println("generatore attivo, piano: "+plan_reference);
			for(int i =0 ; i< acts.size();i++)
				System.out.println(acts.get(i));

		}




	}

	// GPIO.digitalWrite(swm1Pin, GPIO.HIGH)


}



