package gal.action;

import model.Entity;

public class Wizz extends GALAction {
	@Override
	public boolean exec(Entity entity) {
		return entity != null && entity.wizz();
	}
}