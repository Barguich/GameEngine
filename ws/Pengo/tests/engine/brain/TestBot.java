package engine.brain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import brain.AttackBot;
import brain.PatrolBot;
import engine.Game;
import geometry.Grid;
import model.BasicStunt;
import pengo.model.*;

public class TestBot {

    private PengoModel newModel(int width, int height) {
        Game game = new Game(width, height);
        Grid grid = game.grid();
        return new PengoModel(grid);
    }

    @Test
    public void testPatrolBotMovesEnemy() {
        PengoModel model = newModel(10, 10);
        Grid grid = model.grid();

        Enemy enemy = new Enemy();
        enemy.setPosition(grid.new Position(5, 5));
        enemy.setSize(grid.new Dimension(1, 1));

        BasicStunt stunt = new BasicStunt(model, enemy);
        enemy.setStunt(stunt);
        PatrolBot bot = new PatrolBot(stunt);

        model.add(enemy);

        bot.think();
        model.tick(1000);

        assertEquals(grid.new Position(6, 5), enemy.position());
    }

    @Test
    public void testTorusOnXaxis() {
        PengoModel model = newModel(10, 10);
        Grid grid = model.grid();

        Enemy enemy = new Enemy();
        enemy.setPosition(grid.new Position(9, 5));
        enemy.setSize(grid.new Dimension(1, 1));

        BasicStunt stunt = new BasicStunt(model, enemy);
        enemy.setStunt(stunt);
        PatrolBot bot = new PatrolBot(stunt);

        model.add(enemy);

        bot.think();
        model.tick(1000);

        assertEquals(grid.new Position(0, 5), enemy.position());
    }

    @Test
    public void testPlayerEnemyLoseLife() {
        PengoModel model = newModel(10, 10);
        Grid grid = model.grid();

        PengoPlayer player = new PengoPlayer();
        player.setPosition(grid.new Position(5, 5));
        player.setSize(grid.new Dimension(1, 1));
        model.setPlayer(player);

        Enemy enemy = new Enemy();
        enemy.setPosition(grid.new Position(7, 5));
        enemy.setSize(grid.new Dimension(1, 1));

        BasicStunt stunt = new BasicStunt(model, enemy);
        enemy.setStunt(stunt);
        AttackBot bot = new AttackBot(stunt, player);

        model.add(enemy);

        bot.think();
        model.tick(1000);

        assertFalse(model.lost());
    }

    @Test
    public void testEnemyGoldBlockFreeze() {
        PengoModel model = newModel(10, 10);
        Grid grid = model.grid();

        PengoPlayer player = new PengoPlayer();
        player.setPosition(grid.new Position(1, 5));
        player.setSize(grid.new Dimension(1, 1));
        model.setPlayer(player);

        GoldBlock gold = new GoldBlock();
        gold.setPosition(grid.new Position(5, 5));
        gold.setSize(grid.new Dimension(1, 1));
        model.add(gold);

        Enemy enemy = new Enemy();
        enemy.setPosition(grid.new Position(7, 5));
        enemy.setSize(grid.new Dimension(1, 1));

        BasicStunt stunt = new BasicStunt(model, enemy);
        enemy.setStunt(stunt);
        AttackBot bot = new AttackBot(stunt, player);

        model.add(enemy);

        bot.think();
        model.tick(1000);

        assertTrue(enemy.frozen());
    }

    @Test
    public void testPlayerFishBonusConsume() {
        PengoModel model = newModel(10, 10);
        Grid grid = model.grid();

        PengoPlayer player = new PengoPlayer();
        player.setPosition(grid.new Position(5, 5));
        player.setSize(grid.new Dimension(1, 1));
        model.setPlayer(player);

        FishBonus bonus = new FishBonus();
        bonus.setPosition(grid.new Position(6, 5));
        bonus.setSize(grid.new Dimension(1, 1));
        model.add(bonus);

        BasicStunt stunt = new BasicStunt(model, player);
        player.setStunt(stunt);

        stunt.walk(0);
        model.tick(1000);

        assertTrue(bonus.consumed());
        assertFalse(model.entities().contains(bonus));
    }

    @Test
    public void testIceBlockKillsEnemyAndScore() {
        PengoModel model = newModel(10, 10);
        Grid grid = model.grid();

        PengoPlayer player = new PengoPlayer();
        player.setPosition(grid.new Position(1, 1));
        player.setSize(grid.new Dimension(1, 1));
        model.setPlayer(player);

        IceBlock block = new IceBlock();
        block.setPosition(grid.new Position(5, 5));
        block.setSize(grid.new Dimension(1, 1));

        BasicStunt blockStunt = new BasicStunt(model, block);
        block.setStunt(blockStunt);
        model.add(block);

        Enemy enemy = new Enemy();
        enemy.setPosition(grid.new Position(6, 5));
        enemy.setSize(grid.new Dimension(1, 1));
        model.add(enemy);

        block.startSlide(0);
        model.tick(1000);

        assertTrue(enemy.dead());
        assertFalse(model.entities().contains(enemy));
        assertEquals(100, model.score());
    }

    @Test
    public void testDiamondVictory() {
        PengoModel model = newModel(10, 10);
        Grid grid = model.grid();

        PengoPlayer player = new PengoPlayer();
        player.setPosition(grid.new Position(1, 1));
        player.setSize(grid.new Dimension(1, 1));
        model.setPlayer(player);

        DiamondBlock d1 = new DiamondBlock();
        d1.setPosition(grid.new Position(2, 5));
        d1.setSize(grid.new Dimension(1, 1));
        model.add(d1);

        DiamondBlock d2 = new DiamondBlock();
        d2.setPosition(grid.new Position(4, 5));
        d2.setSize(grid.new Dimension(1, 1));
        model.add(d2);

        DiamondBlock d3 = new DiamondBlock();
        d3.setPosition(grid.new Position(6, 5));
        d3.setSize(grid.new Dimension(1, 1));
        model.add(d3);

        model.tick(100);

        assertTrue(model.won());
        assertFalse(model.lost());
    }

    @Test
    public void testMapLoader() throws Exception {
        String[] map = PengoMapLoader.readMap("rsrc/maps/lvl1.txt");

        assertEquals(10, PengoMapLoader.width(map));
        assertEquals(5, PengoMapLoader.height(map));

        Game game = new Game(
            PengoMapLoader.width(map),
            PengoMapLoader.height(map)
        );

        PengoModel model = new PengoModel(game.grid());

        PengoMapLoader.load(model, map);

        assertNotNull(model.player());
        assertEquals(game.grid().new Position(1, 1), model.player().position());
        assertEquals(32, model.entities().size());
    }
}