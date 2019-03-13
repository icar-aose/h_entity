package org.icar.h.sps_management;

import org.sintef.jarduino.*;


public class PowerSensorArtifact extends JArduino {

	
  public PowerSensorArtifact(String port) {
    super(port);
    
  }
  
  @Override
	protected void setup() {
		// initialize the digital pin as an output.
		// Pin 13 has a LED connected on most Arduino boards:
		pinMode(DigitalPin.PIN_8, PinMode.INPUT);
		pinMode(DigitalPin.PIN_11, PinMode.INPUT);
		pinMode(DigitalPin.PIN_5, PinMode.OUTPUT);
	}
	@Override
	protected void loop() {
		// set the LED on
		delay(2000);
		System.out.println(digitalRead(DigitalPin.PIN_8));
		if (digitalRead(DigitalPin.PIN_8) == DigitalState.LOW)
		  {
		    digitalWrite(DigitalPin.PIN_5, DigitalState.HIGH);
		  }
		delay(2000);
		if (digitalRead(DigitalPin.PIN_11) == DigitalState.LOW)
		  {
		    digitalWrite(DigitalPin.PIN_5, DigitalState.LOW);
		  }
		
	}
	public static void main (String argv[])
	{
		JArduino arduino = new PowerSensorArtifact("/dev/tty.usbmodem14101");
		arduino.runArduinoProcess();
	}
}