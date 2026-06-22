package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.Game;
import pengo.model.Enemy;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import pengo.model.Wall;

public class WallVibrationTest {

    @Test
    public void testPlayerTouchWallStartsVibration() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        PengoPlayer player = new PengoPlayer();
        player.setPosition(Game.grid().new Position(5, 5));
        player.setSize(Game.grid().new Dimension(1, 1));
        model.setPlayer(player);

        Wall wall = new Wall();
        wall.setPosition(Game.grid().new Position(6, 5));
        wall.setSize(Game.grid().new Dimension(1, 1));
        model.add(wall);

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(7, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        model.add(enemy);

        player.collision(wall);

        assertTrue(model.wallVibration());
        assertTrue(model.isVibrating(enemy));
    }

    @Test
    public void testWallVibrationStopsAfterDuration() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        Wall wall = new Wall();
        wall.setPosition(Game.grid().new Position(5, 5));
        wall.setSize(Game.grid().new Dimension(1, 1));
        model.add(wall);

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(6, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        model.add(enemy);

        model.startWallVibration(wall, 1000);

        assertTrue(model.wallVibration());
        assertTrue(model.isVibrating(enemy));

        model.tick(1001);

        assertFalse(model.wallVibration());
        assertFalse(model.isVibrating(enemy));
    }
}