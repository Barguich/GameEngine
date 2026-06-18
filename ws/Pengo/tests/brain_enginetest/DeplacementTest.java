package brain_enginetest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import brain.AttackBot;
import engine.Game;
import geometry.ISU;
import geometry.Grid.Position;
import model.BasicStunt;
import model.Entity;
import model.Model;
import pengo.model.Enemy;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import pengo.model.Wall;

public class DeplacementTest {

    @Test
    void playerMoveEast() {
        Game game = new Game(10, 10);
        Position p = game.grid().new Position(2, 2);

        PengoPlayer player = new PengoPlayer();
        player.setPosition(p);
        player.setSize(game.grid().new Dimension(1, 1));

        ISU.Coord before = player.center().mkCopy();

        player.moveEast(3.7);

        assertEquals(before.x() + 3.7, player.center().x(), 0.001);
        assertEquals(before.y(), player.center().y(), 0.001);
    }

    @Test
    void playerTorusEast() {
        Game game = new Game(10, 10);
        Position p = game.grid().new Position(9, 5);

        PengoPlayer player = new PengoPlayer();
        player.setPosition(p);
        player.setSize(game.grid().new Dimension(1, 1));

        player.moveEast(3.7);

        assertEquals(0, player.position().x());
        assertEquals(5, player.position().y());
    }

    @Test
    void playerTorusWest() {
        Game game = new Game(10, 10);
        Position p = game.grid().new Position(0, 5);

        PengoPlayer player = new PengoPlayer();
        player.setPosition(p);
        player.setSize(game.grid().new Dimension(1, 1));

        player.moveWest(3.7);

        assertEquals(9, player.position().x());
        assertEquals(5, player.position().y());
    }

    @Test
    void boundingMovesWithPlayer() {
        Game game = new Game(10, 10);
        Position p = game.grid().new Position(2, 2);

        PengoPlayer player = new PengoPlayer();
        player.setPosition(p);
        player.setSize(game.grid().new Dimension(1, 1));

        player.setBounding();

        ISU.Coord before = player.center().mkCopy();

        player.moveEast(3.7);
        player.setBounding();

        assertEquals(before.x() + 3.7, player.center().x(), 0.001);
    }

    @Test
    void playerEnemyFarDoNotIntersect() {
        Game game = new Game(10, 10);

        PengoPlayer player = new PengoPlayer();
        player.setPosition(game.grid().new Position(1, 1));
        player.setSize(game.grid().new Dimension(1, 1));

        Enemy enemy = new Enemy();
        enemy.setPosition(game.grid().new Position(8, 8));
        enemy.setSize(game.grid().new Dimension(1, 1));

        assertFalse(player.intersects(enemy));
    }

    @Test
    void playerEnemyCollide() {
        Game game = new Game(10, 10);

        PengoPlayer player = new PengoPlayer();
        player.setPosition(game.grid().new Position(4, 1));
        player.setSize(game.grid().new Dimension(1, 1));

        Enemy enemy = new Enemy();
        enemy.setPosition(game.grid().new Position(4, 1));
        enemy.setSize(game.grid().new Dimension(1, 1));

        assertTrue(player.intersects(enemy));
    }

    @Test
    void intersectionAfterMove() {
        Game game = new Game(10, 10);

        PengoPlayer player = new PengoPlayer();
        player.setPosition(game.grid().new Position(2, 2));
        player.setSize(game.grid().new Dimension(1, 1));

        Enemy enemy = new Enemy();
        enemy.setPosition(game.grid().new Position(3, 2));
        enemy.setSize(game.grid().new Dimension(1, 1));

        assertFalse(player.intersects(enemy));

        player.moveEast(3.7);

        assertTrue(player.intersects(enemy));
    }

