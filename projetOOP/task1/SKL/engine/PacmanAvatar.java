package engine;

import oop.graphics.Canvas;
import oop.graphics.Graphics;

public class PacmanAvatar extends Avatar {

    public PacmanAvatar(Entity e) {
        super(e);
    }

    @Override
    public void paint(Canvas canvas, Graphics g){
        g.setColor(Graphics.Colors.yellow);
        int x = (int)(entity.center().x() * Game.game().pixelPerCm);
        int y = (int)(entity.center().y() * Game.game().pixelPerCm);
        g.fillOval(x, y, 30, 30);
    }

}
