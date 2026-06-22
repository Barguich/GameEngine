package gal.action;

import model.Entity;

public interface iGALAction {

	/**
	 * @apiNote asks the action to start execution
	 * @param e = the entity which performs the action
	 * @return true if the action can be started
	 */
	public boolean exec(Entity e);
}
