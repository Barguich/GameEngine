package gal.condition;

import model.Entity;

import java.util.LinkedList;

public class Conjunction extends GALCondition {

	// FIELDS
	private LinkedList<iGALCondition> conditions;

	// CONSTRUCTOR
	public Conjunction() {
		this.conditions = new LinkedList<>();
	}

	// BUILDER
	public void add(iGALCondition c) {
		conditions.add(c);
	}

	// EVAL
	public boolean eval(Entity e) {
		for (iGALCondition c : conditions) {
			if (c.eval(e) == false)
				return false;
		}
		return true;
	}
}
