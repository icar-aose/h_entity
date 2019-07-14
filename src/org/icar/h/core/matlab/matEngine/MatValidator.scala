package org.icar.h.core.matlab.matEngine

import java.io.File

import akka.actor._
import com.mathworks.engine._
import com.typesafe.config.ConfigFactory
import java.util.{ArrayList, HashMap}
import java.io._
import org.icar.h.sps_management.worker._

class MatValidator extends Actor with ActorLogging {

  private var ml: MatlabEngine = null

  override def preStart(): Unit = {

    self ! start() //if you want start the matlab engine before

  }


  override def receive: Receive = {

    case start() => {
      if (ml == null) {

        //Start MATLAB asynchronously
        val eng = MatlabEngine.startMatlabAsync
        // Get engine instance
        ml = eng.get

        val path = System.getProperty("user.dir") + "/sps_data" //if you are on intellij

        // val path = "/home/elefante/h_entity/sps_data"    //if you are on command line
        println(path)
        var model: String = null
        var textFile: String = null

        val dir = new File(path)
        for (file <- dir.listFiles) {
          if (file.getName.endsWith(".slx"))
            textFile = file.getName
        }

        model = new File(textFile).getName.substring(0, new File(textFile).getName.length - 4)
        ml.putVariable("path", path.toCharArray)
        ml.putVariable("model", model)
        ml.eval("cd(path)")
        ml.eval("startup")
        System.out.println("matlab engine started")
      }

    }


    case evaluateSolution(plan_reference: String, switchers: ArrayList[String], all_switchers: ArrayList[String], open_switchers: ArrayList[String], num_loads: Int) => {
      var results: HashMap[String, Double] = new HashMap[String, Double]
      //intanto setto a 1 tutte le variabili, dopodiche setto a 0 quelle OPEN
      //NB: DOVRO' FARLO AD OGNI ITERAZIONE PERCHE' LA SOLUZIONE VIENE CALCOLATA CON LO STATO INIZIALE;
      System.out.println(switchers)
      System.out.println(all_switchers)
      System.out.println(open_switchers)

      for (i <- 0 until all_switchers.size)
        ml.eval(all_switchers.get(i) + "=[0 1];")

      for (i <- 0 until open_switchers.size)
        ml.eval(open_switchers.get(i) + "=[0 0];")

      var i = 0
      while (i < switchers.size) {
        //System.out.println(switchers.get(i)+","+switchers.get(i+1));
        ml.eval(switchers.get(i) + "(1, 1) = 1;")
        ml.eval(switchers.get(i) + "(1, 2) = " + String.valueOf(switchers.get(i + 1)) + ";")
        i = i + 2
      }

      ml.eval("execute(model);")

      results = fetchLoadAndGen
      sender() ! ResultSolution(results, plan_reference)
    }


    case _ => println("Received unknown msg ")
  }


  def fetchLoadAndGen: HashMap[String, Double] = {
    val name: File = new File(System.getProperty("user.dir") + "/sps_data/startup.m")
    val results: HashMap[String, Double] = new HashMap[String, Double]
    if (name.isFile) {
      val res: Boolean = ml.getVariable("genResult")
      if (res) {
        results.put("genResult", 1.0)
      }
      else {
        results.put("genResult", 0.0)
      }
      val input: BufferedReader = new BufferedReader(new FileReader(name))
      var text: String = null
      var elem: Array[String] = new Array[String](2)
      text = input.readLine()
      while (text != null) {
        elem = text.split("=")
        val value : Array[Double]  = ml.getVariable(elem(0).trim)
        results.put(elem(0).trim, value(0))
        println(elem(0) + "= " + value(0))
        text = input.readLine()
      }
      input.close()
    }
    results
  }

}

object MatValidator {
  def main(args: Array[String]) {
    //get the configuration file from classpath
    val configFile = getClass.getClassLoader.getResource("resources/matlab_application.conf").getFile
    //parse the config
    val config = ConfigFactory.parseFile(new File(configFile))
    //create an actor system with that config
    val system = ActorSystem("RemoteSystem", config)
    //create a remote actor from actorSystem
    val remote = system.actorOf(Props[MatValidator], name = "remote_matlab")
    println("remote is ready")

  }
}
