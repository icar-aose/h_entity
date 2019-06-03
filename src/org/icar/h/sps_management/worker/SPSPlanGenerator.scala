package org.icar.h.sps_management.worker



import scala.collection.mutable.{ArrayBuffer, HashMap}
import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import org.icar.fol._
import org.icar.h.core.Akka2Jade
import org.icar.ltl.{Finally, LogicConjunction, ltlFormula}
import org.icar.musa.context.{AddEvoOperator, EvoOperator, RemoveEvoOperator, StateOfWorld}
import org.icar.musa.main_entity._
import org.icar.musa.pmr.{SingleGoalProblemSpecification, Solution, TimeTermination}
import org.icar.musa.scenarios.sps.{Circuit, Mission, ReconfigurationScenario}
import java.util.ResourceBundle

import scala.collection.mutable
import scala.concurrent.{Await, Future}

object SPSPlanGenerator {
  def props(bridge: Akka2Jade, mission_man_ref: ActorRef, circ_sens_ref: ActorRef): Props = Props(classOf[SPSPlanGenerator], bridge, mission_man_ref, circ_sens_ref)
}

class SPSPlanGenerator(val bridge: Akka2Jade, val mission_man_ref: ActorRef, val circ_sens_ref: ActorRef) extends Actor with ActorLogging {
  private val term = TimeTermination((2 minutes).toMillis)

  val properties: ResourceBundle = ResourceBundle.getBundle("org.icar.h.sps_management.Boot")

  private val path: String = properties.getString("circuit.name")
  private val circuit = Circuit.load_from_file(path)

  private val discovered_solutions: mutable.HashMap[String, Solution] = mutable.HashMap.empty[String, Solution]

  override def preStart: Unit = {
    log.info("ready")
  }

  override def receive: Receive = {

    case FindSolutions(mission_ref, failure_ref) =>
      //Thread.sleep(2000)    //find a solution
      log.info(s"finding solutions for the failure: $failure_ref in mission $mission_ref \n")

      implicit val timeout: Timeout = Timeout(5 seconds)
      val future_mission: Future[Any] = mission_man_ref ? GetMissionDescription(mission_ref)
      val future_scenario: Future[Any] = circ_sens_ref ? GetCurrentScenarioDescription()

      val mission = Await.result(future_mission, timeout.duration).asInstanceOf[MissionDescription].mission
      log.info("list of vitals:")
      mission.vitals.foreach(log.info)

      val scenario = Await.result(future_scenario, timeout.duration).asInstanceOf[CurrentScenarioDescription].scenario
      log.info("list of active generators:")
      scenario.up_generators.foreach(log.info)

      val wi = initial_state(circuit,scenario)
      val goal = goal_specification(circuit, mission)
      val assumptions = assumption_set(circuit)
      val cap_set: Array[AbstractCapability] = capabilities(circuit, scenario)
      val quality_asset = new SPSQualityAsset(circuit, mission, assumptions)

      val problem_specification = SingleGoalProblemSpecification(assumptions, goal, quality_asset)

      context.actorOf(PMRActor.props(problem_specification,wi,cap_set,term), "pmr_actor")

    case PMRFullSolution(sol) =>
      //println("discovered")
      val num = discovered_solutions.size + 1
      val name = "solution_"+num
      discovered_solutions += (name -> sol)
      bridge.sendHead(s"discovered($name)")

    case GetPlan(sol_ref) =>
      val sol: Solution = discovered_solutions(sol_ref)
      log.debug("sending back the requested solution!")
      sender() ! Plan(sol_ref, sol)

    case _ ⇒
      log.error("unspecified message")
  }


  private def goal_specification(circuit: Circuit, mission: Mission): LTLGoal = {
    var vitals = ArrayBuffer[ltlFormula]()
    for (v <- circuit.loads if mission.vitals.contains(v.id))
      vitals += v.atom
    val conj = LogicConjunction(vitals)
    LTLGoal(Finally(conj))
  }

  private def assumption_set(circuit: Circuit): AssumptionSet = {
    /* standard assumptions */
    val list = ArrayBuffer[Assumption]()
    list += Assumption("off(X) :- load(X), not on(X).")
    list += Assumption("off(X) :- generator(X), not on(X).")
    list += Assumption("down(X) :- node(X), not up(X).")
    list += Assumption("open(X) :- sw(X), not closed(X).")

    for (n <- circuit.nodes)
      list += Assumption("node(n" + n.id + ").")

    for (c <- circuit.connections) {
      list += Assumption(c.source.up + ":-" + c.dest.up + "," + "not " + c.failure + ".")
      list += Assumption(c.dest.up + ":-" + c.source.up + "," + "not " + c.failure + ".")
    }
    for (s <- circuit.switcher) {
      list += Assumption("sw(" + s.id + ").")
      list += Assumption(s.source.up + ":-" + s.dest.up + ", " + s.closed + ".")
      list += Assumption(s.dest.up + ":-" + s.source.up + ", " + s.closed + ".")
    }
    for (g <- circuit.generators) {
      list += Assumption("generator(" + g.id + ").")
      list += Assumption(g.node.up + ":-" + g.up + ".")
    }
    for (l <- circuit.loads) {
      list += Assumption("load(" + l.id + ").")
      list += Assumption(l.up + ":-" + l.node.up + ".")
    }

    AssumptionSet(list: _*)
  }

