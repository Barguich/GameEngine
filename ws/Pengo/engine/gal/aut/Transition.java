package gal.aut;

import gal.action.*;
import gal.condition.iGALCondition;
import gal_engine.State;
import model.Entity;

public class Transition {

	// FIELDS

	private State source;
	private iGALCondition condition;
	private iGALAction action;
	private State tgt;

	// CONSTRUCTOR

	public Transition(State source, iGALCondition condition, GALAction action, State target) {
		this.source = source;
		this.tgt = target;
		this.condition = condition;
		this.action = action;
	}

	public State source() {
		return source;
	}

	public State target() {
		return tgt;
	}

	// EXEC

	/**
	 * @apiNote Tries to execute the transition and update the {@code Bot} state
	 * @apiNote The transition is triggerd if
	 *          <UL>
	 *          <LI>The {@code src} state is the current state of the
	 *          {@code Bot}</LI>
	 *          <LI>The {@code condition} is satisfied</LI>
	 *          </UL>
	 * @implNote if the transition is triggered, the {@code Bot} state switches to
	 *           {@code tgt} state.
	 * @param e = the entity which evaluates the condition using its {@code Bot}
	 *          figures (health, state, ...) and triggers the {@code Stunt} action
	 * @return {@code true} if the condition is satisfied and the action can start
	 */
	public boolean exec(Entity e) {
		if (!condition.eval(e))
			return false;
		if (action != null) {
			boolean started = action.exec(e);

			System.out.println("[TRANSITION] " + source() + " -> " + target() + " condition="
					+ condition.getClass().getSimpleName() + " action=" + action.getClass().getSimpleName()
					+ " started=" + started);

			if (!started) {
				// L'action n'a pas pu démarrer (entité déjà occupée) :
				// la transition n'a PAS lieu, on reste dans l'état source.
				return false;
			}

		}
		return true;
	}

}
