package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.Game;
import pengo.model.DiamondBlock;
import pengo.model.Enemy;
import pengo.model.GoldBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;

public class GoldBlockTest {

    private PengoModel newModel() {
        new Game(10, 10);
        return new PengoModel(Game.grid());
    }

    private Enemy enemyAt(int x, int y) {
        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(x, y));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        return enemy;
    }

    @Test
    public void testGoldBlockFreezesEnemies() {
        PengoModel model = newModel();

        Enemy enemy1 = enemyAt(5, 5);
        Enemy enemy2 = enemyAt(6, 5);

        model.add(enemy1);
        model.add(enemy2);

        GoldBlock gold = new GoldBlock();

        gold.activate(model);

        assertTrue(enemy1.frozen());
        assertTrue(enemy2.frozen());
        assertTrue(gold.active());
    }

    @Test
    public void testGoldBlockActivatesDoubleScore() {
        PengoModel model = newModel();

        GoldBlock gold = new GoldBlock();

        gold.activate(model);

        assertTrue(model.doubleScore());
        assertTrue(gold.active());
    }

    @Test
    public void testDoubleScoreIncreasesKillEnemyScore() {
        PengoModel model = newModel();

        Enemy enemy = enemyAt(5, 5);
        model.add(enemy);

        GoldBlock gold = new GoldBlock();
        gold.activate(model);

        int beforeScore = model.score();

        model.killEnemy(enemy);

        assertTrue(
            model.score() > beforeScore,
            "Le score doit augmenter quand un ennemi est tué"
        );
    }

    @Test
    public void testDoubleScoreGivesMoreThanNormalScore() {
        PengoModel normalModel = newModel();

        Enemy normalEnemy = enemyAt(5, 5);
        normalModel.add(normalEnemy);

        int normalBefore = normalModel.score();
        normalModel.killEnemy(normalEnemy);
        int normalGain = normalModel.score() - normalBefore;

        PengoModel doubleModel = newModel();

        Enemy doubleEnemy = enemyAt(5, 5);
        doubleModel.add(doubleEnemy);

        GoldBlock gold = new GoldBlock();
        gold.activate(doubleModel);

        int doubleBefore = doubleModel.score();
        doubleModel.killEnemy(doubleEnemy);
        int doubleGain = doubleModel.score() - doubleBefore;

        assertTrue(
            doubleGain >= normalGain,
            "Avec le double score, le gain doit être au moins égal au score normal"
        );

        assertTrue(doubleModel.doubleScore());
    }
    @Test
    public void testFreezeExpiresAfterDuration() {
        new Game(10, 10);

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(5, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));

        enemy.freeze(1000);

        assertTrue(enemy.frozen());

        enemy.tick(10_000);
        enemy.tick(1001);

        assertFalse(
            enemy.frozen(),
            "Le freeze doit finir après la durée prévue"
        );
    }
    @Test
    public void testDoubleScoreExpiresAfterDuration() throws Exception {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        model.activateDoubleScore(1000);

        assertTrue(model.doubleScore());

        // On force l'état du modèle à PLAYING.
        java.lang.reflect.Field stateField =
                PengoModel.class.getDeclaredField("state");

        stateField.setAccessible(true);
        stateField.set(model, PengoModel.GameState.PLAYING);

        // On force le timer interne presque à zéro.
        java.lang.reflect.Field remainingField =
                PengoModel.class.getDeclaredField("doubleScoreRemaining");

        remainingField.setAccessible(true);
        remainingField.setLong(model, 1L);

        model.tick(2);

        assertFalse(
            model.doubleScore(),
            "Le double score doit être désactivé après expiration"
        );
    }
    @Test
    public void testGoldBlockCollisionWithEnemyFreezesOnlyThatEnemy() {
        PengoModel model = newModel();

        GoldBlock gold = new GoldBlock();
        gold.setPosition(Game.grid().new Position(5, 5));
        gold.setSize(Game.grid().new Dimension(1, 1));
        model.add(gold);

        Enemy enemy1 = enemyAt(5, 5);
        Enemy enemy2 = enemyAt(7, 5);

        model.add(enemy1);
        model.add(enemy2);

        gold.collision(enemy1);

        assertTrue(enemy1.frozen());
        assertFalse(enemy2.frozen());
        assertTrue(model.doubleScore());
        assertTrue(gold.active());
    }

    @Test
    public void testGoldBlockActiveExpiresAfterDuration() {
        PengoModel model = newModel();

        GoldBlock gold = new GoldBlock();
        model.add(gold);

        gold.activate(model);

        assertTrue(gold.active());

        long duration = model.config().goldFreezeDuration();

        gold.tick(duration + 1);

        assertFalse(gold.active());
    }
    @Test
    public void testGoldBlockIsNotDestructibleByEnemy() {
        GoldBlock gold = new GoldBlock();

        assertFalse(gold.destructibleByEnemy());
    }
    @Test
    public void testGoldBlockActivateNullDoesNothing() {
        GoldBlock gold = new GoldBlock();

        gold.activate(null);

        assertFalse(gold.active());
    }
    @Test
    public void testGoldBlockActiveExpires() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        GoldBlock gold = new GoldBlock();

        gold.activate(model);

        assertTrue(gold.active());

        gold.tick(model.config().goldFreezeDuration() + 1);

        assertFalse(gold.active());
    }
    @Test
    public void testGoldBlockCollisionWithNullDoesNothing() {
        new Game(10, 10);

        GoldBlock gold = new GoldBlock();

        gold.collision(null);

        assertFalse(gold.active());
    }
    @Test
    public void testDiamondBlockIsSpecialNotDestructibleByEnemy() {
        DiamondBlock diamond = new DiamondBlock();

        assertFalse(diamond.destructibleByEnemy());
    }
    @Test
    public void testGoldBlockIsSpecialNotDestructibleByEnemy() {
        GoldBlock gold = new GoldBlock();

        assertFalse(gold.destructibleByEnemy());
    }
    @Test
    public void testDiamondBlockIsNotDestructibleByEnemy() {
        DiamondBlock diamond = new DiamondBlock();

        assertFalse(diamond.destructibleByEnemy());
    }
    @Test
    public void testDiamondBlockReceiveGalHitDoesNotDestroyIt() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        DiamondBlock diamond = new DiamondBlock();
        diamond.setPosition(Game.grid().new Position(5, 5));
        diamond.setSize(Game.grid().new Dimension(1, 1));
        model.add(diamond);

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(6, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        model.add(enemy);

        boolean result = diamond.receiveGalHit(enemy);

        assertFalse(result);
        assertFalse(diamond.broken());
        assertTrue(model.entities().contains(diamond));
    }
    
    
}