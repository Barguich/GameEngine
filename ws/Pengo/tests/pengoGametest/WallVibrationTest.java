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
    public void testWallCanVibrate() {
        new Game(10, 10);

        Wall wall = new Wall();
        wall.setPosition(Game.grid().new Position(5, 5));
        wall.setSize(Game.grid().new Dimension(1, 1));

        assertFalse(wall.isVibrating());

        wall.vibrate();

        assertTrue(wall.isVibrating());
        assertNotEquals(0, wall.vibrationOffset());
    }

    @Test
    public void testWallVibrationStopsAfterDuration() {
        new Game(10, 10);

        Wall wall = new Wall();
        wall.setPosition(Game.grid().new Position(5, 5));
        wall.setSize(Game.grid().new Dimension(1, 1));

        wall.vibrate();

        assertTrue(wall.isVibrating());

        wall.tick(201);

        assertFalse(wall.isVibrating());
        assertEquals(0, wall.vibrationOffset());
    }

    @Test
    public void testModelStartWallVibration() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        Wall wall = new Wall();
        wall.setPosition(Game.grid().new Position(5, 5));
        wall.setSize(Game.grid().new Dimension(1, 1));
        model.add(wall);

        model.startWallVibration(wall, 1000);

        assertTrue(
            model.wallVibration(),
            "La vibration globale des murs doit être activée"
        );

        assertTrue(
            model.isVibrating(wall) || wall.isVibrating(),
            "Le mur doit être en vibration"
        );
    }

    @Test
    public void testPlayerTouchWallDoesNotCrash() {
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

        player.collision(wall);

        assertNotNull(player.position());
        assertNotNull(wall.position());
    }
    @Test
    public void testWallVibrationOffsetIsZeroWhenNotVibrating() {
        new Game(10, 10);

        Wall wall = new Wall();

        assertFalse(wall.isVibrating());
        assertEquals(0, wall.vibrationOffset());
    }
    @Test
    public void testWallVibrationOffsetChangesDuringVibration() {
        new Game(10, 10);

        Wall wall = new Wall();

        wall.vibrate();

        int offset1 = wall.vibrationOffset();

        wall.tick(50);

        int offset2 = wall.vibrationOffset();

        assertTrue(wall.isVibrating());
        assertNotEquals(offset1, offset2);
    }
    @Test
    public void testWallBoundingCreated() {
        new Game(10, 10);

        Wall wall = new Wall();
        wall.setPosition(Game.grid().new Position(5, 5));
        wall.setSize(Game.grid().new Dimension(1, 1));

        wall.setBounding();

        assertNotNull(wall.bounding());
    }
}