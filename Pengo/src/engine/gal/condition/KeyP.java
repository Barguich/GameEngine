package engine.gal.condition;

import engine.gal.Keyboard;
import engine.gal.arguments.Key;
import engine.gal.aut.iGALCondition;
import engine.model.Entity;

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
