package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.Game;
import pengo.model.DiamondBlock;
import pengo.model.Enemy;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;

public class DiamondBlockTest {
	@Test
	public void testDiamondBlockIsDiamondReturnsTrue() {
	    new Game(10, 10);

	    DiamondBlock diamond = new DiamondBlock();

	    assertTrue(diamond.isDiamond());
	}
	@Test
	public void testDiamondBlockWizzWithoutModelReturnsFalse() {
	    new Game(10, 10);

	    DiamondBlock diamond = new DiamondBlock();

	    boolean result = diamond.wizz();

	    assertFalse(result);
	}
	@Test
	public void testDiamondBlockWizzWithPengoModelWinsGame() {
	    new Game(10, 10);

	    PengoModel model = new PengoModel(Game.grid());

	    PengoPlayer player = new PengoPlayer();
	    player.setPosition(Game.grid().new Position(1, 1));
	    player.setSize(Game.grid().new Dimension(1, 1));
	    model.setPlayer(player);

	    Enemy enemy = new Enemy();
	    enemy.setPosition(Game.grid().new Position(8, 8));
	    enemy.setSize(Game.grid().new Dimension(1, 1));
	    model.add(enemy);

	    DiamondBlock diamond = diamondAt(5, 5);
	    model.add(diamond);

	    boolean result = diamond.wizz();

	    assertTrue(result);
	    assertTrue(model.won());
	    assertEquals(PengoModel.GameState.WON, model.state());
	}
	@Test
	public void testDiamondBlockWizzCanBeCalledSeveralTimes() {
	    new Game(10, 10);

	    PengoModel model = new PengoModel(Game.grid());

	    DiamondBlock diamond = diamondAt(5, 5);
	    model.add(diamond);

	    assertTrue(diamond.wizz());
	    assertTrue(diamond.wizz());

	    assertTrue(model.won());
	}
	@Test
	public void testDiamondBlockReceiveGalHitWithNullDoesNotDestroy() {
	    new Game(10, 10);

	    PengoModel model = new PengoModel(Game.grid());

	    DiamondBlock diamond = diamondAt(5, 5);
	    model.add(diamond);

	    boolean result = diamond.receiveGalHit(null);

	    assertFalse(result);
	    assertFalse(diamond.broken());
	    assertTrue(model.entities().contains(diamond));
	}
	@Test
	public void testDiamondBlockReceiveGalHitMultipleTimesDoesNotDestroy() {
	    new Game(10, 10);

	    PengoModel model = new PengoModel(Game.grid());

	    DiamondBlock diamond = diamondAt(5, 5);
	    model.add(diamond);

	    Enemy enemy = new Enemy();
	    enemy.setPosition(Game.grid().new Position(6, 5));
	    enemy.setSize(Game.grid().new Dimension(1, 1));
	    model.add(enemy);

	    diamond.receiveGalHit(enemy);
	    diamond.receiveGalHit(enemy);
	    diamond.receiveGalHit(enemy);

	    assertFalse(diamond.broken());
	    assertTrue(model.entities().contains(diamond));
	}
	private DiamondBlock diamondAt(int x, int y) {
	    DiamondBlock diamond = new DiamondBlock();
	    diamond.setPosition(Game.grid().new Position(x, y));
	    diamond.setSize(Game.grid().new Dimension(1, 1));
	    return diamond;
	}
	

}