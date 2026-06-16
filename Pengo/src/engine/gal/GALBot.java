package engine.gal;

import engine.model.Entity;
import engine.gal.aut.Automaton;

public class GALBot extends Bot {

	private Entity selectedEndity;
	private Automaton automaton;

	// CONSTRUCTOR
	public GALBot(Entity e) {
		super(e);
	}

	// ENTITY SELECTED BY CONDITON
	public void selectedEntity(Entity e) {
		this.selectedEndity = e;
	}

	public Entity selected() {
		return this.selectedEndity;
	}

	// AUTOMATON

	/**
	 * @apiNote change the automaton of the Bot
	 * @impNote Que devient l'état (State) du Bot ?
	 * @param automaton
	 */
	public void set(Automaton automaton) {
		this.automaton = automaton;
	}

	// TICK & COLLISION & COMPLETED

	/**
	 * @apiNote observe, choose an action and execute it
	 * @implNote The GAL automaton is one way to encode a behaviour
	 * @param elapsed is not used by the automaton
	 */
	@Override
	public void tick(double elapsed) {
		if (automaton == null)
			return;
		automaton.step(entity);
	}

	/**
	 * @apiNote stops the current action and then queries the PLC to select a new
	 *          action
	 */
	@Override
	public void collision(Entity impactor, double elapsed_ms) {
		automaton.step(entity);
	}

	/**
	 * @apiNote notifies the Bot that the action of its Stunt is completed.
	 */
	public void completed() {
		automaton.step(entity);
	}

}
