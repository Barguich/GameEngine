package engine;// == MAIN ==

import game.PacMan;

public class Main {

    public static void main(String args[]) {

        Game g = new Game(20, 15);
        Model model = new Model();

        PacMan pacman = new PacMan();

        BasicStunt stunt =
                new BasicStunt(model, pacman);

        pacman.stunt(stunt);

        model.add(pacman);

        System.out.println(
                pacman.center().x()
        );

        stunt.walk(0);

        model.tick(1);

        System.out.println(
                pacman.center().x()
        );

// ====================================================
// GAME
// ====================================================

//        assert g.width_ncell == 20;
//        assert g.height_ncell == 15;
//        assert g.width_cm == 74.0;
//        assert g.height_cm == 55.5;
//        assert Game.game() == g;
//
//// ====================================================
//// AXIS
//// ====================================================
//
//        Axis torus = new Axis(true, 20);
//        assert torus.normalize(21) == 1;
//        assert torus.normalize(-1) == 19;
//        assert torus.modp(21, 20) == 1;
//        assert torus.modp(-1, 20) == 19;
//        assert torus.distance(0, 19) == 1;
//        assert torus.distance(5, 7) == 2;
//
//// ====================================================
//// GRID DIMENSION
//// ====================================================
//
//        Grid.Dimension d = g.grid.new Dimension(3, 4);
//        assert d.x() == 3;
//        assert d.y() == 4;
//        Grid.Dimension d2 = g.grid.new Dimension(3, 4);
//        assert d.equals(d2);
//        assert d.equiv(d2);
//
//// ====================================================
//// GRID POSITION
//// ====================================================
//
//        Grid.Position p = g.grid.new Position(3, 5);
//        Grid.Position p2 = p.copy();
//        assert p.equals(p2);
//
//// ====================================================
//// GRID VECTOR
//// ====================================================
//
//        Grid.Vector gv = g.grid.new Vector(2, 3);
//        p.translate(gv);
//        assert p.x() == 5;
//        assert p.y() == 8;
//
//// ====================================================
//// TORE X
//// ====================================================
//
//        Grid.Position p3 = g.grid.new Position(19, 10);
//        p3.translate(g.grid.new Vector(1, 0));
//        assert p3.x() == 0;
//
//// ====================================================
//// DISTANCE GRID
//// ====================================================
//
//        Grid.Position a = g.grid.new Position(0, 0);
//        Grid.Position b = g.grid.new Position(3, 4);
//        assert a.distanceTo(b) == 5.0;
//
//// ====================================================
//// CELL
//// ====================================================
//
//        Entity e1 = new Entity("test");
//        Grid.Cell cell = g.grid.cellAt(g.grid.new Position(0, 0));
//        cell.add(e1);
//        assert cell.contains(e1);
//        cell.remove(e1);
//        assert !cell.contains(e1);
//
//// ====================================================
//// ISU DIMENSION
//// ====================================================
//
//        ISU.Dimension isuDim = g.isu.new Dimension(10, 20);
//        assert isuDim.x() == 10;
//        assert isuDim.y() == 20;
//
//// ====================================================
//// SCALED VECTOR
//// ====================================================
//
//        ISU.Vector sv = isuDim.mkScaledVector(2);
//
//        assert sv.x() == 20;
//        assert sv.y() == 40;
//
//// ====================================================
//// COORD -> GRID
//// ====================================================
//
//        ISU.Coord coord = g.isu.new Coord(7.4, 11.1);
//
//        Grid.Position gp = coord.toGridPosition();
//
//        System.out.println(gp.x());
//        System.out.println(gp.y());
//
//        System.out.println(
//                "coord = " + coord.x() + "," + coord.y()
//        );
//
//        System.out.println(
//                "gp = " + gp.x() + "," + gp.y()
//        );
//        assert gp.x() == 2;
//        assert gp.y() == 3;
//
//// ====================================================
//// COPY COORD
//// ====================================================
//
//        ISU.Coord coordCopy = coord.mkCopy();
//
//        assert coord.equals(coordCopy);
//
//// ====================================================
//// DISTANCE ISU
//// ====================================================
//
//        ISU.Coord c1 = g.isu.new Coord(0, 0);
//
//        ISU.Coord c2 = g.isu.new Coord(3, 4);
//
//        assert c1.distanceTo(c2) == 5;
//
//// ====================================================
//// VECTOR NORM
//// ====================================================
//
//        ISU.Vector v = g.isu.new Vector(3, 4);
//
//        assert v.norm() == 5;
//
//// ====================================================
//// UNITY
//// ====================================================
//
//        v.unity();
//        assert Math.abs(v.norm() - 1) < 0.0001;
//
//// ====================================================
//// DOT PRODUCT
//// ====================================================
//
//        ISU.Vector v1 = g.isu.new Vector(1, 2);
//        ISU.Vector v2 = g.isu.new Vector(3, 4);
//        assert v1.dot(v2) == 11;
//
//// ====================================================
//// SCALE
//// ====================================================
//
//        ISU.Vector v3 = g.isu.new Vector(2, 3);
//        v3.scale(2);
//        assert v3.x() == 4;
//        assert v3.y() == 6;
//
//// ====================================================
//// ADD VECTOR
//// ====================================================
//
//        ISU.Vector va = g.isu.new Vector(1, 2);
//        ISU.Vector vb = g.isu.new Vector(3, 4);
//        va.add(vb);
//        assert va.x() == 4;
//        assert va.y() == 6;
//
//// ====================================================
//// VECTOR TOWARD
//// ====================================================
//
//        ISU.Coord start = g.isu.new Coord(1, 1);
//        ISU.Coord target = g.isu.new Coord(4, 5);
//        ISU.Vector toward = start.mkVectorToward(target);
//        assert toward.x() == 3;
//        assert toward.y() == 4;
//
//// ====================================================
//// ENTITY
//// ====================================================
//
//        Entity pacman = new Entity("PacMan");
//        assert pacman.orientation() == 0;
//        assert pacman.position().x() == 0;
//        assert pacman.position().y() == 0;
//
//// ====================================================
//// ENTITY TURN
//// ====================================================
//
//        pacman.turn(90);
//        assert pacman.orientation() == 90;
//        pacman.turn(300);
//        assert pacman.orientation() == 30;
//
//// ====================================================
//// ENTITY GRID TRANSLATION
//// ====================================================
//
//        pacman.translate(g.grid.new Vector(1, 0));
//        assert pacman.position().x() == 1;
//
//// ====================================================
//// ENTITY SET POSITION
//// ====================================================
//
//        pacman.setPosition(g.grid.new Position(5, 5));
//        assert pacman.position().x() == 5;
//        assert pacman.position().y() == 5;
//
//// ====================================================
//// ENTITY SET COORD
//// ====================================================
//
//        pacman.setCoord(g.isu.new Coord(7.4, 11.1));
//        assert pacman.position().x() == 2;
//        assert pacman.position().y() == 3;
//
//// ====================================================
//// ENTITY MOVE NORTH
//// ====================================================
//
//        pacman.setPosition(g.grid.new Position(0, 0));
//        pacman.moveNorth(1);
//        assert pacman.position().y() == 14;
//
//// ====================================================
//// ENTITY MOVE SOUTH
//// ====================================================
//
//        pacman.moveSouth(1);
//        assert pacman.position().y() == 0;
//
//// ====================================================
//// ENTITY MOVE EAST
//// ====================================================
//
//        double oldX = pacman.center().x();
//        pacman.moveEast(3.7);
//        assert pacman.center().x() == oldX + 3.7;
//
//// ====================================================
//// ENTITY MOVE WEST
//// ====================================================
//
//        double oldX2 = pacman.center().x();
//        pacman.moveWest(3.7);
//        assert pacman.center().x() == oldX2 - 3.7;
//        System.out.println("TOUS LES TESTS SONT PASSÉS");
    }
}
