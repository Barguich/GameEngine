package game;// == GUM ==

import engine.Circle;
import engine.Entity;
import engine.Game;

public class Gum extends Entity {

    // CONSTRUCTOR

    public Gum() {
        super("Gum");
    }

    // === Task COLLISION ===
    @Override
    public void setBounding() {
        super.setBounding();
        Circle circle = new Circle(center(), Game.game().cmPerCell / 8.0);
        bounding.add(circle);
    }
}
