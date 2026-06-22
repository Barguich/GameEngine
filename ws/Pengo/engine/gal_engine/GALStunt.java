package gal_engine;

import java.util.List;

import gal.action.iAllGALActions;
import gal.arguments.Direction;
import geometry.ISU;
import geometry.Grid.Cell;
import model.Entity;
import model.Model;
import model.Stunt;

public class GALStunt extends Stunt implements iAllGALActions {

	private static final double DEFAULT_SPEED_CM_S = 10.0;

	private double speed_cm_s;
	private double max_degPer_ms;
	private double step_cm;
	private double action_ms;

	// CONSTRUCTOR
	public GALStunt(Model model, Entity e) {
		super(model, e);
		this.speed_cm_s = DEFAULT_SPEED_CM_S;
		this.max_degPer_ms = 90.0 / 1000.0;
		this.step_cm = entity.step().x();
		this.action_ms = 0;
	}

	// STEP
	public void setStepLength(double cm) {
		this.step_cm = cm;
	}

	public double stepLength() {
		return this.step_cm;
	}

	// SPEED
	public void setMaxLinearSpeed(double cmPer_s) {
		this.speed_cm_s = cmPer_s;
	}

	public void setMaxAngularSpeed(double degPer_ms) {
		this.max_degPer_ms = degPer_ms;
	}

	public double actionDuration() {
		return action_ms;
	}

	// TICK
	public void tick(double elapsed_ms) {
		if (action_ms <= 0)
			return;

		action_ms -= elapsed_ms;

		if (action_ms <= 0) {
			action_ms = 0;
			entity.stop();
			entity.done();
		}
	}

	// MOVE
	public boolean startMoving(Direction direction, double intensity, double duration_ms) {
		if (action_ms > 0)
			return false;

		Direction absDir = toAbsolute(direction, entity.orientation());
		if (absDir == null)
			return false;

		double speed = speed_cm_s * intensity;
		if (speed <= 0)
			speed = speed_cm_s;

		ISU isu = entity.center().isu();

		switch (absDir.name()) {
			case "N":
				entity.setLinearSpeed(isu.new Vector(0, -speed));
				break;
			case "S":
				entity.setLinearSpeed(isu.new Vector(0, speed));
				break;
			case "E":
				entity.setLinearSpeed(isu.new Vector(speed, 0));
				break;
			case "W":
				entity.setLinearSpeed(isu.new Vector(-speed, 0));
				break;
			default:
				return false;
		}

		action_ms = duration_ms;
		return true;
	}

	// 0=E, 90=S, 180=W, 270=N
	private Direction toAbsolute(Direction d, int orientation) {
		if (d.isAbsolute())
			return d;
		if (d == Direction.H)
			return null;
		int relative;
		if (d == Direction.F)
			relative = 0;
		else if (d == Direction.B)
			relative = 180;
		else if (d == Direction.R)
			relative = 90;
		else if (d == Direction.L)
			relative = -90;
		else
			return null;
		int angle = ((orientation + relative) % 360 + 360) % 360;
		switch (angle) {
			case 0:
				return Direction.E;
			case 90:
				return Direction.S;
			case 180:
				return Direction.W;
			case 270:
				return Direction.N;
			default:
				return null;
		}
	}

	public boolean startTurning(int angle_deg, double intensity) {
		if (action_ms > 0)
			return false;
		double speed = intensity * max_degPer_ms;
		if (speed <= 0)
			speed = max_degPer_ms;
		entity.setAngularSpeed(Math.signum(angle_deg) * speed);
		action_ms = Math.abs(angle_deg) / speed;
		return true;
	}

	@Override
	public void set(double x_cm, double y_cm) {
		entity.setCoord(entity.center().isu().new Coord(x_cm, y_cm));
	}

	@Override
	public void set(int orientation) {
		entity.turnTo(orientation);
	}

	@Override
	public void set(Cell c) {
		entity.setPosition(c.position());
	}

	@Override
	public void collision(Entity e) {
		action_ms = 0;
		entity.stop();
	}

	@Override
	public void done() {
		action_ms = 0;
		entity.stop();
	}

	@Override
	public void collision(List<Entity> others) {
		action_ms = 0;
	}

	@Override
	public void update(long elapsed) {
		tick(elapsed);
	}

	@Override
	public void walk(int degree) {
		Direction dir;
		degree = ((degree % 360) + 360) % 360;
		if (degree == 0)
			dir = Direction.E;
		else if (degree == 90)
			dir = Direction.S;
		else if (degree == 180)
			dir = Direction.W;
		else if (degree == 270)
			dir = Direction.N;
		else
			return;
		entity.turnTo(degree);
		startMoving(dir, 1.0, 1000.0);
	}
}
