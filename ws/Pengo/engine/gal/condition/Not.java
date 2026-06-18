package gal.condition;

import gal.aut.iGALCondition;
import model.Entity;

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
