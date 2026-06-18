package engine.gal.aut;

import engine.gal.State;
import java.util.List;

public interface iTransitions {

	List<Transition> get(State state);

	void add(Transition t);
}
