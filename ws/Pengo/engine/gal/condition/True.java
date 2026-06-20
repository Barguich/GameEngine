package gal.condition;

import model.Entity;

public class True implements iGALCondition {

	// CONSTRUCTOR

	public True() {
	}

	// EVAL
	@Override
	public boolean eval(Entity e) {
		return true;
	}

}
