package gal_engine;

import java.util.List;

import engine.Game;
import gal.action.iAllGALActions;
import gal.arguments.Direction;
import geometry.ISU;
import geometry.Grid.Cell;
import model.Entity;
import model.Model;
import model.Stunt;

public class GALStunt extends Stunt implements iAllGALActions {

	private Entity entity;
	private double max_cmPer_ms;
	private double max_degPer_ms;
	private double step_cm;
	private double action_ms;

	public GALStunt(Model model, Entity e) {
		super(model, e);
		this.entity = e;
		step_cm = entity.step().x();

		double DEBUG_SPEED_FACTOR = 1.0;

		max_cmPer_ms = (Game.game().cmPerCell / 1000.0) * DEBUG_SPEED_FACTOR;
		max_degPer_ms = (90.0 / 1000.0) * DEBUG_SPEED_FACTOR;
		action_ms = 0;
	}

	public double actionDuration() {
		return action_ms;
	}

	public void setStepLength(double cm) {
		this.step_cm = cm;
	}

	public double stepLength() {
		return this.step_cm;
	}

	public void setMaxLinearSpeed(double cmPer_ms) {
		max_cmPer_ms = cmPer_ms;
	}

	public void setMaxAngularSpeed(double degPer_ms) {
		this.max_degPer_ms = degPer_ms;
	}

	// TICK
	public void tick(double elapsed_ms) {
		if (action_ms <= 0)
			return;
		action_ms -= elapsed_ms;
		if (action_ms <= 0) {
			action_ms = 0;
			entity.stop();
			if (entity.position() != null) {
				entity.setPosition(entity.position());
			}
			entity.done();
		}
	}

	public boolean startMoving(Direction direction, double intensity, double duration_ms) {
		if (action_ms > 0) {
			System.out.println("MOVE REFUSED");
			return false;
		}

		double speed = (intensity > 0) ? intensity * max_cmPer_ms : max_cmPer_ms;

		ISU isu = entity.center().isu();

		Direction absDir = resolveAbsolute(direction);

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

		System.out.println(
				"START MOVING " +
						direction +
						" speed=" + speed +
						" duration=" + duration_ms);
		action_ms = duration_ms;
		return true;
	}

	private Direction resolveAbsolute(Direction dir) {
		if (dir.isAbsolute() || dir == Direction.H)
			return dir;

		int absAngle = (entity.orientation() + relativeAngle(dir) + 360) % 360;

		switch (absAngle) {
			case 0:
				return Direction.E;
			case 90:
				return Direction.S;
			case 180:
				return Direction.W;
			case 270:
				return Direction.N;
			default:
				if (absAngle < 45 || absAngle >= 315)
					return Direction.E;
				if (absAngle < 135)
					return Direction.S;
				if (absAngle < 225)
					return Direction.W;
				return Direction.N;
		}
	}

	private int relativeAngle(Direction dir) {
		if (dir == Direction.F)
			return 0;
		if (dir == Direction.B)
			return 180;
		if (dir == Direction.R)
			return 90;
		if (dir == Direction.L)
			return 270;
		return 0;
	}

	// TURN
	public boolean startTurning(int angle_deg, double intensity) {
		System.out.println(
				"TURN request orientation="
						+ entity.orientation()
						+ " action_ms="
						+ action_ms);
		if (action_ms > 0) {
			System.out.println("TURN REFUSED");
			return false;
		}
		double speed = (intensity > 0) ? intensity * max_degPer_ms : max_degPer_ms;
		entity.turn(angle_deg);

		System.out.println("TURN APPLIED → new orientation=" + entity.orientation());

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
		entity.snapToGrid();
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
		degree = ((degree % 360) + 360) % 360;
		Direction dir;
		if (degree == 0) {
			dir = Direction.E;
			entity.turnTo(0);
		} else if (degree == 90) {
			dir = Direction.S;
			entity.turnTo(90);
		} else if (degree == 180) {
			dir = Direction.W;
			entity.turnTo(180);
		} else if (degree == 270) {
			dir = Direction.N;
			entity.turnTo(270);
		} else
			return;
		startMoving(dir, 1.0, 1000.0);
	}
}
