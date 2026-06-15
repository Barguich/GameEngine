package game;

import engine.*;
import oop.graphics.Graphics;


public class ObstacleAvatar extends Avatar {

    private static final int SCALE = 10;

    public ObstacleAvatar(View view, Entity entity) {
        super(view, entity);
    }

    @Override
    public void paint(Graphics g) {
        if (entity.center() == null || entity.size() == null) return;
        ISU.Coord c = entity.center();
        ISU.Dimension s = entity.size();
        int x = (int)(c.x() * SCALE);
        int y = (int)(c.y() * SCALE);
        int w = (int)(s.x() * SCALE);
        int h = (int)(s.y() * SCALE);

        // remplissage bleu foncé
        g.setColor(g.getColor(255, 0, 0, 180));
        g.setColor(g.getColor(255, 0, 0, 120));
        g.drawRect(x - w/2, y - h/2, w, h);        // bordure bleu clair pour l'effet de labyrinthe
        g.setColor(g.getColor(255, 100, 100, 255));
        g.drawRect(x - w/2, y - h/2, w, h);
    }
}