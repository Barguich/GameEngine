package gal.condition;

import arguments.Category;
import arguments.Direction;
import gal.aut.iGALCondition;

public abstract class GALCondition implements iGALCondition {

   protected Direction direction;
   protected Category category;

   // CONSTANT

   public static final True TRUE = new True();
}