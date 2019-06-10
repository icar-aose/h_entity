package org.icar.h.sps_management.artifact;

import cartago.Artifact;
import cartago.OPERATION;
import processing.io.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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

	Map<String,String> SwitchNamePin = new HashMap();
	Map<String, Integer> NamePinNum = new HashMap();
	String act = "";


	int flagMg1 = 0;
	int flagAuxg1 = 0;

	void init () {

		SwitchNamePin.put("switchsw1","swm1Pin");
		SwitchNamePin.put("switchsw2","swm2Pin");
		SwitchNamePin.put("switchsw3","swL1Pin");
		SwitchNamePin.put("switchsw4","swL2Pin");
		SwitchNamePin.put("switchsws1","swm1busPin");
		SwitchNamePin.put("switchswp1","swm1busPin");
		SwitchNamePin.put("switchsws2","swm2busPin");
		SwitchNamePin.put("switchswp2","swm2busPin");
		SwitchNamePin.put("switchsws3","swmg1busPin");
		SwitchNamePin.put("switchswp3","swmg1busPin");
		SwitchNamePin.put("switchsws4","swL1busPin");
		SwitchNamePin.put("switchswp4","swL1busPin");
		SwitchNamePin.put("switchsws5","swL2busPin");
		SwitchNamePin.put("switchswp5","swL2busPin");
		SwitchNamePin.put("switchsws6","swauxg1busPin");
		SwitchNamePin.put("switchswp6","swauxg1busPin");
		SwitchNamePin.put("switchswmg1","swmg1Pin");
		SwitchNamePin.put("switchswauxg1","swauxg1Pin");

		NamePinNum.put("swm1Pin",27); //Relais 1 motore 1
		NamePinNum.put("swm1busPin",17); //Relais 2 motore 1
		NamePinNum.put("swm2Pin",25);
		NamePinNum.put("swm2busPin",13);
		NamePinNum.put("swmg1Pin",6);
		NamePinNum.put("swmg1busPin",5);
		NamePinNum.put("swL1Pin",20);
		NamePinNum.put("swL1busPin",16);
		NamePinNum.put("swL2Pin",12);
		NamePinNum.put("swL2busPin",21);
		NamePinNum.put("swauxg1Pin",26);
		NamePinNum.put("swauxg1busPin",19);



	}

	//NB: se i flag valgono 0, allora il generatore è acceso altrimenti spento (logica negata dei relays)

	@OPERATION
	public void actPlan(String plan_reference, ArrayList acts )  {

		System.out.println("piano: "+plan_reference);
		for(int i =0 ; i< acts.size();i++)
			System.out.println(acts.get(i));

		flagMg1 = GPIO.digitalRead(swmg1Pin);
		flagAuxg1 = GPIO.digitalRead(swauxg1Pin);

		GPIO.digitalWrite(swmg1Pin,1);   //shutdown the generator mg1
		GPIO.digitalWrite(swauxg1Pin,1);   //shutdown the generator auxg1

		for(int i =0 ; i< acts.size();i++)
		{
			act = SwitchNamePin.get(acts.get(i));
			switch(act)
			{
				case "swmg1Pin":
					if(Integer.parseInt(SwitchNamePin.get(acts.get(i+1)))==0)
						flagMg1 = 1;
					else flagMg1 = 0;
					break;
				case "swauxg1Pin":
					if(Integer.parseInt(SwitchNamePin.get(acts.get(i+1)))==0)
						flagAuxg1 = 1;
					else flagAuxg1 = 0;
					break;
				default:
					if(act.contains("bus"))
					{
						//GPIO.digitalWrite(NamePinNum.get(act),Integer.parseInt(SwitchNamePin.get(acts.get(i+1))));
						System.out.println(NamePinNum.get(act)+" "+" "+Integer.parseInt(SwitchNamePin.get(acts.get(i+1))));
						i=i+4;
					} else
						{
							System.out.println(NamePinNum.get(act)+" "+" "+Integer.parseInt(SwitchNamePin.get(acts.get(i+1))));
						}

			}

		}



		GPIO.digitalWrite(swmg1Pin,flagMg1);   //turn on(depends of the result) the generator mg1
		GPIO.digitalWrite(swauxg1Pin,flagAuxg1);   //turn on(depends of the result) the generator auxg1

	}

	// GPIO.digitalWrite(swm1Pin, GPIO.HIGH)


}



