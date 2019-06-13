package org.icar.h.sps_management.worker

import java.io.File

import akka.actor._
import cartago.util.agent.CartagoBasicContext
import cartago.{CartagoService, NodeId, Op}
import com.typesafe.config.ConfigFactory
import processing.io.GPIO

class SwitcherMonitor extends Actor with ActorLogging {

  val node: NodeId = CartagoService.startNode
  var my_context : CartagoBasicContext = new CartagoBasicContext("remote_agent")
  var my_device = my_context.makeArtifact("switcherArtifact", "org.icar.h.sps_management.artifact.SwitcherArtifact")
  
  val swm1Pin = 27 //Relais 1 motore 1
  val swm1busPin = 17 //Relais 2 motore 1
  val swm2Pin = 25 //Relais 1 motore 2
  val swm2busPin = 13 //Relais 2 motore 2
  val swmg1Pin = 6 //Relais1 main gen 1
  val swmg1busPin = 5 //Relais2 main gen 1
  val swL1Pin = 20 //Relais 1 Load 1
  val swL1busPin = 16 //Relais 2 Load 1
  val swL2Pin = 12 //Relais 1 Load 2
  val swL2busPin = 21 //Relais 2 Load 2
  val swL2plusPin = 23 //Relais 3 plus Load 2
  val swauxg1Pin = 26 //Relais1 aux gen 1
  val swauxg1busPin = 19 //Relais2 aux gen 1

  val NamePinNum: Map[String, Int] = Map("swm1Pin"-> 27,"swm1busPin"-> 17,"swm2Pin"-> 25
    ,"swm2busPin"-> 13,"swmg1Pin"-> 6,"swmg1busPin"-> 5,
  "swL1Pin"-> 20,"swL1busPin"-> 16,"swL2Pin"-> 12,"swL2busPin"-> 21,
    "swauxg1Pin"-> 26,"swauxg1busPin"-> 19)

  val NamePinSwitch : Map [String,String] = Map(   "swm1Pin"->"switchsw1",
  "switchsw2"->"swm2Pin",
  "switchsw3"->"swL1Pin",
  "switchsw4"->"swL2Pin",
  "switchsws1"->"swm1busPin",
  "switchswp1"->"swm1busPin",
  "switchsws2"->"swm2busPin",
  "switchswp2"->"swm2busPin",
  "switchsws3"->"swmg1busPin",
  "switchswp3"->"swmg1busPin",
  "switchsws4"->"swL1busPin",
  "switchswp4"->"swL1busPin",
  "switchsws5"->"swL2busPin",
  "switchswp5"->"swL2busPin",
  "switchsws6"->"swauxg1busPin",
  "switchswp6"->"swauxg1busPin",
  "switchswmg1"->"swmg1Pin",
  "switchswauxg1"->"swauxg1Pin")


  var open_switchers : Set[String] = Set.empty

  override def preStart() : Unit = {

    log.info("Ready")

    self ! SwitcherMonitoring()

  }


  override def receive: Receive = {



    case SwitcherMonitoring() =>
    { var readVal : Int = 0

      open_switchers =Set.empty
      for ((k,v) <- NamePinNum)
        {
          readVal = GPIO.digitalRead(v)

            k match
            {
              case "swm1busPin" => if(readVal==1) open_switchers+="switchswp1" else open_switchers+="switchsws1"
              case "swm2busPin" => if(readVal==1) open_switchers+="switchswp2" else open_switchers+="switchsws2"
              case "swmg1busPin" =>if(readVal==1) open_switchers+="switchswp3" else open_switchers+="switchsws3"
              case "swL1busPin" => if(readVal==1) open_switchers+="switchswp4" else open_switchers+="switchsws4"
              case "swL2busPin" => if(readVal==1) open_switchers+="switchswp5" else open_switchers+="switchsws5"
              case "swauxg1busPin" => if(readVal==1) open_switchers+="switchswp6" else open_switchers+="switchsws6"
              case "swm1Pin" => if(readVal==1) open_switchers+="switchsw1"
              case "swm2Pin" => if(readVal==1) open_switchers+="switchsw2"
              case "swL1Pin" => if(readVal==1) open_switchers+="switchsw3"
              case "swL2Pin" => if(readVal==1) open_switchers+="switchsw4"
              case "swmg1Pin" => if(readVal==1) open_switchers+="switchswmg1"
              case "swauxg1Pin" => if(readVal==1) open_switchers+="switchswauxg1"
            }



        }

      println(open_switchers)
      Thread.sleep(1000)
      self ! SwitcherMonitoring()


    }
    case RequestUpdateScenario()=>
      sender() ! UpdateScenario(open_switchers)




    case _ => println("Received unknown msg ")
  }
}


object SwitcherMonitor{
  def main(args: Array[String]) {
    //get the configuration file from classpath
    val configFile = getClass.getClassLoader.getResource("org/icar/h/sps_management/worker/switcher_monitor_application.conf").getFile
    //parse the config
    val config = ConfigFactory.parseFile(new File(configFile))
    //create an actor system with that config
    val system = ActorSystem("RemoteSystem" , config)
    //create a remote actor from actorSystem
    val remote = system.actorOf(Props[SwitcherMonitor], name="switcher")
    println("remote is ready")


  }
}