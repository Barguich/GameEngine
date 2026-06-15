package game;

import engine.*;
import oop.graphics.Graphics;

public class PacManAvatar extends Avatar {

    private static final int SCALE = 10;
    private int mouthAngle = 0;
    private int mouthDir = 5;  // vitesse d'animation

    public PacManAvatar(View view, Entity entity) {
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
        int r = w / 2;

        // animer la bouche
        mouthAngle += mouthDir;
        if (mouthAngle >= 45) mouthDir = -5;
        if (mouthAngle <= 0)  mouthDir = 5;

        // corps jaune
        g.setColor(g.getColor(255, 255, 255, 0));
        g.fillOval(x - r, y - r, w, h);

        // bouche noire selon orientation
        int orientation = entity.orientation();
        g.setColor(g.getColor(255, 0, 0, 0));
        // dessiner un triangle pour la bouche
        int[] mouthX, mouthY;
        switch (orientation) {
            case 0 -> { // est
                mouthX = new int[]{x, x + r, x + r};
                mouthY = new int[]{y, y - mouthAngle/3, y + mouthAngle/3};
            }
            case 180 -> { // ouest
                mouthX = new int[]{x, x - r, x - r};
                mouthY = new int[]{y, y - mouthAngle/3, y + mouthAngle/3};
            }
            case 90 -> { // sud
                mouthX = new int[]{x, x - mouthAngle/3, x + mouthAngle/3};
                mouthY = new int[]{y, y + r, y + r};
            }
            default -> { // nord
                mouthX = new int[]{x, x - mouthAngle/3, x + mouthAngle/3};
                mouthY = new int[]{y, y - r, y - r};
            }
        }
        g.fillPolygon(mouthX, mouthY, 3);

        // œil noir
        g.setColor(g.getColor(255, 0, 0, 0));
        g.fillOval(x, y - r/2, r/3, r/3);
    }
}