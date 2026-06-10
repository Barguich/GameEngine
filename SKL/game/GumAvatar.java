package game;

import engine.*;
import oop.graphics.Graphics;

import java.awt.Color;
import java.awt.Graphics2D;

public class GumAvatar extends Avatar {

    private static final int SCALE = 10;

    public GumAvatar(View view, Entity entity) {
        super(view, entity);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(g.getColor(255, 255, 255, 255));

        ISU.Coord c = entity.center();

        int x = (int) (c.x() * SCALE);
        int y = (int) (c.y() * SCALE);

        g.fillOval(x - 4, y - 4, 8, 8);
    }
}