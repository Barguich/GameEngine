package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.Game;
import pengo.brain.PlayerStunt;
import pengo.model.Enemy;
import pengo.model.FishBonus;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import pengo.model.Wall;

public class PlayerStuntTest {

    private PengoModel newModel() {
        new Game(10, 10);
        return new PengoModel(Game.grid());
    }

    private PengoPlayer playerAt(int x, int y) {
        PengoPlayer player = new PengoPlayer();
        player.setPosition(Game.grid().new Position(x, y));
        player.setSize(Game.grid().new Dimension(1, 1));
        return player;
    }

    @Test
    public void testSetOrientation() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);

        stunt.set(180);

        assertEquals(180, player.orientation());
    }

    @Test
    public void testSetCell() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);

        var cell = Game.grid().cellAt(Game.grid().new Position(2, 3));

        stunt.set(cell);

        assertEquals(2, player.position().x());
        assertEquals(3, player.position().y());
    }

    @Test
    public void testSetNullCellDoesNothing() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);

        stunt.set((geometry.Grid.Cell) null);

        assertEquals(5, player.position().x());
        assertEquals(5, player.position().y());
    }

    @Test
    public void testSetCoordinates() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);

        stunt.set(10.0, 20.0);

        assertEquals(10.0, player.center().x(), 0.001);
        assertEquals(20.0, player.center().y(), 0.001);
    }

    @Test
    public void testWalkEastDoesNotCrash() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);
        stunt.setSpeed(10);

        assertDoesNotThrow(() -> stunt.walk(0));
        assertNotNull(player.position());
    }

    @Test
    public void testWalkSouthDoesNotCrash() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);

        assertDoesNotThrow(() -> stunt.walk(90));
        assertNotNull(player.position());
    }

    @Test
    public void testWalkWestDoesNotCrash() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);

        assertDoesNotThrow(() -> stunt.walk(180));
        assertNotNull(player.position());
    }

    @Test
    public void testWalkNorthDoesNotCrash() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);

        assertDoesNotThrow(() -> stunt.walk(270));
        assertNotNull(player.position());
    }

    @Test
    public void testDoneStopsMovement() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);

        stunt.walk(0);
        stunt.done();

        assertFalse(stunt.busy());
        assertTrue(player.linearSpeed() == null || player.linearSpeed().norm() == 0);
    }

    @Test
    public void testResetStopsMovement() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);

        stunt.walk(0);
        stunt.reset();

        assertFalse(stunt.busy());
    }

    @Test
    public void testUpdateDoesNotCrashWhenNotMoving() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);

        assertDoesNotThrow(() -> stunt.update(100));
    }

    @Test
    public void testCollisionNullDoesNothing() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);

        assertDoesNotThrow(() -> stunt.collision((pengo.model.Wall) null));
        assertNotNull(player.position());
    }

    @Test
    public void testCollisionWallDoesNotCrash() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);
        stunt.actInDirection(0);

        Wall wall = new Wall();
        wall.setPosition(Game.grid().new Position(6, 5));
        wall.setSize(Game.grid().new Dimension(1, 1));
        model.add(wall);

        assertDoesNotThrow(() -> stunt.collision(wall));
        assertFalse(stunt.busy());
    }

    @Test
    public void testCollisionFishBonusDoesNotCrash() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        FishBonus fish = new FishBonus();
        fish.setPosition(Game.grid().new Position(6, 5));
        fish.setSize(Game.grid().new Dimension(1, 1));
        model.add(fish);

        PlayerStunt stunt = new PlayerStunt(model, player);

        assertDoesNotThrow(() -> stunt.collision(fish));
        assertTrue(fish.consumed() || !model.entities().contains(fish));
    }

    @Test
    public void testCollisionFrozenEnemyDoesNotCrash() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(6, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        model.add(enemy);

        enemy.freeze(1000);

        PlayerStunt stunt = new PlayerStunt(model, player);

        assertDoesNotThrow(() -> stunt.collision(enemy));
        assertFalse(stunt.busy());
    }

    @Test
    public void testCollisionDraggedEnemyDoesNotCrash() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(6, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        model.add(enemy);

        enemy.startDraggedByIce();

        PlayerStunt stunt = new PlayerStunt(model, player);

        assertDoesNotThrow(() -> stunt.collision(enemy));
        assertFalse(stunt.busy());
    }

    @Test
    public void testCollisionDangerousEnemyDoesNotCrash() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(6, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        model.add(enemy);

        PlayerStunt stunt = new PlayerStunt(model, player);

        int livesBefore = player.lives();

        assertDoesNotThrow(() -> stunt.collision(enemy));

        assertTrue(player.lives() <= livesBefore);
        assertFalse(stunt.busy());
    }

    @Test
    public void testCollisionIceBlockDoesNotCrash() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(6, 5));
        block.setSize(Game.grid().new Dimension(1, 1));
        model.add(block);

        PlayerStunt stunt = new PlayerStunt(model, player);

        assertDoesNotThrow(() -> stunt.collision(block));
        assertFalse(stunt.busy());
    }
}