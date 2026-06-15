package engine.gal.aut;

import java.util.List;

import engine.gal.State;

public interface iTransitions {

	List<Transition> get(State state);

	void add(Transition t);
}
