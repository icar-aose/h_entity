package org.icar.h.sps_management.worker

case class CheckFailure( mission_ref : String)
case class FindSolutions( mission_ref : String, failure_description : String )
case class Validate( plan_reference : String )
case class Enact( plan_reference : String )

case class GetSolution( plan_reference : String )
case class Solution(plan_reference : String,plan:String)

case class GetMission(mission_ref:String)
case class Mission(mission_ref:String,mission:String)

