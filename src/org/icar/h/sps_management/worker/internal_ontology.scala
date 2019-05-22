package org.icar.h.sps_management.worker

import org.icar.musa.pmr.Solution
import org.icar.musa.scenarios.sps.{Mission, ReconfigurationScenario}
import org.icar.h.sps_management.rpi_ina219._
import java.util.{ArrayList, HashMap}


sealed abstract class Action
sealed abstract class Predicate
sealed abstract class Concept


case class CheckFailure( mission_ref : String) extends Action
case class FindSolutions( mission_ref : String, failure_description : String ) extends Action
case class Validate( plan_reference : String ) extends Action
case class Enact( plan_reference : String ) extends Action

case class StartCheckMonitor () extends Action
case class Check () extends Action

@SerialVersionUID(113L)
case class AmpData (current : Array[Double] ) extends Serializable
{

  def setCurrent (current : Double, i : Int): Unit = {
    this.current(i) = current
  }

  def getCurrent(i: Int): Double = this.current(i)
}

@SerialVersionUID(114L)
case class RaspDataVal(data : AmpData) extends Concept with Serializable


@SerialVersionUID(115L)
case class AmpValue (current : Double , adr: INA219.Address) extends Serializable

@SerialVersionUID(116L)
case class evaluateSolution(plan_reference : String, switchers: ArrayList[String], all_switchers: ArrayList[String], open_switchers: ArrayList[String], num_loads: Int) extends Predicate with Serializable

@SerialVersionUID(117L)
case class ResultSolution(results: HashMap[String, Double], plan_reference : String ) extends Action with Serializable

@SerialVersionUID(118L)
case class start() extends Action with Serializable

case class GetPlan( plan_reference : String ) extends Predicate
case class Plan(plan_reference : String,plan:Solution) extends Concept

case class GetMissionDescription(mission_ref:String) extends Predicate
case class MissionDescription(mission_ref:String,mission:Mission) extends Concept

case class GetCurrentScenarioDescription() extends Predicate
case class CurrentScenarioDescription(scenario: ReconfigurationScenario) extends Concept

case class PMRFullSolution( sol : Solution)