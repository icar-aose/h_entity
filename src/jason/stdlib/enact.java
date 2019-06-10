package jason.stdlib;

import jason.JasonException;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.Message;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.Term;

public class enact extends DefaultInternalAction {

    public Object execute(final TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        if (args.length==0)
            throw new JasonException("The enact command requires the plan_ref as parameter!");

        Term plan_ref = args[0];
        if (plan_ref==null || !plan_ref.isAtom()) {
            throw new JasonException("The plan_ref parameter is in the wrong format!");
        } else {
            Atom fr = (Atom) plan_ref;
            String fr_string = fr.getFunctor();

            Message m = new Message("achieve", ts.getUserAgArch().getAgName(), "workersystem", "enact("+fr_string+")");
            ts.getUserAgArch().sendMsg(m);
        }

        return true;
    }
}