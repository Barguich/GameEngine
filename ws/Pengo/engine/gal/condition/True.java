package gal.condition;

import gal.aut.iGALCondition;
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
