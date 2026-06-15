package engine.gal;

import engine.model.Entity;
import engine.gal.aut.Automaton;

public class GALBot {

	// CONSTRUCTOR

	GALBot(Entity e) {
		throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `GALBot`");
	}

	// ENTITY SELECTED BY CONDITON

	Entity selectedEndity;

	void selectedEntity(Entity e) {
		throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `selectedEntity`");
	}

	Entity selected() {
		throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `selected`");
	}

	// AUTOMATON

	Automaton automaton;

	/**
	 * @apiNote change the automaton of the Bot
	 * @impNote Que devient l'état (State) du Bot ?
	 * @param automaton
	 */
	void set(Automaton automaton) {
		throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `set`");
	}

	// TICK & COLLISION & COMPLETED

	/**
	 * @apiNote observe, choose an action and execute it
	 * @implNote The GAL automaton is one way to encode a behaviour
	 * @param elapsed is not used by the automaton
	 */

	void tick(double elapsed) {
		throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `tick`");
	}

	/**
	 * @apiNote stops the current action and then queries the PLC to select a new
	 *          action
	 */
	void collision(Entity impactor, double elapsed_ms) {
	}

	/**
	 * @apiNote notifies the Bot that the action of its Stunt is completed.
	 */
	void completed() {
	}

}
