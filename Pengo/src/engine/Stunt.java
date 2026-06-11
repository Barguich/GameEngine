package engine;

import java.util.List;

public abstract class Stunt {

	protected Model model;
	protected Entity entity;

	public Stunt(Model model, Entity entity) {
		this.model = model;
		this.entity = entity;
	}

	public void set(double x_cm, double y_cm) {
	}



	public void collision(Entity e) {
	}

	public void done() {
	}

	public void collision(List<Entity> others) {
		if (others == null)
			return;
		for (Entity e : others) {
			collision(e);
		}
	}

}
