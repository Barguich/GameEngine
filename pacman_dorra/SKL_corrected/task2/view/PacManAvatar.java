package view;

import engine.Entity;
import entities.PacMan;
import oop.graphics.Graphics;

public class PacManAvatar extends Avatar {

    public PacManAvatar(Entity entity) {
        super(entity);
    }

    @Override
    public void paint(Graphics g) {
        int cell = cellPixel();
        int size = Math.max(12, cell - 4);
        int cx = xPixel(entity);
        int cy = yPixel(entity);

        int visibleSize = size;
        boolean dying = false;
        boolean moving = false;

        if (entity instanceof PacMan) {
            PacMan pac = (PacMan) entity;
            dying = pac.isDying();
            moving = pac.isMovingForAnimation();

            if (dying) {
                long dt = System.currentTimeMillis() - pac.deathStartMs();
                double factor = Math.max(0.0, 1.0 - dt / 1200.0);
                visibleSize = Math.max(1, (int) Math.round(size * factor));
            }
        }

        int x = cx - visibleSize / 2;
        int y = cy - visibleSize / 2;

        g.setColor(g.getColor(255, 255, 230, 0));
        g.fillOval(x, y, visibleSize, visibleSize);

        if (dying) {
            return;
        }

        if (!moving) {
            return;
        }

        long phase = (System.currentTimeMillis() / 110) % 3;
        if (phase == 0) {
            return;
        }

        int open = phase == 1 ? visibleSize / 5 : visibleSize / 3;
        int o = entity.orientation();
        int[] xs;
        int[] ys;

        if (o == 180) {
            xs = new int[] { cx, x, x };
            ys = new int[] { cy, cy - open, cy + open };
        } else if (o == 90) {
            xs = new int[] { cx, cx - open, cx + open };
            ys = new int[] { cy, y, y };
        } else if (o == 270) {
            xs = new int[] { cx, cx - open, cx + open };
            ys = new int[] { cy, y + visibleSize, y + visibleSize };
        } else {
            xs = new int[] { cx, x + visibleSize, x + visibleSize };
            ys = new int[] { cy, cy - open, cy + open };
        }

        g.setColor(g.getColor(255, 0, 0, 0));
        g.fillPolygon(xs, ys, 3);
    }
}
