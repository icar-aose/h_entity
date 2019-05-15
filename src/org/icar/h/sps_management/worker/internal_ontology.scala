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

case class GetPlan( plan_reference : String ) extends Predicate
case class Plan(plan_reference : String,plan:Solution) extends Concept

case class GetMissionDescription(mission_ref:String) extends Predicate
case class MissionDescription(mission_ref:String,mission:Mission) extends Concept

case class GetCurrentScenarioDescription() extends Predicate
case class CurrentScenarioDescription(scenario: ReconfigurationScenario) extends Concept

case class PMRFullSolution( sol : Solution)