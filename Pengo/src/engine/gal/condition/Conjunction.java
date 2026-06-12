package engine.gal.condition;

import java.util.LinkedList;
import engine.gal.aut.iGALCondition;
import engine.model.Entity;

class Conjunction {

	// FIELDS

	LinkedList<iGALCondition> conditions;

	// CONSTRUCTOR

	Conjunction() {
		throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `Conjunction`");
	}

	// BUILDER

	void add(iGALCondition c) {
		throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `add`");
	}

	// EVAL
	boolean eval(Entity e) {
		throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `eval`");
	}

}
