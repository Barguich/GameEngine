package view;

import engine.Entity;
import oop.graphics.Graphics;

public class BossAvatar extends Avatar {

    public BossAvatar(Entity entity) {
        super(entity);
    }

    @Override
    public void paint(Graphics g) {
        int cell = cellPixel();
        int cx = xPixel(entity);
        int cy = yPixel(entity);

        g.setColor(g.getColor(255, 100, 0, 170));
        g.fillRect(cx - cell / 2, cy - 2 * cell, cell, 4 * cell);
        g.fillRect(cx - cell / 2, cy - cell / 2, 3 * cell, cell);
    }
}
