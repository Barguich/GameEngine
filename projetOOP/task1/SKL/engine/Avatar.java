package engine;

import oop.graphics.Canvas;
import oop.graphics.Graphics;

public abstract class Avatar {
    protected Entity entity;

    public Avatar(Entity entity) {
        this.entity = entity;
    }

    public abstract void paint(Canvas canvas, Graphics g);
}
