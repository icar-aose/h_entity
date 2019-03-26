package org.icar.h.sps_management.worker

import akka.actor.{Actor, ActorLogging, Props}
import org.icar.musa.context.StateOfWorld
import org.icar.musa.main_entity.AbstractCapability
import org.icar.musa.pmr._

object PMRActor {
  def props(ps : SingleGoalProblemSpecification, initial_state:StateOfWorld, cap_set: Array[AbstractCapability], termination : TimeTermination) : Props =
    Props(classOf[PMRActor],ps,initial_state,cap_set,termination)
}

class PMRActor(ps : SingleGoalProblemSpecification, initial_state:StateOfWorld, cap_set: Array[AbstractCapability], termination : TimeTermination) extends Actor with ActorLogging {
  val timestamp: Long = System.currentTimeMillis

  val explorer: SingleGoalProblemExploration = new SingleGoalProblemExploration(ps, cap_set, self.path.name)
  val root: WTSStateNode = get_root(initial_state)
  val wts = new WTS(root)

  val solution_builder = new EarlyDecisionSolutionBuilder
  val seq_builder = new SequenceBuilder(root, solution_builder)

  case class DoIteration( it : Int)

  override def preStart: Unit = {
    log.info("ready")
    explorer.to_visit = root :: explorer.to_visit
    self ! DoIteration(1)
  }

  override def receive: Receive = {
    case DoIteration(iteration) =>
      val end = check_termination(termination)

      if (!end) {
        // iteration
        log.debug(s"iteration $iteration")

        explorer.execute_iteration()
        val exp_opt = explorer.highest_expansion
        if (exp_opt.isDefined) {
          val exp = exp_opt.get
          wts.addExpansion(exp)

          exp match {
            case x: SimpleWTSExpansion =>
              explorer.pick_expansion(x)
              seq_builder.deal_with_expansion(x)
              if (!x.end.su.isAccepted)
                explorer.new_node(x.end)

            case x: MultiWTSExpansion =>
              explorer.pick_expansion(x)
              seq_builder.deal_with_multi_expansion(x)
              for (e <- x.evo.values) {
                if (!e.su.isAccepted)
                  explorer.new_node(e)
              }

            case _ =>
          }
        }

        if (solution_builder.new_solutions.nonEmpty) {
          //log.info("new solutions found")
          solution_builder.new_solutions.foreach( sol => context.parent ! PMRFullSolution(sol) )
          solution_builder.new_solutions = List()
        }

        self ! DoIteration(iteration+1)

      } else {
        // terminated
        log.info("time is over")
      }

    case _ =>

  }

  private def get_root(wi: StateOfWorld): WTSStateNode = {
    val su = explorer.su_builder.initialize(ps.goal.ltl, wi, ps.ass_set )
    val qos = ps.asset.evaluate_node(wi, su.distance_to_satisfaction)
    WTSStateNode(wi, su, qos)
  }

  private def check_termination(termination: TimeTermination) : Boolean = {
    val currtimestamp = System.currentTimeMillis
    val elapsed = currtimestamp-timestamp

    if (elapsed > termination.millisec)
      true
    else
      false
  }
}
