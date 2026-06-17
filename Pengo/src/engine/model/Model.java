package engine.model;

import java.util.ArrayList;
import java.util.List;

import engine.geometry.Grid;
import engine.geometry.ISU;

public class Model {

	private Grid grid;
	private List<Entity> entities;

	public Model(Grid grid) {
		assert grid != null;

		this.grid = grid;
		this.entities = new ArrayList<Entity>();
	}

	public Grid grid() {
		return grid;
	}

	public List<Entity> entities() {
		return entities;
	}

	public List<Entity> getEntities() {
		return entities;
	}

	public void add(Entity e) {
		assert e != null;

		if (!entities.contains(e)) {
			entities.add(e);
			e.setModel(this);
			e.deploy();
		}
	}

	public void remove(Entity e) {
		assert e != null;

		if (entities.remove(e)) {
			e.retract();
			e.setModel(null);
		}
	}

	public boolean move(Entity e, ISU.Vector v) {
		assert e != null;
		assert v != null;

		if (!entities.contains(e)) {
			return false;
		}

		e.translate(v);

		List<Entity> cols = collisions(e);

		if (!cols.isEmpty()) {
			v.scale(-1);
			e.translate(v);

			Entity other = cols.get(0);

			e.collision(other);
			other.collision(e);
			return false;
		}

		return true;
	}

	public List<Entity> collisions(Entity e) {
		List<Entity> result = new ArrayList<Entity>();

		for (Entity other : entities) {
			if (other != e && e.intersects(other)) {
				result.add(other);
			}
		}

		return result;
	}

	public List<Entity> entitiesAt(Grid.Position p) {
		assert p != null;

		List<Entity> result = new ArrayList<Entity>();

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

	public void tick(long elapsed) {
		assert elapsed >= 0;

		List<Entity> copy = new ArrayList<Entity>(entities);

		// 1) Les Bots réfléchissent
		for (Entity e : copy) {
			if (entities.contains(e) && e.bot() != null) {
				e.bot().tick(elapsed);
			}
		}

		// 2) Les entités exécutent leur déplacement
		for (Entity e : copy) {
			if (entities.contains(e)) {
				e.tick(elapsed);
			}
		}
	}

	public void clear() {
		for (Entity e : new ArrayList<Entity>(entities)) {
			remove(e);
		}
	}
}