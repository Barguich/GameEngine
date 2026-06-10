package engine;


import org.junit.jupiter.api.Test;

import engine.Entity;
import engine.Grid;
import engine.ISU;
import game.Game;

public class EntityTest {

    private void verifint(int expected, int result, String msg) {
        if (expected != result) {
            throw new Error(msg + " expected " + expected + " but got " + result);
        }
    }

    private void verifpos(Grid.Position p, int x, int y, String msg) {
        if (p.x() != x || p.y() != y) {
            throw new Error(msg + " expected (" + x + "," + y + ") but got (" + p.x() + "," + p.y() + ")");
        }
    }

    private void verifnotnull(Object obj, String msg) {
        if (obj == null) {
            throw new Error(msg + " is null");
        }
    }

    private Game createGame() {
        return new Game(10, 10);
    }

    @Test
    public void test00SetPosition() {
        Grid grid = createGame().grid();

        Entity e = new Entity("Pac-Man");

        e.setPosition(grid.new Position(2, 3));

        verifpos(e.position(), 2, 3, "position");
        verifnotnull(e.center(), "center");
        verifpos(e.center().toGridPosition(), 2, 3, "center position");
    }

    @Test
    public void test01Translate() {
        Game game = createGame();
        Grid grid = game.grid();
        ISU isu = game.isu();

        Entity e = new Entity("Pac-Man");

        e.setPosition(grid.new Position(2, 3));

        e.translate(grid.new Vector(1, -1));
        verifpos(e.position(), 3, 2, "translate grid");

        e.translate(isu.new Vector(Game.getCmpercell(), 0));
        verifpos(e.position(), 4, 2, "translate isu");
    }

    @Test
    public void test02Turn() {
        Entity e = new Entity("Pac-Man");

        verifint(0, e.orientation(), "orientation start");

        e.turn(90);
        verifint(90, e.orientation(), "turn 90");

        e.turn(300);
        verifint(30, e.orientation(), "turn 300");

        e.turn(-60);
        verifint(330, e.orientation(), "turn negative");
    }

    @Test
    public void test03Move() {
        Grid grid = createGame().grid();

        Entity e = new Entity("Pac-Man");

        e.setPosition(grid.new Position(5, 5));
        e.setStep(grid.new Dimension(1, 1));

        e.moveNorth(2);
        verifpos(e.position(), 5, 3, "moveNorth");

        e.moveSouth(4);
        verifpos(e.position(), 5, 7, "moveSouth");

        e.moveEast(Game.getCmpercell());
        verifpos(e.position(), 6, 7, "moveEast");

        e.moveWest(Game.getCmpercell());
        verifpos(e.position(), 5, 7, "moveWest");
    }

    @Test
    public void test04CenterAfterMove() {
        Grid grid = createGame().grid();

        Entity e = new Entity("Pac-Man");

        e.setPosition(grid.new Position(1, 1));

        verifpos(e.center().toGridPosition(), 1, 1, "center after setPosition");

        e.translate(grid.new Vector(1, 0));

        verifpos(e.position(), 2, 1, "position after translate");
        verifpos(e.center().toGridPosition(), 2, 1, "center after translate");
    }
}