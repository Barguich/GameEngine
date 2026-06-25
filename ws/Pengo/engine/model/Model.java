package model;

import java.util.ArrayList;
import java.util.List;

import geometry.Grid;
import geometry.ISU;

public class Model {

	// Grille du monde utilisée par le modèle
	private Grid grid;

	// Liste des entités présentes dans le jeu
	private List<Entity> entities;

	public Model(Grid grid) {
		this.grid = grid;
		this.entities = new ArrayList<Entity>();
	}

	public Grid grid() {
		return grid;
	}
	//  permet de remplacer la grille quand la map change de taille
	public void setGrid(Grid grid) {
		if (grid == null) {
			return;
		}
		this.grid = grid;
	}
	public List<Entity> entities() {
		return entities;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	// Ajoute une entité au modèle et la place dans la grille
	public void add(Entity e) {
		if (e == null)
			return;

		if (!entities.contains(e)) {
			entities.add(e);
			e.setModel(this);
			e.deploy();
		}
	}

	// Retire une entité du modèle et de sa cellule
	public void remove(Entity e) {
		if (e == null)
			return;

		if (entities.remove(e)) {
			e.retract();
			e.setModel(null);
		}
	}

	// Déplace une entité puis vérifie si ce déplacement crée une collision
	public boolean move(Entity e, ISU.Vector v) {
		if (e == null || v == null)
			return false;

		if (!entities.contains(e)) {
			return false;
		}

		// Déplacement temporaire
		e.translate(v);

		List<Entity> cols = collisions(e);

		if (!cols.isEmpty()) {

			// En cas de collision, on annule le déplacement
			v.scale(-1);
			e.translate(v);

			Entity other = cols.get(0);

			// Les deux entités sont informées de la collision
			e.collision(other);
			other.collision(e);

			return false;
		}

		return true;
	}

	// Retourne toutes les entités en collision avec e
	public List<Entity> collisions(Entity e) {
		List<Entity> result = new ArrayList<Entity>();

		for (Entity other : entities) {

			if (other == e) {
				continue;
			}

			if (!collisionBlocks(e, other)) {
				continue;
			}

			if (e.intersects(other)) {
				result.add(other);
			}
		}

		return result;
	}

	// Retourne les entités situées sur une position de grille
	public List<Entity> entitiesAt(Grid.Position p) {
		List<Entity> result = new ArrayList<Entity>();

		if (p == null)
			return result;

		for (Entity e : entities) {
			if (e.position() != null && e.position().equiv(p)) {
				result.add(e);
			}
		}

		return result;
	}

	public boolean isFree(Grid.Position p) {
		return entitiesAt(p).isEmpty();
	}

	public Entity firstAt(Grid.Position p) {
		List<Entity> list = entitiesAt(p);

		if (list.isEmpty()) {
			return null;
		}

		return list.get(0);
	}

	protected boolean collisionBlocks(Entity mover, Entity other) {
		return true;
	}

	// Mise à jour globale du modèle à chaque tick du jeu
	public void tick(long elapsed) {
		if (elapsed < 0)
			return;

		List<Entity> copy = new ArrayList<Entity>(entities);

		// Les bots choisissent leur action
		for (Entity e : copy) {
			if (entities.contains(e) && e.bot() != null) {
				e.bot().tick(elapsed);
			}
		}

		// Les entités appliquent ensuite leur déplacement
		for (Entity e : copy) {
			if (entities.contains(e)) {
				e.tick(elapsed);
			}
		}
	}

	// Vide complètement le modèle
	public void clear() {
		for (Entity e : new ArrayList<Entity>(entities)) {
			remove(e);
		}
	}

	public boolean got(Entity observer, int value) {
		return false;
	}
}