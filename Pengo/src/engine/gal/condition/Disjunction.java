package engine.gal.condition;

import engine.gal.aut.iGALCondition;
import engine.model.Entity;

import java.util.LinkedList;

public class Disjunction extends GALCondition {

	// FIELD
	private LinkedList<iGALCondition> conditions;

	// CONSTRUCTOR
	public Disjunction() {
		this.conditions = new LinkedList<>();
	}

	// BUILDER
	public void add(iGALCondition c) {
		conditions.add(c);
	}

	// EVAL
	public boolean eval(Entity e) {
		for (iGALCondition c : conditions) {
			if (c.eval(e))
				return true;
		}
		return false;
	}
}
