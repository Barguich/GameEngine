package engine.gal.condition;

import engine.gal.aut.iGALCondition;
import engine.model.Entity;

public class Not extends GALCondition {

	// FIELD
	private iGALCondition in;

	// CONSTRUCTOR
	public Not(iGALCondition c) {
		this.in = c;
	}

	// EVAL
	public boolean eval(Entity e) {
		return !in.eval(e);
	}
}
