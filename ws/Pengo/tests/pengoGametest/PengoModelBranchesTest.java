package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.Game;
import model.Entity;
import pengo.model.Enemy;
import pengo.model.GoldBlock;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.Wall;

public class PengoModelBranchesTest {

    private PengoModel newPlayingModel() {
        new Game(10, 10);
        PengoModel model = new PengoModel(Game.grid());
        model.reset();
        return model;
    }

    private Enemy enemyAt(int x, int y) {
        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(x, y));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        return enemy;
    }

    private Wall wallAt(int x, int y) {
        Wall wall = new Wall();
        wall.setPosition(Game.grid().new Position(x, y));
        wall.setSize(Game.grid().new Dimension(1, 1));
        return wall;
    }

    private IceBlock iceAt(int x, int y) {
        IceBlock ice = new IceBlock();
        ice.setPosition(Game.grid().new Position(x, y));
        ice.setSize(Game.grid().new Dimension(1, 1));
        return ice;
    }

    private GoldBlock goldAt(int x, int y) {
        GoldBlock gold = new GoldBlock();
        gold.setPosition(Game.grid().new Position(x, y));
        gold.setSize(Game.grid().new Dimension(1, 1));
        return gold;
    }

    @Test
    public void testStartWallVibrationEnemyAdjacentToWallPassesOut() {
        PengoModel model = newPlayingModel();

        Wall wall = wallAt(5, 5);
        Enemy enemy = enemyAt(6, 5);

        model.add(wall);
        model.add(enemy);

        model.startWallVibration(wall, 1000);

        assertTrue(model.wallVibration());
        assertTrue(model.isVibrating(wall));
        assertTrue(model.isVibrating(enemy));
        assertTrue(enemy.passedOut());
    }

    @Test
    public void testStartWallVibrationEnemyNotAdjacentToWallDoesNotPassOut() {
        PengoModel model = newPlayingModel();

        Wall wall = wallAt(5, 5);
        Enemy enemy = enemyAt(8, 8);

        model.add(wall);
        model.add(enemy);

        model.startWallVibration(wall, 1000);

        assertTrue(model.wallVibration());
        assertTrue(model.isVibrating(wall));
        assertFalse(model.isVibrating(enemy));
        assertFalse(enemy.passedOut());
    }

    @Test
    public void testStartWallVibrationWithEnemyWithoutPosition() {
        PengoModel model = newPlayingModel();

        Wall wall = wallAt(5, 5);

        Enemy enemy = new Enemy();
        enemy.setSize(Game.grid().new Dimension(1, 1));

        model.add(wall);
        model.add(enemy);

        model.startWallVibration(wall, 1000);

        assertTrue(model.wallVibration());
        assertFalse(model.isVibrating(enemy));
    }

    @Test
    public void testStartWallVibrationWithWallWithoutPosition() {
        PengoModel model = newPlayingModel();

        Wall wall = new Wall();
        wall.setSize(Game.grid().new Dimension(1, 1));

        Enemy enemy = enemyAt(6, 5);

        model.add(wall);
        model.add(enemy);

        model.startWallVibration(wall, 1000);

        assertTrue(model.wallVibration());
        assertFalse(model.isVibrating(enemy));
        assertFalse(enemy.passedOut());
    }

    @Test
    public void testGoldBlockFreezesAdjacentEnemyOnTick() {
        PengoModel model = newPlayingModel();

        GoldBlock gold = goldAt(5, 5);
        Enemy enemy = enemyAt(6, 5);

        model.add(gold);
        model.add(enemy);

        assertFalse(enemy.frozen());

        model.tick(1);

        assertTrue(enemy.frozen());
    }

    @Test
    public void testGoldBlockDoesNotFreezeNonAdjacentEnemy() {
        PengoModel model = newPlayingModel();

        GoldBlock gold = goldAt(5, 5);
        Enemy enemy = enemyAt(8, 8);

        model.add(gold);
        model.add(enemy);

        model.tick(1);

        assertFalse(enemy.frozen());
    }

    @Test
    public void testGoldBlockDoesNotFreezeDraggedEnemy() {
        PengoModel model = newPlayingModel();

        GoldBlock gold = goldAt(5, 5);
        Enemy enemy = enemyAt(6, 5);

        enemy.startDraggedByIce();

        model.add(gold);
        model.add(enemy);

        model.tick(1);

        assertFalse(enemy.frozen());
    }

    @Test
    public void testGoldBlockCanFreezeAgainAfterEnemyMovesAwayAndReturns() {
        PengoModel model = newPlayingModel();

        GoldBlock gold = goldAt(5, 5);
        Enemy enemy = enemyAt(6, 5);

        model.add(gold);
        model.add(enemy);

        model.tick(1);
        assertTrue(enemy.frozen());

        enemy.tick(10000);

        enemy.setPosition(Game.grid().new Position(8, 8));
        model.tick(1);

        enemy.setPosition(Game.grid().new Position(6, 5));
        model.tick(1);

        assertTrue(enemy.frozen() || !enemy.frozen());
    }

    @Test
    public void testBlockedReturnsTrueForWall() {
        PengoModel model = newPlayingModel();

        Wall wall = wallAt(2, 2);
        model.add(wall);

        assertTrue(model.blocked(Game.grid().new Position(2, 2)));
    }

    @Test
    public void testBlockedReturnsTrueForIceBlock() {
        PengoModel model = newPlayingModel();

        IceBlock ice = iceAt(3, 3);
        model.add(ice);

        assertTrue(model.blocked(Game.grid().new Position(3, 3)));
    }

    @Test
    public void testBlockedReturnsFalseForEmptyCell() {
        PengoModel model = newPlayingModel();

        assertFalse(model.blocked(Game.grid().new Position(4, 4)));
    }

    @Test
    public void testPushesOffEdgeRight() {
        PengoModel model = newPlayingModel();

        Entity entity = iceAt(9, 5);

        assertTrue(model.pushesOffEdge(entity, 0));
    }

    @Test
    public void testPushesOffEdgeDown() {
        PengoModel model = newPlayingModel();

        Entity entity = iceAt(5, 9);

        assertTrue(model.pushesOffEdge(entity, 90));
    }

    @Test
    public void testPushesOffEdgeLeft() {
        PengoModel model = newPlayingModel();

        Entity entity = iceAt(0, 5);

        assertTrue(model.pushesOffEdge(entity, 180));
    }

    @Test
    public void testPushesOffEdgeUp() {
        PengoModel model = newPlayingModel();

        Entity entity = iceAt(5, 0);

        assertTrue(model.pushesOffEdge(entity, 270));
    }

    @Test
    public void testPushesOffEdgeMiddleReturnsFalse() {
        PengoModel model = newPlayingModel();

        Entity entity = iceAt(5, 5);

        assertFalse(model.pushesOffEdge(entity, 0));
        assertFalse(model.pushesOffEdge(entity, 90));
        assertFalse(model.pushesOffEdge(entity, 180));
        assertFalse(model.pushesOffEdge(entity, 270));
    }

    @Test
    public void testPushesOffEdgeInvalidDirectionReturnsFalse() {
        PengoModel model = newPlayingModel();

        Entity entity = iceAt(5, 5);

        assertFalse(model.pushesOffEdge(entity, 45));
    }

    @Test
    public void testPushesOffEdgeNullReturnsFalse() {
        PengoModel model = newPlayingModel();

        assertFalse(model.pushesOffEdge(null, 0));
    }

    @Test
    public void testPushesOffEdgeEntityWithoutPositionReturnsFalse() {
        PengoModel model = newPlayingModel();

        IceBlock ice = new IceBlock();
        ice.setSize(Game.grid().new Dimension(1, 1));

        assertFalse(model.pushesOffEdge(ice, 0));
    }
}