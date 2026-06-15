package game.pengo.model;

import engine.collision.Bounding;
import engine.collision.Rect;
import engine.model.Entity;

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

    // GETTERS

    public boolean sliding() {
        return sliding;
    }

    public int direction() {
        return direction;
    }

    public boolean broken() {
        return broken;
    }

    // SETTERS

    public void startSlide(int direction) {
        this.direction = direction;
        this.sliding = true;
    }

    public void stopSlide() {
        this.sliding = false;
    }

    public void breakBlock() {
        broken = true;

        if (model != null) {
            model.remove(this);
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