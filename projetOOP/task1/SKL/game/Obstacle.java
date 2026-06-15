package game;

import engine.*;

public class Obstacle extends Entity {

    // CONSTRUCTOR

    public Obstacle(int x_ncell, int y_ncell) {
        super("Obstacle");
        setPosition(grid.new Position(x_ncell, y_ncell));
    }

    // === Task COLLISION ===
    @Override
    public void setBounding() {
        super.setBounding();
        Rect rect = new Rect(center(), isu.new Dimension(Game.game().cmPerCell, Game.game().cmPerCell), 0);
        bounding.add(rect);
    }
}
