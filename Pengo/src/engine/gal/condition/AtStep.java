package engine.gal.condition;

import engine.gal.arguments.Category;
import engine.gal.arguments.Direction;
import engine.model.Entity;

public class AtStep  {

	int nbStep;

	// CONSTRUCTOR

	 AtStep(Direction dir, Category cat, int nbStep){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `AtStep`"); }

	// EVAL

	/**
	 * @apiNote check if the condition AtStep(...) is satisfied by the given entity
	 * @param e = the entity that does the evaluation
	 * @implNote AtStep(...) conditions are intensively used and must be efficient:
	 *           efficiency is perhaps more important than accuracy.
	 * @implNote There is plenty room for optimization here in collaboration with
	 *           the Model and the Bot.
	 */

	 boolean eval(Entity e){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `eval`"); }

}