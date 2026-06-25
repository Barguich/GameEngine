package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.Game;
import pengo.model.Enemy;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;

public class PlayerAttackTest {
	@Test
	public void testPlayerAttackDamagesIceBlockInFront() {
	    new Game(10, 10);

	    PengoModel model = new PengoModel(Game.grid());

	    PengoPlayer player = new PengoPlayer();
	    player.setPosition(Game.grid().new Position(5, 5));
	    player.setSize(Game.grid().new Dimension(1, 1));
	    player.turnTo(0); //regarde à droite
	    model.setPlayer(player);

	    IceBlock block = new IceBlock();
	    block.setPosition(Game.grid().new Position(6, 5));
	    block.setSize(Game.grid().new Dimension(1, 1));
	    model.add(block);

	    player.attack();

	    assertEquals(2, block.hp());
	    assertFalse(block.broken());
	    assertTrue(model.entities().contains(block));
	}
    @Test
    public void testPlayerAttackDoesNothingIfNoBlockInFront() {

        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        PengoPlayer player = new PengoPlayer();
        player.setPosition(Game.grid().new Position(5, 5));
        player.setSize(Game.grid().new Dimension(1, 1));
        player.turnTo(0);

        model.setPlayer(player);

        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(8, 5)); // trop loin
        block.setSize(Game.grid().new Dimension(1, 1));

        model.add(block);

        player.attack();

        assertFalse(block.broken());
        assertTrue(model.entities().contains(block));
    }
    @Test
    public void testPlayerAttackThreeTimesDestroysIceBlock() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        PengoPlayer player = new PengoPlayer();
        player.setPosition(Game.grid().new Position(5, 5));
        player.setSize(Game.grid().new Dimension(1, 1));
        player.turnTo(0);
        model.setPlayer(player);

        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(6, 5));
        block.setSize(Game.grid().new Dimension(1, 1));
        model.add(block);

        assertEquals(3, block.hp());

        player.attack();
        block.tick(500);
        model.tick(500);

        player.attack();
        block.tick(500);
        model.tick(500);

        player.attack();
        block.tick(500);
        model.tick(500);

        assertTrue(
            block.broken() || block.hp() <= 0,
            "Après 3 attaques espacées, le bloc doit être cassé"
        );
    }
    @Test
    public void testWinWhenAllEnemiesDead() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        PengoPlayer player = new PengoPlayer();
        player.setPosition(Game.grid().new Position(1, 1));
        player.setSize(Game.grid().new Dimension(1, 1));
        model.setPlayer(player);

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(5, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        model.add(enemy);

        assertFalse(model.won());

        model.killEnemy(enemy);
        model.remove(enemy);

        model.checkVictory();

        assertTrue(model.won());
        assertEquals(PengoModel.GameState.WON, model.state());
    }
    @Test
    public void testPlayerLoseLifeStopsAtZero() {
        new Game(10, 10);

        PengoPlayer player = new PengoPlayer();

        player.loseLife();
        player.loseLife();
        player.loseLife();
        player.loseLife();

        assertEquals(0, player.lives());
        assertTrue(player.dead());
    }
    @Test
    public void testPlayerAddScore() {
        new Game(10, 10);

        PengoPlayer player = new PengoPlayer();

        player.addScore(100);
        player.addScore(50);

        assertEquals(150, player.score());
    }
    @Test
    public void testPlayerSpeedBoostExpires() {
        new Game(10, 10);

        PengoPlayer player = new PengoPlayer();

        player.activateSpeedBoost(1000);

        assertTrue(player.speedBoosted());
        assertTrue(player.speedMultiplier() > 1.0);

        player.tick(1001);

        assertFalse(player.speedBoosted());
        assertEquals(1.0, player.speedMultiplier(), 0.001);
    }
    
}