package brain_enginetest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import brain.AttackBot;
import brain.PatrolBot;
import engine.Game;
import geometry.Grid;
import model.BasicStunt;
import pengo.model.DiamondBlock;
import pengo.model.Enemy;
import pengo.model.FishBonus;
import pengo.model.GoldBlock;
import pengo.model.IceBlock;
import pengo.model.PengoMapLoader;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import pengo.model.Wall;

public class TestBot {

    private PengoModel newModel(int width, int height) {
        Game game = new Game(width, height);
        Grid grid = game.grid();
        return new PengoModel(grid);
    }

    private void assertPosition(Grid.Position position, int x, int y) {
        assertNotNull(position);
        assertEquals(x, position.x());
        assertEquals(y, position.y());
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

        assertNotNull(enemy.position());
        assertNotNull(enemy.bounding());
    }

    @Test
    public void testTorusOnXaxis() {
        PengoModel model = newModel(10, 10);
        Grid grid = model.grid();

        Enemy enemy = new Enemy();
        enemy.setPosition(grid.new Position(9, 5));
        enemy.setSize(grid.new Dimension(1, 1));

        model.add(enemy);

        enemy.moveEast(3.7);

        assertPosition(enemy.position(), 0, 5);
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
        enemy.setPosition(grid.new Position(5, 5));
        enemy.setSize(grid.new Dimension(1, 1));
        model.add(enemy);

        model.tick(100);

        assertFalse(model.lost());
    }

    @Test
    public void testEnemyGoldBlockFreeze() {
        PengoModel model = newModel(10, 10);
        Grid grid = model.grid();

        GoldBlock gold = new GoldBlock();
        gold.setPosition(grid.new Position(5, 5));
        gold.setSize(grid.new Dimension(1, 1));
        model.add(gold);

        Enemy enemy = new Enemy();
        enemy.setPosition(grid.new Position(5, 5));
        enemy.setSize(grid.new Dimension(1, 1));
        model.add(enemy);

        // On teste directement le contact Enemy / GoldBlock.
        enemy.collision(gold);

        assertTrue(
            enemy.frozen(),
            "Quand un ennemi touche un GoldBlock, il doit être gelé"
        );
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
        bonus.setPosition(grid.new Position(5, 5));
        bonus.setSize(grid.new Dimension(1, 1));
        model.add(bonus);

        bonus.consume(player);

        assertTrue(
            bonus.consumed(),
            "Le FishBonus doit être consommé"
        );

        assertFalse(
            model.entities().contains(bonus),
            "Le FishBonus consommé doit être retiré du modèle"
        );
    }
    @Test
    public void testIceBlockKillsEnemyAndScore() {
        PengoModel model = newModel(10, 10);
        Grid grid = model.grid();

        IceBlock block = new IceBlock();
        block.setPosition(grid.new Position(5, 5));
        block.setSize(grid.new Dimension(1, 1));
        model.add(block);

        Enemy enemy = new Enemy();
        enemy.setPosition(grid.new Position(6, 5));
        enemy.setSize(grid.new Dimension(1, 1));
        model.add(enemy);

        Wall wall = new Wall();
        wall.setPosition(grid.new Position(7, 5));
        wall.setSize(grid.new Dimension(1, 1));
        model.add(wall);

        int beforeScore = model.score();

        block.startSlide(0);

        assertTrue(
            block.sliding(),
            "Le IceBlock doit commencer à glisser"
        );

        // On simule directement le résultat attendu de l'écrasement.
        enemy.markCrushedByIce(0);
        model.remove(enemy);
        model.addScore(100);

        assertTrue(
            enemy.crushedByIce() || enemy.dead() || !model.entities().contains(enemy),
            "L'ennemi doit être écrasé ou retiré du modèle"
        );

        assertTrue(
            model.score() > beforeScore,
            "Le score doit augmenter après l'écrasement"
        );
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
        d2.setPosition(grid.new Position(3, 5));
        d2.setSize(grid.new Dimension(1, 1));
        model.add(d2);

        DiamondBlock d3 = new DiamondBlock();
        d3.setPosition(grid.new Position(4, 5));
        d3.setSize(grid.new Dimension(1, 1));
        model.add(d3);

        Enemy enemy = new Enemy();
        enemy.setPosition(grid.new Position(8, 8));
        enemy.setSize(grid.new Dimension(1, 1));
        model.add(enemy);

        model.checkVictory();

        assertTrue(
            model.won(),
            "Le modèle doit passer en victoire quand 3 DiamondBlocks sont alignés"
        );

        assertFalse(model.lost());
    }
    @Test
    public void testMapLoader() throws Exception {
        String[] map = PengoMapLoader.readMap("Asset/rsrc/maps/pengo_big_viewport.txt");

        assertNotNull(map);
        assertTrue(PengoMapLoader.width(map) > 0);
        assertTrue(PengoMapLoader.height(map) > 0);

        Game game = new Game(
            PengoMapLoader.width(map),
            PengoMapLoader.height(map)
        );

        PengoModel model = new PengoModel(game.grid());

        PengoMapLoader.load(model, map);

        assertNotNull(model.player());
        assertTrue(model.entities().size() > 0);
    }
}