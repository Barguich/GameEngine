package game;// == PAC MAN ==

import engine.Circle;
import engine.Entity;
import engine.Game;

public class PacMan extends Entity {

    // CONSTRUCTOR

    public PacMan() {
        super("PacMan");
    }

    // === Task COLLISION ===
    @Override
    public void setBounding() {
        super.setBounding();
        double radius = Game.game().cmPerCell / 4.0;
        bounding.add(new Circle(center(), radius));
    }

}
