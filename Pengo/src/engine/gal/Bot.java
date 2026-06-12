package engine.gal;

import engine.model.Entity;

public class Bot {

	Entity entity;

	// CONSTRUCTOR

	Bot(Entity e) {
		throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `Bot`");
	}

	// STUNT

	GALStunt stunt;

	void stunt(GALStunt stunt) {
		throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `stunt`");
	}

	// STATE

	State state;

	/**
	 * @apiNote The state can be used
	 *          <UL>
	 *          <LI>to drive the automaton</LI>
	 *          <LI>to select the appropriate avatar</LI>
	 *          </UL>
	 * @return the state of mind of the Bot
	 */
	State state() {
		throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `state`");
	}

	void state(State state) {
		throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `state`");
	}

	// HEALTH

	/**
	 * @apiNote 0 &le; health &le; 100
	 */
	int healthPercent;

	// TICK & COLLISION & COMPLETED

	/**
	 * @apiNote wakes up the Bot so that it can take action
	 * @param elapsed_ms
	 */
	void tick(double elapsed_ms) {

	}

	/**
	 * @apiNote notifies the Bot that a collision has occurred with {@code impactor}
	 *          after {@code elapsed_ms} so that the Bot can take action
	 * @param impactor
	 * @param elapsed_ms
	 */
	void collision(Entity impactor, double elapsed_ms) {

	}

	/**
	 * @apiNote notifies the Bot that the action of its Stunt is completed.
	 */
	void completed() {
	}

}
