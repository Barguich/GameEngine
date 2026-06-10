package view;

import engine.Entity;
import oop.graphics.Graphics;

public class GumAvatar extends Avatar {

    public GumAvatar(Entity entity) {
        super(entity);
    }

    @Override
    public void paint(Graphics g) {
        int size = Math.max(4, cellPixel() / 6);
        int cx = xPixel(entity);
        int cy = yPixel(entity);

        g.setColor(g.getColor(255, 255, 255, 220));
        g.fillOval(cx - size / 2, cy - size / 2, size, size);
    }
}
