package gal.action;

import gal.arguments.Direction;
import gal_engine.GALStunt;
import model.Entity;
import model.Stunt;

public class Turn extends GALAction {

	private int angle_deg;

	// 3 CONSTRUCTORS

	/**
	 * @param angle_deg &in; [-360,360]
	 * @param intensity &in; [0,1]
	 */
	public Turn(int angle_deg, double intensity) {
		this.angle_deg = angle_deg;
		this.intensity = intensity;
	}

	public Turn(Direction dir) {
		this(dir, 1.0);
	}

	public Turn(int angle_deg) {
		this(angle_deg, 1.0);
	}

	public Turn(double intensity) {
		this(90, intensity);
	}

	public Turn(Direction dir, double intensity) {
		this.intensity = intensity;

		if (dir == Direction.R) {
			angle_deg = 90;
		} else if (dir == Direction.L) {
			angle_deg = -90;
		} else if (dir == Direction.B) {
			angle_deg = 180;
		} else if (dir.isAbsolute()) {
			angle_deg = dir.toAngle();
		} else {
			angle_deg = 90;
		}
	}

	// EXEC
	public boolean exec(Entity e) {
		if (e == null)
			return false;
 
		Stunt s = e.stunt();
 
		if (s instanceof GALStunt) {
			// Passe par le Stunt : respecte action_ms, refuse si déjà occupé,
			// et la rotation est progressive (gérée par tick()).
			boolean started = ((GALStunt) s).startTurning(angle_deg, intensity);
			System.out.println("[Turn] exec angle=" + angle_deg + " started=" + started);
			return started;
		}
 
		e.turn(angle_deg);
		return true;
	}
}
