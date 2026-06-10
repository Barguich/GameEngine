package game;

import engine.*;
import oop.graphics.Graphics;

public class GhostAvatar extends Avatar {

    private static final int SCALE = 10;
    private int r, g_col, b;

    // constructeur avec couleur par défaut (rouge)
    public GhostAvatar(View view, Entity entity) {
        this(view, entity, 255, 0, 0);
    }

    // constructeur avec couleur personnalisée
    public GhostAvatar(View view, Entity entity, int r, int g_col, int b) {
        super(view, entity);
        this.r = r;
        this.g_col = g_col;
        this.b = b;
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
        int r = w / 2;

        // corps
        g.setColor(g.getColor(255, this.r, this.g_col, this.b));
        g.fillOval(x - r, y - r, w, h);
        g.fillRect(x - r, y, w, r);

        // yeux blancs
        g.setColor(g.getColor(255, 255, 255, 255));
        g.fillOval(x - r/2 - 2, y - r/3, r/2, r/2);
        g.fillOval(x + 2,       y - r/3, r/2, r/2);

        // pupilles bleues
        g.setColor(g.getColor(255, 0, 0, 255));
        g.fillOval(x - r/2,     y - r/3 + 2, r/4, r/4);
        g.fillOval(x + r/4,     y - r/3 + 2, r/4, r/4);
    }
}