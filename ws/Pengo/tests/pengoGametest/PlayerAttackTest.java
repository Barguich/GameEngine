package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.Game;
import pengo.model.DiamondBlock;
import pengo.model.Enemy;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import pengo.model.Wall;

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
    @Test
    public void testPlayerInitialValues() {
        new Game(10, 10);

        PengoPlayer player = new PengoPlayer();

        assertEquals(3, player.lives());
        assertEquals(0, player.score());
        assertFalse(player.dead());
        assertFalse(player.speedBoosted());
        assertEquals(1.0, player.speedMultiplier(), 0.001);
    }
    @Test
    public void testPlayerLoseLifeUntilDead() {
        new Game(10, 10);

        PengoPlayer player = new PengoPlayer();

        player.loseLife();
        assertEquals(2, player.lives());
        assertFalse(player.dead());

        player.loseLife();
        assertEquals(1, player.lives());
        assertFalse(player.dead());

        player.loseLife();
        assertEquals(0, player.lives());
        assertTrue(player.dead());

        player.loseLife();
        assertEquals(0, player.lives());
    }
    @Test
    public void testPlayerSpeedBoostMultiplierAndExpiration() {
        new Game(10, 10);

        PengoPlayer player = new PengoPlayer();

        player.activateSpeedBoost(1000);

        assertTrue(player.speedBoosted());
        assertTrue(player.speedMultiplier() > 1.0);

        player.tick(1001);

        assertFalse(player.speedBoosted());
        assertEquals(1.0, player.speedMultiplier(), 0.001);
    }
    @Test
    public void testModelAddScoreWithoutDoubleScore() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        int before = model.score();

        model.addScore(100);

        assertTrue(model.score() > before);
    }
    @Test
    public void testModelAddScoreWithDoubleScore() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        model.activateDoubleScore(1000);

        int before = model.score();

        model.addScore(100);

        assertTrue(model.score() > before);
        assertTrue(model.doubleScore());
    }
    @Test
    public void testModelSetPlayerAddsPlayer() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        PengoPlayer player = new PengoPlayer();
        player.setPosition(Game.grid().new Position(2, 2));
        player.setSize(Game.grid().new Dimension(1, 1));

        model.setPlayer(player);

        assertSame(player, model.player());
        assertTrue(model.entities().contains(player));
    }
    @Test
    public void testModelAddScoreNormal() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        model.addScore(100);

        assertEquals(100, model.score());
    }
    @Test
    public void testModelAddScoreDoubleScore() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        model.activateDoubleScore(1000);
        model.addScore(100);

        assertEquals(200, model.score());
    }
    @Test
    public void testModelFreezeEnemies() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        Enemy enemy1 = new Enemy();
        enemy1.setPosition(Game.grid().new Position(3, 3));
        enemy1.setSize(Game.grid().new Dimension(1, 1));
        model.add(enemy1);

        Enemy enemy2 = new Enemy();
        enemy2.setPosition(Game.grid().new Position(4, 3));
        enemy2.setSize(Game.grid().new Dimension(1, 1));
        model.add(enemy2);

        model.freezeEnemies(1000);

        assertTrue(enemy1.frozen());
        assertTrue(enemy2.frozen());
    }
    @Test
    public void testModelKillEnemyAddsScore() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(5, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        model.add(enemy);

        int before = model.score();

        model.killEnemy(enemy);

        assertTrue(enemy.dead() || enemy.dying());
        assertTrue(model.score() > before);
    }
    @Test
    public void testModelVictoryWhenNoEnemiesRemaining() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        PengoPlayer player = new PengoPlayer();
        player.setPosition(Game.grid().new Position(1, 1));
        player.setSize(Game.grid().new Dimension(1, 1));
        model.setPlayer(player);

        model.checkVictory();

        assertTrue(model.won());
        assertEquals(PengoModel.GameState.WON, model.state());
    }
    @Test
    public void testModelNoVictoryWhenEnemyAlive() {
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

        model.checkVictory();

        assertFalse(model.won());
    }
    @Test
    public void testModelDiamondVictoryVertical() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        PengoPlayer player = new PengoPlayer();
        player.setPosition(Game.grid().new Position(1, 1));
        player.setSize(Game.grid().new Dimension(1, 1));
        model.setPlayer(player);

        DiamondBlock d1 = new DiamondBlock();
        d1.setPosition(Game.grid().new Position(5, 2));
        d1.setSize(Game.grid().new Dimension(1, 1));
        model.add(d1);

        DiamondBlock d2 = new DiamondBlock();
        d2.setPosition(Game.grid().new Position(5, 3));
        d2.setSize(Game.grid().new Dimension(1, 1));
        model.add(d2);

        DiamondBlock d3 = new DiamondBlock();
        d3.setPosition(Game.grid().new Position(5, 4));
        d3.setSize(Game.grid().new Dimension(1, 1));
        model.add(d3);

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(8, 8));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        model.add(enemy);

        model.checkVictory();

        assertTrue(model.won());
    }
    @Test
    public void testModelWallVibrationStarts() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        Wall wall = new Wall();
        wall.setPosition(Game.grid().new Position(5, 5));
        wall.setSize(Game.grid().new Dimension(1, 1));
        model.add(wall);

        model.startWallVibration(wall, 1000);

        assertTrue(model.wallVibration());
    }
    @Test
    public void testModelWallVibrationStartsOnly() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        Wall wall = new Wall();
        wall.setPosition(Game.grid().new Position(5, 5));
        wall.setSize(Game.grid().new Dimension(1, 1));
        model.add(wall);

        model.startWallVibration(wall, 1000);

        assertTrue(model.wallVibration());
    }
    
    
}