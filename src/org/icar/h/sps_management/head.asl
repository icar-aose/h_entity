/* Initial beliefs and rules */
mission(undefined).

/* Initial goals */
!continuity_of_service.

/* Plans */
+!continuity_of_service
: 
	true 
<- 
	!failure_prediciton;
	!failure_detection;
.

+!failure_detection
:	
	not mission(undefined)
<-
	.wait(1000);
	?mission(Mission);
	.check_failure(Mission);
	//.send(workersystem,tell,jason.stdlib.check_failure)
.
+!failure_detection
:
	mission(undefined)
<-
	.print("waiting a mission");
	.wait(1000);
	!!failure_detection
.


+current_mission(Mission) : true
<-
    -+mission(Mission);
    .abolish(current_mission(Mission));
.

+failure(FailureDescription) : true
<-  
	.print("Find failure: ", FailureDescription)
	!management_and_recovery(Mission,FailureDescription)
	.abolish(failure(FailureDescription))
.


+!management_and_recovery(Mission,FailureDescription) :	true
<-
	.print("Now contact the worker sps reconfigurator for these failures: ",FailureDescription);
	.wait(2000);
	?mission(Mission);
	.find_reconfigurations(Mission,FailureDescription);
	//.send(workersystem,achive,find_reconfigurations(FailureDescription));
.

+discovered(Plan)
<-
	.print("The worker sps reconfigurator finds a solution: ",Plan)
	.print("Now contact the worker validator")
	.wait(2000)
	.validate(Plan);
	//validate_emergency_management(Plan,/*-->*/Problems);
	//notify_commander(Plan,Results,Problems);
.

+selected(Plan)
<-
    .print("The worker_validator validate the solution: ",Plan)
    .print("Now contact the Worker plan enactor for enact the Plan")
    .wait(2000)
	.enact(Plan);
.

+alive [source(X)]
:
	true
<-
	.print("messages Alive from ",X);
	.abolish(alive);
.




+!failure_prediciton
:
	true
<-
	true
.




