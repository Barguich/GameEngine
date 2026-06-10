package engine;

import org.junit.jupiter.api.Test;

import engine.Grid;
import engine.ISU;
import game.Game;

public class GridTest {

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
    public void test00GridPositions() {
        Grid grid = createGame().grid();

        Grid.Position p1 = grid.new Position(0, 0);
        Grid.Position p2 = grid.new Position(10, 0);
        Grid.Position p3 = grid.new Position(-1, -1);

        verifpos(p1, 0, 0, "position normal");
        verifpos(p2, 0, 0, "position torus");
        verifpos(p3, 9, 9, "position negative");
    }

    @Test
    public void test01CellAt() {
        Grid grid = createGame().grid();

        Grid.Cell cell = grid.cellAt(grid.new Position(12, 13));

        verif(cell != null, "cell null");
        verif(cell.position.equiv(grid.new Position(2, 3)), "cell position");
    }

    @Test
    public void test02MovementAndDistance() {
        Grid grid = createGame().grid();

        Grid.Position p = grid.new Position(2, 3);

        p.translate(grid.new Vector(1, -1));
        verifpos(p, 3, 2, "translate");

        p.translate(grid.new Vector(10, 10));
        verifpos(p, 3, 2, "full torus translate");

        p.moveNorth(3);
        verifpos(p, 3, 9, "moveNorth");

        Grid.Position a = grid.new Position(0, 0);
        Grid.Position b = grid.new Position(3, 4);
        Grid.Position c = grid.new Position(9, 0);

        verifdouble(5, a.distanceTo(b), "distance 3 4 5");
        verifdouble(1, a.distanceTo(c), "torus distance");
    }

    @Test
    public void test03GridISUConversions() {
        Grid grid = createGame().grid();

        Grid.Position p = grid.new Position(2, 3);

        ISU.Coord coord = p.toISUCoord();

        verifdouble(2 * Game.getCmpercell(), coord.x(), "coord x");
        verifdouble(3 * Game.getCmpercell(), coord.y(), "coord y");

        Grid.Position back = coord.toGridPosition();
        verifpos(back, 2, 3, "back conversion");

        Grid.Dimension d = grid.new Dimension(2, 3);
        ISU.Dimension isuDim = d.toISUDimension();

        verifdouble(2 * Game.getCmpercell(), isuDim.x(), "dimension x");
        verifdouble(3 * Game.getCmpercell(), isuDim.y(), "dimension y");
    }

    @Test
    public void test04CenteredConversionAndRotation() {
        Grid grid = createGame().grid();

        Grid.Position p = grid.new Position(2, 3);
        ISU.Coord c = p.toISUCoordCentered();

        verifdouble(2 * Game.getCmpercell() + Game.getCmpercell() / 2, c.x(), "center x");
        verifdouble(3 * Game.getCmpercell() + Game.getCmpercell() / 2, c.y(), "center y");

        verifpos(c.toGridPosition(), 2, 3, "center back");

        Grid.Position center = grid.new Position(2, 2);
        Grid.Position point = grid.new Position(3, 2);

        point.rotateAround(center, 90);

        verifpos(point, 2, 3, "rotate around center");
    }
}