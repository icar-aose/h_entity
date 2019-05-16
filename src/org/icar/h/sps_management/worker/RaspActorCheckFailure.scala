package org.icar.h.sps_management.worker

import processing.io._
import java.io.File
import akka.actor._
import com.typesafe.config.ConfigFactory
import org.icar.h.sps_management.rpi_ina219._
  
class RaspActorCheckFailure extends Actor {
   	val ledPin = 17
	var s0 : INA219 = new INA219 (INA219.Address.ADDR_40, 0.1
				,1,INA219.Brng.V16,INA219.Pga.GAIN_8
				,INA219.Adc.BITS_12,INA219.Adc.BITS_12)
	
	var s1 : INA219 = new INA219 (INA219.Address.ADDR_41, 0.1
				,1,INA219.Brng.V16,INA219.Pga.GAIN_8
				,INA219.Adc.BITS_12,INA219.Adc.BITS_12)
	
	var s2 : INA219 = new INA219 (INA219.Address.ADDR_44, 0.1
				,1,INA219.Brng.V16,INA219.Pga.GAIN_8
				,INA219.Adc.BITS_12,INA219.Adc.BITS_12)
	
	var s3 : INA219 = new INA219 (INA219.Address.ADDR_45, 0.1
				,1,INA219.Brng.V16,INA219.Pga.GAIN_8
				,INA219.Adc.BITS_12,INA219.Adc.BITS_12)


	var swm1Pin=27; //Relais 1 motore
	var swm1busPin=17; //Relais 2 motore
	var swmg1Pin=6; //Relais1 main gen 1
	var swmg1busPin=5; //Relais2 main gen 1
	var swL1Pin=20; //Relais 1 Load 1
	var swL1busPin=16; //Relais 2 Load 1
   	
	override def preStart() : Unit = {


		// inizializzazione direzione dei PIN del GPIO
		GPIO.pinMode(swm1Pin, GPIO.OUTPUT)
		GPIO.pinMode(swm1busPin, GPIO.OUTPUT)
		GPIO.pinMode(swmg1Pin, GPIO.OUTPUT)
		GPIO.pinMode(swmg1busPin, GPIO.OUTPUT)
		GPIO.pinMode(swL1Pin, GPIO.OUTPUT)
		GPIO.pinMode(swL1busPin, GPIO.OUTPUT)

		//inizializzazione stato delle uscite (questi relais funzionano in logica negata; uscita alta= relais spento cioè su nc e viceversa)
		GPIO.digitalWrite(swm1Pin, GPIO.HIGH)
		GPIO.digitalWrite(swm1busPin, GPIO.HIGH)
		GPIO.digitalWrite(swmg1Pin, GPIO.LOW)
		GPIO.digitalWrite(swmg1busPin, GPIO.HIGH)
		GPIO.digitalWrite(swL1Pin, GPIO.LOW)
		GPIO.digitalWrite(swL1busPin, GPIO.HIGH)
	}
   
  override def receive: Receive = {
    case Check() =>
	GPIO.digitalWrite(swm1Pin, GPIO.LOW)
	Thread.sleep(3000)
       	GPIO.digitalWrite(swL1Pin, GPIO.LOW)
	while(true)
      		{
		 println("current0: "+s0.getCurrent*1000)
		 println("current1: "+s1.getCurrent*1000)
		 println("current2: "+s2.getCurrent*1000)
		 println("current3: "+s3.getCurrent*1000+"\n")
		 Thread.sleep(3000)
		}
      
    
    case _ => println("Received unknown msg ")
	}
  }


object RaspActorCheckFailure{
 def main(args: Array[String]) {
   //get the configuration file from classpath
   val configFile = getClass.getClassLoader.getResource("org/icar/h/sps_management/worker/remote_application.conf").getFile
   //parse the config
   val config = ConfigFactory.parseFile(new File(configFile))
   //create an actor system with that config
   val system = ActorSystem("RemoteSystem" , config)
   //create a remote actor from actorSystem
   val remote = system.actorOf(Props[RaspActorCheckFailure], name="remote")
   println("remote is ready")


 }
}

