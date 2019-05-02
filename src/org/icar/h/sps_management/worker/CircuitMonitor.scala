package org.icar.h.sps_management.worker

import java.io.File

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import cartago.{ArtifactId, CartagoException}
import cartago.util.agent.CartagoBasicContext
import cartago.util.agent._
import com.typesafe.config.ConfigFactory
import org.icar.h.core.Akka2Jade
import org.icar.musa.scenarios.sps.ReconfigurationScenario


object CircuitMonitor {
  def props(bridge: Akka2Jade): Props = Props(classOf[CircuitMonitor], bridge)
}

class CircuitMonitor(val bridge: Akka2Jade) extends Actor with ActorLogging {
  val scenario: ReconfigurationScenario = ReconfigurationScenario.scenario_circuit3_parsed_1

  var my_context: CartagoBasicContext = new CartagoBasicContext("my_agent")
  var my_device: ArtifactId = _
  var p: Percept = _

  val CheckerActor = context.actorSelection("akka.tcp://RemoteSystem@194.119.214.139:5150/user/remote")  //IP of the PC remote



  val configFile = getClass.getClassLoader.getResource("local_application.conf").getFile
  val config = ConfigFactory.parseFile(new File(configFile))
  val system = ActorSystem("ClientSystem",config)
  val localActor = system.actorOf(Props[CircuitMonitor], name="local")




  override def preStart: Unit = {
    log.info("ready")
    try {
      my_device = my_context.makeArtifact("device", "org.icar.h.Device")
      my_context.focus(my_device)

    } catch {
      case e: CartagoException =>
        e.printStackTrace();
    }

  }

  override def receive: Receive = {

    case CheckFailure(mission_ref) =>
      //println("i'm worker check failure!\n")
      /*var p: Percept = null
      val sig: String = null


      SimpleClient.transfer

      do {
        p = my_context.waitForPercept()
        log.info("percept: " + p.getSignal)
      } while (!p.hasSignal());
*/
      CheckerActor ! "Check"

    case msg : Double =>

      Thread.sleep(500 )
      println ("current value low: "+ msg)
      bridge.sendHead("failure(f1)")

    case GetCurrentScenarioDescription() =>
      sender() ! CurrentScenarioDescription(scenario)


    case _ =>
      log.error("unspecified message")
  }
}