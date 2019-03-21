package org.icar.h.sps_management.worker

import akka.actor.{Actor, ActorLogging, Props}
import org.icar.h.Akka2Jade
import cartago.{ArtifactId, CartagoException, Op, Tuple}
import cartago.util.agent.CartagoBasicContext
import cartago.events._
import cartago.util.agent._
import org.icar.musa.scenarios.sps.ReconfigurationScenario;

object CircuitMonitor {
   def props(bridge : Akka2Jade) : Props = Props(classOf[CircuitMonitor],bridge)
}

class CircuitMonitor(val bridge : Akka2Jade) extends Actor with ActorLogging {
  val scenario: ReconfigurationScenario = ReconfigurationScenario.scenario_circuit3_parsed_1

  var my_context : CartagoBasicContext = new CartagoBasicContext("my_agent")
	var my_device : ArtifactId = null
	var p : Percept  = null
		  
		  override def preStart : Unit = {
        log.info("ready")
        try {
          my_device = my_context.makeArtifact("device", "org.icar.h.Device");
          my_context.focus(my_device)

        } catch {
		      case e : CartagoException =>
		        e.printStackTrace();
        }

      }
    
      override def receive: Receive = {

        case CheckFailure( mission_ref ) =>
          //println("i'm worker check failure!\n")
				  var p :Percept = null
          val sig : String = null
				  do {
			 	    p = my_context.waitForPercept()
		        log.info("percept: "+p.getSignal)
				  } while (!p.hasSignal());

          bridge.sendHead("failure(f1)")
          
        case _ =>
          println("FailureMonitor: unspecied message")
    }
}