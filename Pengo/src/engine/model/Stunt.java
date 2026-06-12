package engine.model;

import java.util.List;

import engine.geometry.Grid.Cell;

public abstract class Stunt {

	protected Model model;
	protected Entity entity;

	public Stunt(Model model, Entity entity) {
		this.model = model;
		this.entity = entity;
	}
	public Entity entity() {
		return entity;
	}
	public Model model() {
		return model;
	}

	public abstract void set(double x_cm, double y_cm) ;
	public abstract void set(int orientation) ;
	public abstract void set(Cell c);
	public abstract void collision(Entity e) ;
	public abstract void done() ;
	public  abstract  void collision(List<Entity> others) ;

}
