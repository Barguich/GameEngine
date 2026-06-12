package engine.model;

import java.util.ArrayList;
import java.util.List;

import engine.geometry.Grid;
import engine.geometry.ISU;

public class Model {

    // FIELDS

    private Grid grid;
    private List<Entity> entities;

    // CONSTRUCTOR

    public Model(Grid grid) {
        assert grid != null;

        this.grid = grid;
        this.entities = new ArrayList<Entity>();
    }

    // GETTERS

    public Grid grid() {
        return grid;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Entity> entities() {
        return entities;
    }

    // ADD / REMOVE

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

    // MOVE

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

            e.collision(cols.get(0));

            return false;
        }

        return true;
    }

    // COLLISIONS

    public List<Entity> collisions(Entity e) {
        assert e != null;

        List<Entity> result = new ArrayList<Entity>();

        for (Entity other : entities) {
            if (other != e && e.intersects(other)) {
                result.add(other);
            }
        }

        return result;
    }

    public List<Entity[]> allCollisions() {
        List<Entity[]> result = new ArrayList<Entity[]>();

        for (int i = 0; i < entities.size(); i++) {
            Entity e1 = entities.get(i);

            for (int j = i + 1; j < entities.size(); j++) {
                Entity e2 = entities.get(j);

                if (e1.intersects(e2)) {
                    result.add(new Entity[] { e1, e2 });
                }
            }
        }

        return result;
    }

    // POSITION HELPERS

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

    // TICK

    public void tick(long elapsed_ms) {
        assert elapsed_ms >= 0;

        double dt = elapsed_ms / 1000.0;

        List<Entity> copy = new ArrayList<Entity>(entities);

        for (Entity e : copy) {

            if (!entities.contains(e)) {
                continue;
            }

            // rotation continue
            if (e.angularSpeed() != 0) {
                int angle = (int) Math.round(e.angularSpeed() * dt);
                e.turn(angle);
            }

            // déplacement continu
            ISU.Vector speed = e.linearSpeed();

            if (speed != null && speed.norm() != 0) {

                ISU.Vector movement = e.center().isu().new Vector(
                    speed.x() * dt,
                    speed.y() * dt
                );

                boolean moved = move(e, movement);

                if (!moved) {
                    e.stop();
                }
            }
        }
    }

    // CLEAR

    public void clear() {
        for (Entity e : new ArrayList<Entity>(entities)) {
            remove(e);
        }
    }
}