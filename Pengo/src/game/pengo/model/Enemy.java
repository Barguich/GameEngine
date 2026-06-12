package game.pengo.model;

import engine.collision.Bounding;
import engine.collision.Circle;
import engine.model.Entity;

public class Enemy extends Entity {

    private boolean frozen;
    private long frozenRemaining;

    private boolean dead;

    public Enemy() {
        super("Enemy");

        this.frozen = false;
        this.frozenRemaining = 0;
        this.dead = false;
    }

    public boolean frozen() {
        return frozen;
    }

    public boolean dead() {
        return dead;
    }

    public void freeze(long duration_ms) {
        assert duration_ms >= 0;

        if (dead) {
            return;
        }

        frozen = true;
        frozenRemaining = duration_ms;
        stop();
    }

    public void unfreeze() {
        if (dead) {
            return;
        }

        frozen = false;
        frozenRemaining = 0;
    }

    public void kill() {
        dead = true;
        frozen = false;
        frozenRemaining = 0;
        stop();

        if (model != null) {
            model.remove(this);
        }
    }

    @Override
    public void tick(long elapsed) {
        assert elapsed >= 0;

        if (dead) {
            return;
        }

        if (frozen) {
            frozenRemaining -= elapsed;

            if (frozenRemaining <= 0) {
                unfreeze();
            }

            return;
        }

        super.tick(elapsed);
    }

    @Override
    public void setBounding() {
        if (center == null || size == null) {
            return;
        }

        bounding = new Bounding();

        double radius = Math.min(size.x(), size.y()) / 2.0;
        bounding.add(new Circle(center, radius));
    }
}