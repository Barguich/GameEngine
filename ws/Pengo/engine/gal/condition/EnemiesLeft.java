package gal.condition;

import model.Entity;
import pengo.model.PengoModel;

public class EnemiesLeft implements iGALCondition {

	private final int maximum;

	public EnemiesLeft(int maximum) {
		this.maximum = maximum;
	}

	@Override
	public boolean eval(Entity entity) {
		return entity != null && entity.model() instanceof PengoModel
				&& ((PengoModel) entity.model()).enemiesRemaining() <= maximum;
	}
}