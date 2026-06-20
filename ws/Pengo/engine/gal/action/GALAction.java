package gal.action;

public abstract class GALAction implements iGALAction {

	/**
	 * @param intensity &in; [0,1] ≃ %
	 */
	protected double intensity = 1.0;

	// CONSTANT
	public static final Nothing NOTHING = new Nothing();
}