  private def initial_state(circuit: Circuit, scenario: ReconfigurationScenario): StateOfWorld = {
    var list = ArrayBuffer[GroundPredicate]()

    for (r <- circuit.switcher) {
      val state = if (scenario.open_switchers.contains(r.id)) "open" else "closed"
      list += GroundPredicate(state, AtomTerm(r.id))
    }

    for (r <- circuit.generators) {
      val state = if (scenario.up_generators.contains(r.id)) "on" else if (scenario.generator_malfunctioning.contains(r.id)) "error" else "off"
      list += GroundPredicate(state, AtomTerm(r.id))
    }

    for (r <- scenario.failures) {
      list += GroundPredicate("f", AtomTerm(r))
    }

    StateOfWorld.create(list.toArray)
  }

  private def capabilities(circuit: Circuit, scenario: ReconfigurationScenario): Array[AbstractCapability] = {
    var cap_list = ArrayBuffer[AbstractCapability]()

    for (gen <- circuit.generators if !scenario.generator_malfunctioning.contains(gen)) {
      //cap_list += generate_switch_on_generator(gen.id)
      //cap_list += generate_switch_off_generator(gen.id)
    }

    for (sw <- circuit.switcher if !scenario.switcher_malfunctioning.contains(sw)) {
      if (circuit.sw_map.contains(sw.id)) {
        val g2_name = circuit.sw_map(sw.id)
        cap_list += generate_combinated_on_off_switcher(sw.id, g2_name)
      } else {

        val parts = sw.id.split("switch")
        val second = parts(1)
        if (!second.startsWith("f")) {
          cap_list += generate_close_switcher(sw.id)
          cap_list += generate_open_switcher(sw.id)
        }
      }
    }

    def generate_switch_on_generator(name : String) : GroundedAbstractCapability = {
      val generator_name = "switch_ON_"+name
      val generator = AtomTerm(name)
      val pre = FOLCondition(Literal(Predicate("off", generator )))
      val post = FOLCondition(Literal(Predicate("on", generator )))
      val evo_1 = EvolutionScenario(Array[EvoOperator](RemoveEvoOperator(GroundPredicate("off", generator)),AddEvoOperator(GroundPredicate("on", generator))))
      GroundedAbstractCapability(generator_name,pre,post,Map("1"-> evo_1),DataInSpecification(ArrayBuffer()),DataOutSpecification(ArrayBuffer()),Map(),"switch_OFF_"+name)
    }

    def generate_switch_off_generator(name : String) : GroundedAbstractCapability = {
      val generator_name = "switch_OFF_"+name
      val generator = AtomTerm(name)
      val pre = FOLCondition(Literal(Predicate("on", generator )))
      val post = FOLCondition(Literal(Predicate("off", generator )))
      val evo_1 = EvolutionScenario(Array[EvoOperator](RemoveEvoOperator(GroundPredicate("on", generator)),AddEvoOperator(GroundPredicate("off", generator))))
      GroundedAbstractCapability(generator_name,pre,post,Map("1"-> evo_1),DataInSpecification(ArrayBuffer()),DataOutSpecification(ArrayBuffer()),Map(),"switch_ON_"+name)
    }
    def generate_close_switcher(name : String) : GroundedAbstractCapability = {
      val capname = "CLOSE_"+name
      val switcher = AtomTerm(name)
      val pre = FOLCondition(Literal(Predicate("open", switcher )))
      val post = FOLCondition(Literal(Predicate("closed", switcher )))
      val evo_1 = EvolutionScenario(Array[EvoOperator](RemoveEvoOperator(GroundPredicate("open", switcher)),AddEvoOperator(GroundPredicate("closed", switcher))))
      GroundedAbstractCapability(capname,pre,post,Map("1"-> evo_1),DataInSpecification(ArrayBuffer()),DataOutSpecification(ArrayBuffer()),Map(),"OPEN_"+name)
    }
    def generate_open_switcher(name : String) : GroundedAbstractCapability = {
      val capname = "OPEN_"+name
      val switcher = AtomTerm(name)
      val pre = FOLCondition(Literal(Predicate("closed", switcher )))
      val post = FOLCondition(Literal(Predicate("open", switcher )))
      val evo_1 = EvolutionScenario(Array[EvoOperator](RemoveEvoOperator(GroundPredicate("closed", switcher)),AddEvoOperator(GroundPredicate("open", switcher))))
      GroundedAbstractCapability(capname,pre,post,Map("1"-> evo_1),DataInSpecification(ArrayBuffer()),DataOutSpecification(ArrayBuffer()),Map(),"CLOSE_"+name)
    }
    def generate_combinated_on_off_switcher(name1 : String, name2 : String) : GroundedAbstractCapability = {
      val capname = "CLOSE_"+name1+"_&_OPEN_"+name2
      val switcher1 = AtomTerm(name1)
      val switcher2 = AtomTerm(name2)
      val pre = FOLCondition(Conjunction(Literal(Predicate("open", switcher1 )),Literal(Predicate("closed", switcher2 ))))
      val post = FOLCondition(Conjunction(Literal(Predicate("closed", switcher1 )),Literal(Predicate("open", switcher2 ))))
      val evo_1 = EvolutionScenario(Array[EvoOperator](
        RemoveEvoOperator(GroundPredicate("open", switcher1)),
        AddEvoOperator(GroundPredicate("closed", switcher1)),
        RemoveEvoOperator(GroundPredicate("closed", switcher2)),
        AddEvoOperator(GroundPredicate("open", switcher2))
      ))
      GroundedAbstractCapability(capname,pre,post,Map("1"-> evo_1),DataInSpecification(ArrayBuffer()),DataOutSpecification(ArrayBuffer()),Map(),"CLOSE_"+name2+"_&_OPEN_"+name2)
    }

    cap_list.toArray
  }


}

