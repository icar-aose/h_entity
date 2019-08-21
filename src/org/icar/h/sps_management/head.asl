/* Initial beliefs and rules */
mission(undefined).

/* Initial goals */
!continuity_of_service.

/* Plans */
+!continuity_of_service
: 
	true 
<-
	makeArtifact("CaptainGui","org.icar.h.sps_management.artifact.CaptainInterface",[],Id);
	//!failure_prediciton;
	!failure_detection;
    focus(Id);
.




+!failure_detection
:	
	not mission(undefined)
<-
	//.print("checking the state of circuits");
	.wait(1000);
	?mission(Mission);
	.check_failure(Mission);
.
+!failure_detection
:
	mission(undefined)
<-
	//.print("waiting a mission");
	.wait(1000);
	!!failure_detection
.


+current_mission(Mission) : true
<-
    //.print("changing mission: ",Mission);
    -+mission(Mission);
    .abolish(current_mission(Mission));
.

+failure(FailureDescription) : true
<-  
	.print("Find failure: ", FailureDescription)
	!management_and_recovery(FailureDescription)
	.abolish(failure(FailureDescription))
.


+!management_and_recovery(FailureDescription) :	true
<-
	.print("Now contact the worker sps reconfigurator for these failures: ",FailureDescription);
	//.wait(2000);
	?mission(Mission);
	.find_reconfigurations(Mission,FailureDescription);
.

+discovered(Plan)
<-
	//.print("The worker sps reconfigurator has found a solution: ",Plan)
	//.print("contacting the validator")
	//.wait(2000)
	.validate(Plan);
	.abolish(discovered(Plan));
	//validate_emergency_management(Plan,/*-->*/Problems);
	//notify_commander(Plan,Results,Problems);
.

+validated(Plan_reference,Plan_description)
<-
    //.print("The ",Plan_reference," is valid according the Physical Simulator");
    //.print(Plan_description);
    .term2string(Plan_description,String);
    addSolution(Plan_reference,String);
    .abolish(validated(Plan_reference,Plan_description));
.

+notvalidated(Plan_reference,Plan_description)
<-
    .print(Plan_description);
    .term2string(Plan_description,String);
    addSolutionNotValidated(Plan_reference,String);
.

+selected(Plan_reference)[artifact_name(Id,Name)]
<-
    //.print("The CAPTAIN selected the reconfiguration: ",Plan_reference);
    //.print("Now contacting the enactor");
    .term2string(Plan_ID, Plan_reference);
	.enact(Plan_ID);
	.print(Id);
.

/* soluzioni da modificare - non ottimale - selezione fallimento e selezione switch da dashboard  */

+selected_failure(Fail) : true
<-
    //.print("messages failure: ",Fail);
    .fault(Fail);
    .abolish(selected_failure(Fail));
.

+selected_switch(Switch) : true
<-
    .print("messages Switch: ",Switch);
    .enact_single(Switch);
    .abolish(selected_switch(Switch));
.


+tick(Param) [artifact_name(Id,Name)]
<-
    .print("tick...",Param);
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



