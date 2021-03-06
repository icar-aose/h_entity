package org.icar.h.sps_management.worker

import akka.actor._
import org.icar.h.sps_management.rpi_ina219._


object SensorMonitor {
  def props(adr : INA219.Address, index_rasp : Int): Props = Props(classOf[SensorMonitor],adr, index_rasp)
}
class SensorMonitor (adr : INA219.Address , index_rasp : Int) extends Actor with ActorLogging {

	  var s : INA219 = new INA219 (adr, 0.1
				,1,INA219.Brng.V16,INA219.Pga.GAIN_8
				,INA219.Adc.BITS_12,INA219.Adc.BITS_12)

    var current : Double = _

	  override def preStart() : Unit = {
      log.info("Ready")
	  }
   
    override def receive: Receive = {
      case Check() =>
      while(true)
      { current =s.getCurrent*1000
        sender() ! AmpValue(current,adr,index_rasp)
        Thread.sleep(1000)
      }
      
    
      case _ => println("Received unknown msg: "+self)
	  }
  }





