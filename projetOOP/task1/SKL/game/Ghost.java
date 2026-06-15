package game;// == GHOST ==

import engine.*;

public class Ghost extends Entity {

    // CONSTRUCTOR

    public Ghost() {
        super("Ghost");
    }

    // === Task COLLISION ===
    @Override
    public void setBounding() {
        super.setBounding();
        double cell = Game.game().cmPerCell;
        double radius = Game.game().cmPerCell / 4.0;
        double width = Game.game().cmPerCell / 2;

        ISU.Coord ghostCenter = center().mkTranslated(isu.new Vector(0, cell / 8.0));
        bounding.add(new Rect(ghostCenter, isu.new Dimension(width, width), orientation()));
        bounding.add(new Circle(ghostCenter.mkTranslated(isu.new Vector(0, -radius)), radius));
    }

}
