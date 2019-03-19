package org.icar.h.sps_management.worker

case class CheckFailure()
case class FindSolutions( failure_description : String )
case class Validate( plan_reference : String )
case class Enact( plan_reference : String )

case class GetSolution( plan_reference : String )
case class Solution(plan_reference : String,plan:String)