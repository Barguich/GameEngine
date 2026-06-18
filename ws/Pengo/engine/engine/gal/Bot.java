package engine.gal;

import engine.model.Entity;

public class Bot {

	protected Entity entity;
	private GALStunt stunt;
	private State state;
	private int healthPercent;

	// CONSTRUCTOR
	public Bot(Entity e) {
		this.entity = e;
		this.healthPercent = 100;
	}

	// STUNT
	public void stunt(GALStunt stunt) {
		this.stunt = stunt;
	}

	// STATE
	/**
	 * @apiNote The state can be used
	 *          <UL>
	 *          <LI>to drive the automaton</LI>
	 *          <LI>to select the appropriate avatar</LI>
	 *          </UL>
	 * @return the state of mind of the Bot
	 */
	public State state() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	// HEALTH

	/**
	 * @apiNote 0 &le; health &le; 100
	 */

	// TICK & COLLISION & COMPLETED

	/**
	 * @apiNote wakes up the Bot so that it can take action
	 * @param elapsed_ms
	 */
	public void tick(double elapsed_ms) {

	}

	/**
	 * @apiNote notifies the Bot that a collision has occurred with {@code impactor}
	 *          after {@code elapsed_ms} so that the Bot can take action
	 * @param impactor
	 * @param elapsed_ms
	 */
	public void collision(Entity impactor, double elapsed_ms) {

	}

	/**
	 * @apiNote notifies the Bot that the action of its Stunt is completed.
	 */
	public void completed() {
	}

	public GALStunt stunt() {
		return stunt;
	}
}
