package view;

import engine.Entity;
import oop.graphics.Graphics;

public class GhostAvatar extends Avatar {

    public GhostAvatar(Entity entity) {
        super(entity);
    }

    @Override
    public void paint(Graphics g) {
        int cell = cellPixel();
        int size = Math.max(12, cell - 4);
        int x = xPixel(entity) - size / 2;
        int y = yPixel(entity) - size / 2;

        g.setColor(g.getColor(255, 255, 0, 0));
        g.fillOval(x, y, size, size);
        g.fillRect(x, y + size / 2, size, size / 2);

        int foot = Math.max(3, size / 5);
        g.fillOval(x, y + size - foot, foot * 2, foot * 2);
        g.fillOval(x + size / 2 - foot, y + size - foot, foot * 2, foot * 2);
        g.fillOval(x + size - foot * 2, y + size - foot, foot * 2, foot * 2);

        int eye = Math.max(3, size / 5);
        g.setColor(g.getColor(255, 255, 255, 255));
        g.fillOval(x + size / 4 - eye / 2, y + size / 3 - eye / 2, eye, eye);
        g.fillOval(x + 3 * size / 4 - eye / 2, y + size / 3 - eye / 2, eye, eye);

        int pupil = Math.max(2, eye / 2);
        g.setColor(g.getColor(255, 0, 0, 255));
        g.fillOval(x + size / 4 - pupil / 2, y + size / 3 - pupil / 2, pupil, pupil);
        g.fillOval(x + 3 * size / 4 - pupil / 2, y + size / 3 - pupil / 2, pupil, pupil);
    }
}
