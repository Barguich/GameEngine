package action;

import arguments.Direction;
import gal_engine.GALStunt;
import model.Entity;
import model.Stunt;

public class Move extends GALAction {
	// FIELDS
	private final Direction direction;
	private final double duration_ms;

	// CONSTRUCTORS
	public Move(Direction direction) {
		this(direction, 1.0, 1000.0);
	}

	public Move(Direction direction, double intensity, double duration_ms) {
		this.direction = direction;
		this.intensity = intensity;
		this.duration_ms = duration_ms;
	}

	// EXEC
	@Override
	public boolean exec(Entity e) {
		if (e == null)
			return false;
		Stunt s = e.stunt();
		if (!(s instanceof GALStunt))
			return false;
		return ((GALStunt) s).startMoving(direction, intensity, duration_ms);
	}
}
