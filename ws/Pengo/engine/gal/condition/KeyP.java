package gal.condition;

import gal.arguments.Key;
import gal_engine.Keyboard;
import model.Entity;

public class KeyP implements iGALCondition {

	private final Key key;

	public KeyP(Key key) {
		if (key == null)
			throw new IllegalArgumentException();
		this.key = key;
	}

	@Override
	public boolean eval(Entity e) {
		return Keyboard.self().isDown(key);
	}
}
