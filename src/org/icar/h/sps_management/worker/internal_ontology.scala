package org.icar.h.sps_management.worker

import org.icar.musa.scenarios.sps.Mission

case class CheckFailure( mission_ref : String)
case class FindSolutions( mission_ref : String, failure_description : String )
case class Validate( plan_reference : String )
case class Enact( plan_reference : String )

case class GetSolution( plan_reference : String )
case class Solution(plan_reference : String,plan:String)

case class GetMissionDescription(mission_ref:String)
case class MissionDescription(mission_ref:String,mission:Mission)

