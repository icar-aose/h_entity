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
        if (args.length==0)
            throw new JasonException("The find_reconfigurations command requires the failure_ref as parameter!");

        Term failure_ref = args[0];
        if (failure_ref==null && !failure_ref.isAtom()) {
            throw new JasonException("The failure_ref parameter is in a wrong format!");
        } else {
            Atom fr = (Atom) failure_ref;
            String fr_string = fr.getFunctor();

            Message m = new Message("achieve", ts.getUserAgArch().getAgName(), "workersystem", "find_reconfigurations("+fr_string+")");
            ts.getUserAgArch().sendMsg(m);

        }

        return true;
    }
}