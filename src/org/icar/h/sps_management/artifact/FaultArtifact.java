package org.icar.h.sps_management.artifact;

import cartago.Artifact;
import cartago.OPERATION;
import processing.io.GPIO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FaultArtifact extends Artifact {

	int swf1Pin = 26;
	int swf2Pin = 13;
//	int swf3Pin = 5;
//	int swf4Pin = 16;
//	int swf5Pin = 12;


	void init() {

		System.out.println("FaultEnactor Active");
		// inizializzazione direzione dei PIN del GPIO
		GPIO.pinMode(swf1Pin, GPIO.OUTPUT);
		GPIO.pinMode(swf2Pin, GPIO.OUTPUT);
//		GPIO.pinMode(swf3Pin, GPIO.OUTPUT);
//		GPIO.pinMode(swf4Pin, GPIO.OUTPUT);
//		GPIO.pinMode(swf5Pin, GPIO.OUTPUT);

		//inizializzazione stato delle uscite (questi relais funzionano in logica negata; uscita alta= relais spento cioè su nc e viceversa)
		GPIO.digitalWrite(swf1Pin, GPIO.HIGH);
		GPIO.digitalWrite(swf2Pin, GPIO.HIGH);
//		GPIO.digitalWrite(swf3Pin, GPIO.HIGH);
//		GPIO.digitalWrite(swf4Pin, GPIO.HIGH);
//		GPIO.digitalWrite(swf5Pin, GPIO.HIGH);

	}

	//NB: se i flag valgono 0, allora il generatore è acceso altrimenti spento (logica negata dei relays)

	@OPERATION
	public void actFault(String fault) {

		System.out.println(fault);
		if(fault.equals("switchf1")) {
			if (GPIO.digitalRead(swf1Pin) == 1) {
				System.out.println("open");
				GPIO.digitalWrite(swf1Pin, 0);
			} else {
				GPIO.digitalWrite(swf1Pin, 1);
				System.out.println("close");
			}
		}
		else
			if (GPIO.digitalRead(swf2Pin)==1)
				GPIO.digitalWrite(swf2Pin, 0);
			else GPIO.digitalWrite(swf2Pin, 1);
	}

}



