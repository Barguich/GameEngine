package gal.condition;

import gal.arguments.Key;
import gal.aut.iGALCondition;
import gal_engine.Keyboard;
import model.Entity;

public class KeyR implements iGALCondition {

	private final Key key;

	public KeyR(Key key) {
		if (key == null)
			throw new IllegalArgumentException();
		this.key = key;
	}

	@Override
	public boolean eval(Entity e) {
		return Keyboard.self().consumeRelease(key);
	}
}
