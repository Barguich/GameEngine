package gal.condition;

import gal.arguments.Category;
import gal.arguments.Direction;

public abstract class GALCondition implements iGALCondition {

	protected Direction direction;
	protected Category category;

	// CONSTANT

	public static final True TRUE = new True();
}
