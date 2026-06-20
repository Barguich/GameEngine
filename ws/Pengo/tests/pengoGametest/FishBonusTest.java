package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.Game;
import pengo.model.FishBonus;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
public class FishBonusTest {
	@Test
	public void testFishBonusConsumed() {

	    new Game(10, 10);

	    PengoModel model = new PengoModel(Game.grid());

	    PengoPlayer player = new PengoPlayer();
	    player.setPosition(Game.grid().new Position(5, 5));
	    player.setSize(Game.grid().new Dimension(1, 1));
	    model.setPlayer(player);

	    FishBonus fish = new FishBonus();
	    fish.setPosition(Game.grid().new Position(6, 5));
	    fish.setSize(Game.grid().new Dimension(1, 1));
	    model.add(fish);

	    fish.consume(player);

	    assertTrue(fish.consumed());
	    assertFalse(model.entities().contains(fish));
	}
	@Test
	public void testFishBonusActivatesSpeedBoost() {

	    new Game(10, 10);

	    PengoModel model = new PengoModel(Game.grid());

	    PengoPlayer player = new PengoPlayer();
	    player.setPosition(Game.grid().new Position(5, 5));
	    player.setSize(Game.grid().new Dimension(1, 1));
	    model.setPlayer(player);

	    FishBonus fish = new FishBonus();

	    fish.consume(player);

	    assertTrue(player.speedBoosted());
	    assertEquals(2.0, player.speedMultiplier());
	}
	@Test
	public void testFishBonusSpeedBoostExpires() {

	    new Game(10, 10);

	    PengoPlayer player = new PengoPlayer();

	    player.activateSpeedBoost(8000);

	    assertTrue(player.speedBoosted());
	    assertEquals(2.0, player.speedMultiplier());

	    player.tick(8000);

	    assertFalse(player.speedBoosted());
	    assertEquals(1.0, player.speedMultiplier());
	}

}
