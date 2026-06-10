package game;

import org.junit.jupiter.api.Test;
import game.Game;

public class GameTest {

    private static final double EPS = 1e-6;

    private void verifint(int expected, int result, String msg) {
        if (expected != result) {
            throw new Error(msg + " expected " + expected + " but got " + result);
        }
    }

    private void verifdouble(double expected, double result, String msg) {
        if (Math.abs(expected - result) > EPS) {
            throw new Error(msg + " expected " + expected + " but got " + result);
        }
    }

    private void verifnotnull(Object obj, String msg) {
        if (obj == null) {
            throw new Error(msg + " is null");
        }
    }

    @Test
    public void test00GameCreation() {
        Game game = new Game(10, 8);

        verifint(10, game.width_ncell(), "width ncell");
        verifint(8, game.height_ncell(), "height ncell");

        verifdouble(10 * Game.getCmpercell(), game.width_cm(), "width cm");
        verifdouble(8 * Game.getCmpercell(), game.height_cm(), "height cm");

        verifnotnull(game.grid(), "grid");
        verifnotnull(game.isu(), "isu");
        verifnotnull(game.pict(), "picture");
    }
}
