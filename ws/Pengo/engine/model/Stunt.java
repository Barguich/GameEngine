package model;

import java.util.List;

import geometry.Grid.Cell;

public abstract class Stunt {

	// Modèle dans lequel l'entité évolue
	protected Model model;

	// Entité contrôlée par ce stunt
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

	// Positionnement direct dans le repère continu
	public abstract void set(double x_cm, double y_cm);

	// Changement d'orientation de l'entité
	public abstract void set(int orientation);

	// Placement de l'entité dans une cellule de la grille
	public abstract void set(Cell c);

	// Réaction à une collision avec une entité
	public abstract void collision(Entity e);

	// Notification de fin d'action
	public abstract void done();

	// Mise à jour du comportement à chaque tick
	public abstract void update(long elapsed);

	// Réaction à plusieurs collisions détectées en même temps
	public abstract void collision(List<Entity> others);

	// Demande de déplacement dans une direction donnée
	public abstract void walk(int degree);
}