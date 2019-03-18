/* Initial beliefs and rules */

/* Initial goals */
mission(on_shore).

/* automatic */
shareable(Plans).

!continuity_of_service.

/* Plans */

+!continuity_of_service 
: 
	true 
<- 
	!failure_prediciton;
	!failure_detection;
.

+!failure_prediciton
:	
	true
<-
	true
.


+!failure_detection
:	
	true
<-
	
	.wait(3000);
	.send(workersystem,tell,check_failure);
.




+failure(FailureDescription)
: true
<-  
	.print("Find failure: ", FailureDescription)
	!management_and_recovery(Mission,FailureDescription)
	.abolish(failure(X))
.



+!management_and_recovery(Mission,FailureDescription)
:	
	true
<-
	.print("Now contact the worker sps reconfigurator for these failures: ",FailureDescription)
	.send(workersystem,achive,sps_reconfigurator(FailureDescription));
.

+discovered(Plan)
<-
	.print("The worker sps reconfigurator finds a solution: ",Plan)
	.print("Now contact the worker validator")
	.send(workersystem,achive,validator(Plan));
	//validate_emergency_management(Plan,/*-->*/Problems);
	//notify_commander(Plan,Results,Problems);
.

+selected(Plan)
<-
    .print("The worker_validator validate the solution: ",Plan)
    .print("Now contact the Worker plan enactor for enact the Plan")
	.send(workersystem,achive,enact(Plan));
.

+alive [source(X)]
:
	true
<-
	.print("messages Alive from ",X);
	.abolish(alive);
.


