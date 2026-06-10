package view;

import engine.Entity;
import oop.graphics.Graphics;

public class ObstacleAvatar extends Avatar {

    public ObstacleAvatar(Entity entity) {
        super(entity);
    }

    @Override
    public void paint(Graphics g) {
        int cell = cellPixel();
        int x = xCellPixel(entity.position().x());
        int y = yCellPixel(entity.position().y());

        g.setColor(g.getColor(255, 0, 0, 150));
        g.fillRect(x, y, cell, cell);

        g.setColor(g.getColor(255, 40, 120, 255));
        g.drawRect(x + 1, y + 1, cell - 2, cell - 2);
    }
}
