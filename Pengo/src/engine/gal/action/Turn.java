package engine.gal.action;

import engine.gal.arguments.Direction;
import engine.model.Entity;

public class Turn  {

	int angle_deg;

	// 3 CONSTRUCTORS

	/**
	 * @param angle_deg &in; [-360,360]
	 * @param intensity &in; [0,1]
	 */
	 Turn(int angle_deg, double intensity){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `Turn`"); }

	 Turn(Direction dir){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `Turn`"); }

	 Turn(int angle_deg){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `Turn`"); }

	 Turn(double intensity){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `Turn`"); }

	 Turn(Direction dir, double intensity){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `Turn`"); }

	// EXEC
	 boolean exec(Entity e){ throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `exec`"); }

}