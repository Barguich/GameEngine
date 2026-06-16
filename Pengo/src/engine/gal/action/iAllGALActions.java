package engine.gal.action;

import engine.gal.arguments.Direction;

public interface iAllGALActions {

	// TODO: à compléter avec les autres actions GAL

	// MOVE

	/**
	 * @apiNote asks for moving
	 *          <UL>
	 *          <LI>at celerity = intensity * maximal celerity
	 *          <LI>during duration (in ms)
	 *          <LI>in the given direction
	 *          </UL>
	 * @param direction
	 * @param intensity   : &in; [0,1] ≃ % but a negative intensity means do not
	 *                    change celerity
	 * @param duration_ms
	 */
	public boolean startMoving(Direction direction, double intensity, double duration_ms);

	// TURN

	/**
	 * @apiNote asks for turning.
	 * @param angle_deg = the desired angle
	 * @param intensity = &in; [0,1] ≃ % of the maximal angular speed
	 */
	public boolean startTurning(int angle_deg, double intensity);

}
