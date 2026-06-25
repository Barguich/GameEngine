package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.Game;
import model.Entity;
import pengo.brain.PengoBots;
import pengo.model.DiamondBlock;
import pengo.model.Enemy;
import pengo.model.FishBonus;
import pengo.model.GoldBlock;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import pengo.model.Wall;

public class PengoBotsTest {

    private PengoModel newModel() {
        new Game(10, 10);
        return new PengoModel(Game.grid());
    }

    @Test
    public void testConfigureNullModelDoesNotCrash() {
        assertDoesNotThrow(() -> PengoBots.configure(null));
    }

    @Test
    public void testConfigureNullEntityDoesNotCrash() {
        PengoModel model = newModel();

        assertDoesNotThrow(() -> PengoBots.configureEntity(model, null));
    }

    @Test
    public void testAttachEnemyAvatarNullDoesNotCrash() {
        assertDoesNotThrow(() -> PengoBots.attachEnemyAvatar(null));
    }

    @Test
    public void testAttachEnemyAvatarAddsAvatar() {
        new Game(10, 10);

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(5, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));

        assertNull(enemy.avatar());

        PengoBots.attachEnemyAvatar(enemy);

        assertNotNull(enemy.avatar());
    }

    @Test
    public void testAttachEnemyAvatarDoesNotReplaceExistingAvatar() {
        new Game(10, 10);

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(5, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));

        PengoBots.attachEnemyAvatar(enemy);

        Object avatarBefore = enemy.avatar();

        PengoBots.attachEnemyAvatar(enemy);

        assertSame(avatarBefore, enemy.avatar());
    }

    @Test
    public void testConfigurePlayerDoesNotCrash() {
        PengoModel model = newModel();

        PengoPlayer player = new PengoPlayer();
        player.setPosition(Game.grid().new Position(1, 1));
        player.setSize(Game.grid().new Dimension(1, 1));

        model.setPlayer(player);

        assertDoesNotThrow(() -> PengoBots.configureEntity(model, player));
    }

    @Test
    public void testConfigureEnemyDoesNotCrashAndTurnsRight() {
        PengoModel model = newModel();

        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(5, 5));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        enemy.turnTo(180);

        model.add(enemy);

        assertDoesNotThrow(() -> PengoBots.configureEntity(model, enemy));

        assertEquals(0, enemy.orientation());
    }

    @Test
    public void testConfigureIceBlockDoesNotCrash() {
        PengoModel model = newModel();

        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(5, 5));
        block.setSize(Game.grid().new Dimension(1, 1));
        model.add(block);

        assertDoesNotThrow(() -> PengoBots.configureEntity(model, block));
    }

    @Test
    public void testConfigureGoldBlockDoesNotCrash() {
        PengoModel model = newModel();

        GoldBlock gold = new GoldBlock();
        gold.setPosition(Game.grid().new Position(5, 5));
        gold.setSize(Game.grid().new Dimension(1, 1));
        model.add(gold);

        assertDoesNotThrow(() -> PengoBots.configureEntity(model, gold));
    }

    @Test
    public void testConfigureDiamondBlockDoesNotCrash() {
        PengoModel model = newModel();

        DiamondBlock diamond = new DiamondBlock();
        diamond.setPosition(Game.grid().new Position(5, 5));
        diamond.setSize(Game.grid().new Dimension(1, 1));
        model.add(diamond);

        assertDoesNotThrow(() -> PengoBots.configureEntity(model, diamond));
    }

    @Test
    public void testConfigureWallDoesNotCrash() {
        PengoModel model = newModel();

        Wall wall = new Wall();
        wall.setPosition(Game.grid().new Position(5, 5));
        wall.setSize(Game.grid().new Dimension(1, 1));
        model.add(wall);

        assertDoesNotThrow(() -> PengoBots.configureEntity(model, wall));
    }

    @Test
    public void testConfigureFishBonusDoesNotCrash() {
        PengoModel model = newModel();

        FishBonus fish = new FishBonus();
        fish.setPosition(Game.grid().new Position(5, 5));
        fish.setSize(Game.grid().new Dimension(1, 1));
        model.add(fish);

        assertDoesNotThrow(() -> PengoBots.configureEntity(model, fish));
    }

    @Test
    public void testConfigureWholeModelDoesNotCrash() {
        PengoModel model = newModel();

        PengoPlayer player = new PengoPlayer();
        player.setPosition(Game.grid().new Position(1, 1));
        player.setSize(Game.grid().new Dimension(1, 1));
        model.setPlayer(player);

        Entity[] entities = {
            new Enemy(),
            new IceBlock(),
            new GoldBlock(),
            new DiamondBlock(),
            new Wall(),
            new FishBonus()
        };

        int x = 2;

        for (Entity e : entities) {
            e.setPosition(Game.grid().new Position(x, 2));
            e.setSize(Game.grid().new Dimension(1, 1));
            model.add(e);
            x++;
        }

        assertDoesNotThrow(() -> PengoBots.configure(model));
    }
}