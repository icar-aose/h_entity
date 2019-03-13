/* Initial beliefs and rules */

/* Initial goals */

!start.

/* Plans */

+!start 
: 
	true 
<- 
	.print("ciao sono la HEAD di questa h-entity");
	.wait(1000);
	.send(workersystem,tell,"lavora");
	
	//.send("diplomat",tell,"ciao");
	//!start;
.


+alive [source(X)]
:
	true
<-
	.print("messages Alive from ",X);
	.abolish(alive);
.

