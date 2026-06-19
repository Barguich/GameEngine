package pengoGametest;
//tester si ca freeze les enneis 
//il dpuble le score 
//fin de freeze + fin de double score 
//kill enemy avec score x2

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.Game;
import pengo.model.Enemy;
import pengo.model.GoldBlock;
import pengo.model.PengoModel;

public class GoldBlockTest {

    @Test
    public void testGoldBlockFreezesEnemies() {

        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(5, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));

        model.add(enemy);

        GoldBlock gold = new GoldBlock();

        gold.activate(model);

        assertTrue(enemy.frozen());
    }

    @Test
    public void testGoldBlockActivatesDoubleScore() {

        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        GoldBlock gold = new GoldBlock();

        gold.activate(model);

        assertTrue(model.doubleScore());
    }

    @Test
    public void testDoubleScoreGives200Points() {

        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(5, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));

        model.add(enemy);

        GoldBlock gold = new GoldBlock();
        gold.activate(model);

        model.killEnemy(enemy);

        assertEquals(200, model.score());
    }

    @Test
    public void testFreezeExpiresAfterDuration() {

        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(5, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));

        model.add(enemy);

        model.freezeEnemies(1000);

        assertTrue(enemy.frozen());

        enemy.tick(1001);

        assertFalse(enemy.frozen());
    }

    @Test
    public void testDoubleScoreExpiresAfterDuration() {

        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        model.activateDoubleScore(1000);

        assertTrue(model.doubleScore());

        model.tick(1001);

        assertFalse(model.doubleScore());
    }
}