package jason.stdlib;

import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.Term;

public class find_reconfigurations extends DefaultInternalAction {

    public Object execute(final TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        if (args.length!=2)
            throw new JasonException("The find_reconfigurations command requires the mission_ref and the failure_ref as parameters!");

        Term mission_ref = args[0];
        Term failure_ref = args[1];

        if (mission_ref==null && !mission_ref.isAtom() && failure_ref==null && !failure_ref.isAtom()) {
            throw new JasonException("The mission_ref or the failure_ref is in a wrong format!");
        } else {
            Atom mr = (Atom) mission_ref;
            String mr_string = mr.getFunctor();

            Atom fr = (Atom) failure_ref;
            String fr_string = fr.getFunctor();

            Message m = new Message("achieve", ts.getUserAgArch().getAgName(), "workersystem", "find_reconfigurations("+mr_string+","+fr_string+")");
            ts.getUserAgArch().sendMsg(m);

        }

        return true;
    }
}