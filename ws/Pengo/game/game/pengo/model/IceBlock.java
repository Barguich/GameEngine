package game.pengo.model;

import collision.Bounding;
import collision.Rect;
import model.BasicStunt;
import model.Entity;

public class IceBlock extends Entity {

    private boolean sliding;
    private int direction;
    private boolean broken;

    public IceBlock() {
        super("IceBlock");

        sliding = false;
        direction = 0;
        broken = false;
    }

    public boolean sliding() {
        return sliding;
    }

    public int direction() {
        return direction;
    }

    public boolean broken() {
        return broken;
    }

    public void startSlide(int direction) {
        this.direction = direction;
        this.sliding = true;

        if (stunt instanceof BasicStunt) {
            ((BasicStunt) stunt).walk(direction);
        }
    }

    public void stopSlide() {
        this.sliding = false;
        stop();
    }

    public void breakBlock() {
        broken = true;

        if (model != null) {
            model.remove(this);
        }
    }

    @Override
    public void collision(Entity e) {
        super.collision(e);

        if (e == null) {
            return;
        }

        if (e instanceof Wall || e instanceof IceBlock) {
            stopSlide();
        }
    }

    @Override
    public void tick(long elapsed) {
        if (broken) {
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
        bounding.add(new Rect(center, size, orientation_degree));
    }
}