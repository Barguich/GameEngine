package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.Game;
import pengo.model.Enemy;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.Wall;

public class IceBlocktest {

    @Test
    public void testKillEnemyMethod() {

        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(5, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));

        model.add(enemy);

        model.killEnemy(enemy);

        assertTrue(enemy.dead());
        assertFalse(model.entities().contains(enemy));
        assertEquals(100, model.score());
    }
    @Test
    public void testIceBlockKillsEnemyAndScore() {

        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(5, 5));
        block.setSize(Game.grid().new Dimension(1, 1));

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(6, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));

        Wall wall = new Wall();
        wall.setPosition(Game.grid().new Position(7, 5));
        wall.setSize(Game.grid().new Dimension(1, 1));

        model.add(block);
        model.add(enemy);
        model.add(wall);

        block.startSlide(0);
        block.collision(enemy);

        assertTrue(enemy.dead());
        assertFalse(model.entities().contains(enemy));
        assertEquals(100, model.score());
    }


    @Test
    public void testIceBlockStopsWhenTouchingAnotherIceBlock() {

        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        IceBlock block1 = new IceBlock();
        block1.setPosition(Game.grid().new Position(5, 5));
        block1.setSize(Game.grid().new Dimension(1, 1));

        IceBlock block2 = new IceBlock();
        block2.setPosition(Game.grid().new Position(6, 5));
        block2.setSize(Game.grid().new Dimension(1, 1));

        model.add(block1);
        model.add(block2);

        block1.startSlide(0);

        assertTrue(block1.sliding());

        block1.collision(block2);

        assertFalse(block1.sliding());
    }
    //iceblock detruit 
 
    @Test
    public void testIceBlockNeedsThreeHitsToBreak() {

        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(5, 5));
        block.setSize(Game.grid().new Dimension(1, 1));

        model.add(block);
        block.damage();

        assertEquals(2, block.hp());
        assertFalse(block.broken());
        assertTrue(model.entities().contains(block));
        block.damage();
        assertEquals(1, block.hp());
        assertFalse(block.broken());
        assertTrue(model.entities().contains(block));

        block.damage();

        assertEquals(0, block.hp());
        assertTrue(block.broken());
        assertFalse(model.entities().contains(block));
    
}
    @Test
    public void testDamageOnAlreadyBrokenBlockDoesNothing() {

        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(5, 5));
        block.setSize(Game.grid().new Dimension(1, 1));

        model.add(block);

        block.damage();
        block.damage();
        block.damage();

        int hp = block.hp();

        block.damage();

        assertEquals(hp, block.hp());
        assertTrue(block.broken());
    }
    //ennemie emporte si la case deriere lui est vide 
    @Test
    public void testSlidingIceBlockPushesEnemyIfNextCellFree() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(5, 5));
        block.setSize(Game.grid().new Dimension(1, 1));
        model.add(block);

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(6, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        model.add(enemy);

        block.startSlide(0);
        block.collision(enemy);

        assertFalse(enemy.dead());
        assertEquals(Game.grid().new Position(7, 5), enemy.position());
        assertTrue(model.entities().contains(enemy));
    }
    //ecrasé si la case deriere lui est un obstacle 
    @Test
    public void testSlidingIceBlockKillsEnemyIfBlockedBehind() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(5, 5));
        block.setSize(Game.grid().new Dimension(1, 1));
        model.add(block);

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(6, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        model.add(enemy);

        Wall wall = new Wall();
        wall.setPosition(Game.grid().new Position(7, 5));
        wall.setSize(Game.grid().new Dimension(1, 1));
        model.add(wall);

        block.startSlide(0);
        block.collision(enemy);

        assertTrue(enemy.dead());
        assertFalse(model.entities().contains(enemy));
        assertEquals(100, model.score());
    }
    @Test
    public void testIceBlockPushesEnemyIfCellBehindIsFree() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(5, 5));
        block.setSize(Game.grid().new Dimension(1, 1));

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(6, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));

        model.add(block);
        model.add(enemy);

        block.startSlide(0);
        block.collision(enemy);

        assertFalse(enemy.dead());
        assertEquals(Game.grid().new Position(7, 5), enemy.position());
    }
    @Test
    public void testIceBlockFrictionSlowsDownBlock() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(5, 5));
        block.setSize(Game.grid().new Dimension(1, 1));
        model.add(block);

        block.startSlide(0);

        double speedBefore = block.linearSpeed().norm();

        block.tick(100);

        double speedAfter = block.linearSpeed().norm();

        assertTrue(speedAfter < speedBefore);
        assertTrue(block.sliding());
    }
}