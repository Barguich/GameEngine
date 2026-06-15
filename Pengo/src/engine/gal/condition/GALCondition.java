package engine.gal.condition;

import engine.gal.arguments.Category;
import engine.gal.arguments.Direction;
import engine.gal.aut.iGALCondition;

public abstract class GALCondition implements iGALCondition  {

   protected  Direction direction;
    protected Category category;

    // CONSTANT

   public static final True TRUE = new True();
}