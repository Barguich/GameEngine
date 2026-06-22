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

        player.attack();
        player.attack();
        player.attack();

        assertTrue(block.broken());
        assertFalse(model.entities().contains(block));
    }
    @Test
    public void testWinWhenAllEnemiesDead() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(5, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        model.add(enemy);

        assertFalse(model.won());

        model.killEnemy(enemy);

        assertTrue(model.won());
        assertEquals(PengoModel.GameState.WON, model.state());
    }
    
}