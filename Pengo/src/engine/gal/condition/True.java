package engine.gal.condition;

import engine.gal.aut.iGALCondition;
import engine.model.Entity;


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
