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

// = Stunt =

public class GALStunt extends Stunt implements iAllGALActions {

	// FIELDS

	private Entity entity;
	private double max_cmPer_ms;
	private double max_degPer_ms;
	private double step_cm;
	private double action_ms;

	// CONSTRUCTOR

	public GALStunt(Model model, Entity e) {
		super(model, e);
		this.entity = e;
		step_cm = entity.step().x();
		max_cmPer_ms = Game.game().cmPerCell;
		max_degPer_ms = 90.0 / 1000.0;
		action_ms = 0;
	}

	// STEP
	public void setStepLength(double cm) {
		this.step_cm = cm;
	}

	public double stepLength() {
		return this.step_cm;
	}

	// SPEED
	public void setMaxLinearSpeed(double cmPer_ms) {
		max_cmPer_ms = cmPer_ms;
	}

	public void setMaxAngularSpeed(double degPer_ms) {
		max_degPer_ms = degPer_ms;
	}

	// == DEFAULT IMPLEMENTATION of GAL Actions ==

	/**
	 * @apiNote the remaining time of the action in progress
	 */

	public double actionDuration() {
		return action_ms;
	}

	// TICK

	/**
	 * @apiNote The tick regularly provides the elapsed time
	 * @apiNote informs the Bot when action is completed
	 * @param elapsed_ms
	 * @implNote {@code action_ms} is updated according to the {@code elapsed_ms}
	 */
	public void tick(double elapsed_ms) {
		if (action_ms > 0)
			System.out.println("[GALStunt] tick action_ms=" + action_ms);
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

		double speed = intensity * max_cmPer_ms;

		if (speed <= 0)
			speed = max_cmPer_ms;

		ISU isu = entity.center().isu();

		switch (direction.name()) {
			case "N":
				entity.setLinearSpeed(
						isu.new Vector(0, -speed));
				break;

			case "S":
				entity.setLinearSpeed(
						isu.new Vector(0, speed));
				break;

			case "E":
				entity.setLinearSpeed(
						isu.new Vector(speed, 0));
				break;

			case "W":
				entity.setLinearSpeed(
						isu.new Vector(-speed, 0));
				break;
		}
		action_ms = duration_ms;
		return true;
	}

	// TURN

	public boolean startTurning(int angle_deg, double intensity) {
		if (action_ms > 0)
			return false;
		double speed = intensity * max_degPer_ms;
		if (speed <= 0)
			speed = max_degPer_ms;
		entity.setAngularSpeed(
				Math.signum(angle_deg) * speed);

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
		} else {
			return;
		}

		startMoving(dir, 1.0, 1000.0);
	}

}
