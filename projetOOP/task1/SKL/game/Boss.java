package game;// = BOSS =

/**
 * //  X
 * //  *XX   |--X--X--*--|
 * //  X
 * //  X
 */

import engine.Entity;
import engine.Game;
import engine.ISU;
import engine.Rect;

/**
 * @implNote Le Boss a la forme d'un `t`
 * @implNote il occupe 1 cellule au dessus de son
 *           centre, 2 au dessous de son centre, et
 *           2 cellules à droite de son centre.
 * @implNote Le Boss a donc une dimension (x=3Cell,y=4Cell)
 * @implNote Cette forme a été choisie pour pouvoir tester les rotations.
 */
public class Boss extends Entity {

    // CONSTRUCTOR

    public Boss() {
        super("Boss");
    }

    // === Task COLLISION ===
    @Override
    public void setBounding() {
        super.setBounding();
        double cell = Game.game().cmPerCell;

        bounding.add(new Rect(center().mkTranslated(isu.new Vector(0, cell/2)), isu.new Dimension(cell, cell*4), orientation()));
        ISU.Coord c = center().mkTranslated(isu.new Vector(cell * 1.5, 0));
        bounding.add(new Rect(c, isu.new Dimension(cell*2, cell), orientation()));

    }

}
