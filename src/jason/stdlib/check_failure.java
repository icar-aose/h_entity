package jason.stdlib;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Term;

public class check_failure extends DefaultInternalAction {

    public Object execute(final TransitionSystem ts, Unifier un, Term[] args) throws Exception {

        Message m = new Message("achieve", ts.getUserAgArch().getAgName(), "workersystem", "check_failure");
        ts.getUserAgArch().sendMsg(m);

        return true;
    }
}
