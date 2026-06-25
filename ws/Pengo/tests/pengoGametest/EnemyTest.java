package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.Game;
import pengo.model.Enemy;
import pengo.model.IceBlock;
import pengo.model.PengoModel;

public class EnemyTest {

    private Enemy enemyAt(int x, int y) {
        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(x, y));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        return enemy;
    }

    @Test
    public void testEnemyFreezeAndExpires() {
        new Game(10, 10);

        Enemy enemy = enemyAt(5, 5);

        enemy.freeze(1000);

        assertTrue(enemy.frozen());
        assertTrue(enemy.harmlessForPlayer());
        assertTrue(enemy.eatableByPlayer());

        enemy.tick(10_000);
        enemy.tick(1001);

        assertFalse(enemy.frozen());
    }

    @Test
    public void testEnemyDraggedState() {
        new Game(10, 10);

        Enemy enemy = enemyAt(5, 5);

        assertFalse(enemy.draggedByIce());

        enemy.startDraggedByIce();

        assertTrue(enemy.draggedByIce());
        assertTrue(enemy.harmlessForPlayer());

        enemy.stopDraggedByIce();

        assertFalse(enemy.draggedByIce());
    }

    @Test
    public void testEnemyCrushedByIceDirection() {
        new Game(10, 10);

        Enemy enemy = enemyAt(5, 5);

        enemy.markCrushedByIce(180);

        assertTrue(enemy.crushedByIce());
        assertEquals(180, enemy.crushDirection());
        assertTrue(enemy.harmlessForPlayer());
    }

    @Test
    public void testEnemyCrushAnimationEndsAndEnemyDies() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        Enemy enemy = enemyAt(5, 5);
        model.add(enemy);

        enemy.markCrushedByIce(0);

        assertTrue(enemy.crushedByIce());

        enemy.tick(1000);

        assertTrue(enemy.dead() || !model.entities().contains(enemy));
    }

    @Test
    public void testEnemyKill() {
        new Game(10, 10);

        Enemy enemy = enemyAt(5, 5);

        enemy.kill();

        assertTrue(enemy.dead() || enemy.dying());
        assertTrue(enemy.harmlessForPlayer());
    }

    @Test
    public void testEnemyPassedOutExpires() {
        new Game(10, 10);

        Enemy enemy = enemyAt(5, 5);

        enemy.passOut(1000);

        assertTrue(enemy.passedOut());
        assertTrue(enemy.harmlessForPlayer());
        assertTrue(enemy.eatableByPlayer());

        enemy.tick(1001);

        assertFalse(enemy.passedOut());
    }

    @Test
    public void testEnemyFrozenState() {
        new Game(10, 10);

        Enemy enemy = enemyAt(5, 5);

        enemy.freeze(1000);

        assertTrue(enemy.frozen());
        assertTrue(enemy.harmlessForPlayer());
        assertTrue(enemy.eatableByPlayer());
    } 

    @Test
    public void testEnemyBreakingThroughBlock() {
        new Game(10, 10);

        Enemy enemy = enemyAt(5, 5);

        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(6, 5));
        block.setSize(Game.grid().new Dimension(1, 1));

        assertFalse(enemy.breakingThrough());

        enemy.beginBreakingThrough(block);

        assertTrue(enemy.breakingThrough());

        enemy.stopBreakingThrough();

        assertFalse(enemy.breakingThrough());
    }
    @Test
    public void testEnemyFrozenIsHarmlessAndEatable() {
        new Game(10, 10);

        Enemy enemy = enemyAt(5, 5);

        enemy.freeze(1000);

        assertTrue(enemy.frozen());
        assertTrue(enemy.harmlessForPlayer());
        assertTrue(enemy.eatableByPlayer());
    }
    @Test
    public void testEnemyDraggedIsHarmlessButNotEatable() {
        new Game(10, 10);

        Enemy enemy = enemyAt(5, 5);

        enemy.startDraggedByIce();

        assertTrue(enemy.draggedByIce());
        assertTrue(enemy.harmlessForPlayer());
        assertFalse(enemy.eatableByPlayer());
    }
    @Test
    public void testEnemyPassedOutIsHarmlessAndEatable() {
        new Game(10, 10);

        Enemy enemy = enemyAt(5, 5);

        enemy.passOut(1000);

        assertTrue(enemy.passedOut());
        assertTrue(enemy.harmlessForPlayer());
        assertTrue(enemy.eatableByPlayer());
    }
    @Test
    public void testEnemyCrushedAnimationValues() {
        new Game(10, 10);

        Enemy enemy = enemyAt(5, 5);

        enemy.markCrushedByIce(90);

        assertTrue(enemy.crushedByIce());
        assertEquals(90, enemy.crushDirection());
        assertTrue(enemy.harmlessForPlayer());
    }
    
}