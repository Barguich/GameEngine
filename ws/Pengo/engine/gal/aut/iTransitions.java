package gal.aut;

import java.util.List;

import gal_engine.State;

public interface iTransitions {

	List<Transition> get(State state);

	void add(Transition t);
}