    @Test
    void intersectionAfterMoveTorus() {
        Game game = new Game(10, 10);

        PengoPlayer player = new PengoPlayer();
        player.setPosition(game.grid().new Position(9, 2));
        player.setSize(game.grid().new Dimension(1, 1));

        Enemy enemy = new Enemy();
        enemy.setPosition(game.grid().new Position(0, 2));
        enemy.setSize(game.grid().new Dimension(1, 1));

        assertFalse(player.intersects(enemy));

        player.moveEast(3.7);

        assertTrue(player.intersects(enemy));
    }

    @Test
    void enemyTurnUpdatesBounding() {
        Game game = new Game(10, 10);

        Enemy enemy = new Enemy();
        enemy.setPosition(game.grid().new Position(4, 4));
        enemy.setSize(game.grid().new Dimension(1, 1));

        enemy.turn(90);

        assertEquals(90, enemy.orientation());
        assertNotNull(enemy.bounding());
    }

    @Test
    void iceBlockDoesNotIntersectFarPlayer() {
        Game game = new Game(10, 10);

        IceBlock block = new IceBlock();
        block.setPosition(game.grid().new Position(5, 5));
        block.setSize(game.grid().new Dimension(1, 1));

        PengoPlayer player = new PengoPlayer();
        player.setPosition(game.grid().new Position(1, 1));
        player.setSize(game.grid().new Dimension(1, 1));

        assertFalse(block.intersects(player));
    }
    @Test
    void iceBlockIntersectsPlayerSamePosition() {
        Game game = new Game(10, 10);

        IceBlock block = new IceBlock();
        block.setPosition(game.grid().new Position(5, 5));
        block.setSize(game.grid().new Dimension(1, 1));

        PengoPlayer player = new PengoPlayer();
        player.setPosition(game.grid().new Position(5, 5));
        player.setSize(game.grid().new Dimension(1, 1));

        assertTrue(block.intersects(player));
    }

    @Test
    void attackBotMovesTowardPlayer() {
        Game game = new Game(20, 15);
        PengoModel model = new PengoModel(game.grid());

        PengoPlayer player = new PengoPlayer();
        player.setPosition(game.grid().new Position(1, 1));
        player.setSize(game.grid().new Dimension(1, 1));

        Enemy enemy = new Enemy();
        enemy.setPosition(game.grid().new Position(5, 1));
        enemy.setSize(game.grid().new Dimension(1, 1));

        model.setPlayer(player);
        model.add(enemy);

        BasicStunt enemyStunt = new BasicStunt(model, enemy);
        AttackBot enemyBot = new AttackBot(enemyStunt, player);

        enemy.setStunt(enemyStunt);

        enemyBot.think();
        model.tick(1000);

        assertEquals(4, enemy.position().x());
    }

    @Test
    void modelAddEntity() {
        Game game = new Game(10, 10);
        Model model = new Model(game.grid());

        PengoPlayer player = new PengoPlayer();
        player.setPosition(game.grid().new Position(2, 2));
        player.setSize(game.grid().new Dimension(1, 1));

        model.add(player);

        assertTrue(model.entities().contains(player));
        assertTrue(game.grid().cellAt(player.position()).contains(player));
        assertSame(model, player.model());
    }

    @Test
    void modelRemoveEntity() {
        Game game = new Game(10, 10);
        Model model = new Model(game.grid());

        PengoPlayer player = new PengoPlayer();
        player.setPosition(game.grid().new Position(2, 2));
        player.setSize(game.grid().new Dimension(1, 1));

        model.add(player);
        model.remove(player);

        assertFalse(model.entities().contains(player));
        assertFalse(game.grid().cellAt(player.position()).contains(player));
    }

    

    @Test
    void modelTickDetectsCollision() {
        Game game = new Game(20, 15);
        PengoModel model = new PengoModel(game.grid());

        PengoPlayer player = new PengoPlayer();
        player.setPosition(game.grid().new Position(5, 5));
        player.setSize(game.grid().new Dimension(1, 1));

        Enemy enemy = new Enemy();
        enemy.setPosition(game.grid().new Position(5, 5));
        enemy.setSize(game.grid().new Dimension(1, 1));

        model.setPlayer(player);
        model.add(enemy);

        assertTrue(player.intersects(enemy));
    }
}