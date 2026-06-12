package engine.gal;

import engine.gal.arguments.Direction;
import engine.model.Entity;

// = Stunt =

public class GALStunt {

	// FIELDS

	Entity entity;

	// CONSTRUCTOR

	 GALStunt(Entity e){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `GALStunt`"); }

	// STEP

	 double step_cm;

	void setStepLength(double cm){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `setStepLength`"); }

	 double stepLength(){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `stepLength`"); }

	// SPEED

	 double max_cmPer_ms;

	void setMaxLinearSpeed(double cmPer_ms){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `setMaxLinearSpeed`"); }

	 double max_degPer_ms;

	void setMaxAngularSpeed(double degPer_ms){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `setMaxAngularSpeed`"); }

	// == DEFAULT IMPLEMENTATION of GAL Actions ==

	/**
	 * @apiNote the remaining time of the action in progress
	 */
	double action_ms;

	 double actionDuration(){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `actionDuration`"); }

	// TICK

	/**
	 * @apiNote The tick regularly provides the elapsed time
	 * @apiNote informs the Bot when action is completed
	 * @param elapsed_ms
	 * @implNote {@code action_ms} is updated according to the {@code elapsed_ms}
	 */
	void tick(double elapsed_ms){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `tick`"); }

	// MOVE

	 boolean startMoving(Direction direction, double intensity, double duration_ms){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `startMoving`"); }

	// TURN

	 boolean startTurning(int angle_deg, double intensity){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `startTurning`"); }

}
