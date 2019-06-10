package jason.stdlib;

import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.Term;

public class check_failure extends DefaultInternalAction {

    public Object execute(final TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        if (args.length==0)
            throw new JasonException("The check_failure command requires the mission_ref as parameter!");

        Term misison_ref = args[0];
        if (misison_ref==null || !misison_ref.isAtom()) {
            throw new JasonException("The mission_ref parameter is in the wrong format!");
        } else {
            Atom fr = (Atom) misison_ref;
            String fr_string = fr.getFunctor();

            Message m = new Message("achieve", ts.getUserAgArch().getAgName(), "workersystem", "check_failure("+fr_string+")");
            ts.getUserAgArch().sendMsg(m);

        }

        return true;
    }
}
