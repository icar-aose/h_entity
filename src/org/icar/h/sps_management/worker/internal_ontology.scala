package org.icar.h.sps_management.worker

import java.util

import org.icar.musa.pmr.Solution
import org.icar.musa.scenarios.sps.ReconfigurationScenario
import org.icar.h.sps_management.rpi_ina219._
import org.icar.h.sps_management.Mission
import java.util.{ArrayList, HashMap}

import akka.actor.NoSerializationVerificationNeeded

import scala.collection.mutable.ArrayBuffer


sealed abstract class Action
sealed abstract class Predicate
sealed abstract class Concept


case class CheckFailure() extends Action
case class FindSolutions( mission_ref : String, failure_description : String ) extends Action
case class Validate( plan_reference : String ) extends Action
case class Enact( plan_reference : String ) extends Action
case class EnactSingle( switch : String ) extends Action
case class Restart() extends Action
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
case class RaspDataVal(data : AmpData, index_rasp : Int) extends Concept with Serializable


@SerialVersionUID(115L)
case class AmpValue (current : Double , adr: INA219.Address, index_rasp : Int) extends Serializable

@SerialVersionUID(116L)
case class evaluateSolution(plan_reference : String, switchers: ArrayList[String], all_switchers: ArrayList[String], open_switchers: ArrayList[String], num_loads: Int) extends Predicate with Serializable

@SerialVersionUID(117L)
case class ResultSolution(results: HashMap[String, Double], plan_reference : String ) extends Action with Serializable

@SerialVersionUID(118L)
case class start() extends Action with Serializable

@SerialVersionUID(119L)
case class EnactPlan(plan_reference : String, acts : util.ArrayList[String]) extends Action with Serializable

@SerialVersionUID(120L)
case class UpdateScenario(open_switchers : ArrayBuffer[String]) extends Action with Serializable

@SerialVersionUID(121L)
case class EnactFault(fault : String) extends Action with Serializable

@SerialVersionUID(122L)
case class StatusFault() extends Action with Serializable

@SerialVersionUID(123L)
case class RequestUpdateScenario() extends Action with Serializable

@SerialVersionUID(123L)
case class UpdateFault(fault : ArrayBuffer[String]) extends Action with Serializable


case class SwitcherMonitoring() extends Action with NoSerializationVerificationNeeded

case class StopAll() extends Action with NoSerializationVerificationNeeded

case class GetPlan(plan_reference : String) extends Predicate with  NoSerializationVerificationNeeded

case class Plan(plan_reference : String,plan:Solution) extends Concept with  NoSerializationVerificationNeeded

case class GetMissionDescription(mission_ref:String) extends Predicate with  NoSerializationVerificationNeeded
case class MissionDescription(mission_ref:String,mission:Mission) extends Concept with  NoSerializationVerificationNeeded

case class GetCurrentScenarioDescription() extends Predicate with  NoSerializationVerificationNeeded
case class CurrentScenarioDescription(scenario: ReconfigurationScenario) extends Concept with  NoSerializationVerificationNeeded

case class PMRFullSolution( sol : Solution) extends NoSerializationVerificationNeeded