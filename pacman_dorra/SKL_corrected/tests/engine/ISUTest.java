package engine;

import org.junit.jupiter.api.Test;

import engine.Grid;
import engine.ISU;
import game.Game;

public class ISUTest {

    private static final double EPS = 1e-6;

    private Game createGame() {
        return new Game(10, 10);
    }

    private void verif(boolean condition, String msg) {
        if (!condition) {
            throw new Error(msg);
        }
    }

    private void verifdouble(double expected, double result, String msg) {
        if (Math.abs(expected - result) > EPS) {
            throw new Error(msg + " expected " + expected + " but got " + result);
        }
    }

    private void verifpos(Grid.Position p, int x, int y, String msg) {
        if (p.x() != x || p.y() != y) {
            throw new Error(msg + " expected (" + x + "," + y + ") but got (" + p.x() + "," + p.y() + ")");
        }
    }

    @Test
    public void test00CoordAndVector() {
        Game game = createGame();
        ISU isu = game.isu();

        ISU.Coord a = isu.new Coord(0, 0);
        ISU.Coord b = isu.new Coord(3, 4);

        verifdouble(5, a.distanceTo(b), "distance");

        ISU.Vector v = isu.new Vector(3, 4);

        verifdouble(5, v.norm(), "norm");

        v.unity();

        verifdouble(1, v.norm(), "unity");
    }

    @Test
    public void test01TranslateToGrid() {
        Game game = createGame();
        ISU isu = game.isu();

        ISU.Coord c = isu.new Coord(0, 0);

        c.translate(isu.new Vector(Game.getCmpercell(), 0));

        Grid.Position p = c.toGridPosition();

        verifpos(p, 1, 0, "coord to grid");
    }

    @Test
    public void test02Rotation() {
        ISU isu = createGame().isu();

        ISU.Coord c = isu.new Coord(1, 0);
        c.rotation(90);

        verifdouble(0, c.x(), "rotation x");
        verifdouble(1, c.y(), "rotation y");

        ISU.Vector v = isu.new Vector(1, 0);
        v.turn(90);

        verifdouble(0, v.x(), "vector x");
        verifdouble(1, v.y(), "vector y");
    }

    @Test
    public void test03CopyVectorTowardDot() {
        ISU isu = createGame().isu();

        ISU.Coord c = isu.new Coord(Game.getCmpercell(), 0);
        ISU.Coord copy = c.mkCopy();

        verif(copy.equals(c), "copy equals");

        ISU.Coord target = isu.new Coord(2 * Game.getCmpercell(), 0);
        ISU.Vector v = c.mkVectorToward(target);

        verifdouble(Game.getCmpercell(), v.norm(), "vector toward");

        ISU.Vector a = isu.new Vector(1, 2);
        ISU.Vector b = isu.new Vector(3, 4);

        verifdouble(11, a.dot(b), "dot product");
    }

    @Test
    public void test04RotateAround() {
        ISU isu = createGame().isu();

        ISU.Coord origin = isu.new Coord(0, 0);
        ISU.Coord p = isu.new Coord(1, 0);

        p.rotateAround(origin, 90);

        verifdouble(0, p.x(), "rotateAround x");
        verifdouble(1, p.y(), "rotateAround y");
    }
}