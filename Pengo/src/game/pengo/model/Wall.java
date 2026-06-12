package game.pengo.model;

import engine.collision.Bounding;
import engine.collision.Rect;
import engine.model.Entity;

public class Wall extends Entity {

    public Wall() {
        super("Wall");
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