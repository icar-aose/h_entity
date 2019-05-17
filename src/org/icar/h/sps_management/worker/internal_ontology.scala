package org.icar.h.sps_management.worker

import org.icar.musa.pmr.Solution
import org.icar.musa.scenarios.sps.{Mission, ReconfigurationScenario}


sealed abstract class Action
sealed abstract class Predicate
sealed abstract class Concept


case class CheckFailure( mission_ref : String) extends Action
case class FindSolutions( mission_ref : String, failure_description : String ) extends Action
case class Validate( plan_reference : String ) extends Action
case class Enact( plan_reference : String ) extends Action

case class Check () extends Action

@SerialVersionUID(113L)
case class AmpData (current : Array[Double] = new Array[Double](4)) extends Serializable
{

  def setCurrent (current : Double, i : Int): Unit = {

    this.current(i) = current
  }

  def getCurrent(i: Int): Double = this.current(i)
}

@SerialVersionUID(114L)
case class RaspDataVal(data : AmpData) extends Concept with Serializable

case class GetPlan( plan_reference : String ) extends Predicate
case class Plan(plan_reference : String,plan:Solution) extends Concept

case class GetMissionDescription(mission_ref:String) extends Predicate
case class MissionDescription(mission_ref:String,mission:Mission) extends Concept

case class GetCurrentScenarioDescription() extends Predicate
case class CurrentScenarioDescription(scenario: ReconfigurationScenario) extends Concept

case class PMRFullSolution( sol : Solution)