package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import engine.Game;
import geometry.Grid;
import model.Entity;
import pengo.model.DiamondBlock;
import pengo.model.Enemy;
import pengo.model.FishBonus;
import pengo.model.GoldBlock;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import pengo.model.Wall;

public class PengoModelTest {

    private PengoModel newModel() {
        new Game(10, 10);
        return new PengoModel(Game.grid());
    }

    private PengoModel newPlayingModel() {
        PengoModel model = newModel();
        model.reset();
        return model;
    }

    private PengoPlayer playerAt(int x, int y) {
        PengoPlayer player = new PengoPlayer();
        player.setPosition(Game.grid().new Position(x, y));
        player.setSize(Game.grid().new Dimension(1, 1));
        return player;
    }

    private Enemy enemyAt(int x, int y) {
        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(x, y));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        return enemy;
    }

    private IceBlock iceAt(int x, int y) {
        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(x, y));
        block.setSize(Game.grid().new Dimension(1, 1));
        return block;
    }

    private Wall wallAt(int x, int y) {
        Wall wall = new Wall();
        wall.setPosition(Game.grid().new Position(x, y));
        wall.setSize(Game.grid().new Dimension(1, 1));
        return wall;
    }

    private DiamondBlock diamondAt(int x, int y) {
        DiamondBlock diamond = new DiamondBlock();
        diamond.setPosition(Game.grid().new Position(x, y));
        diamond.setSize(Game.grid().new Dimension(1, 1));
        return diamond;
    }

    private int countEnemies(PengoModel model) {
        int count = 0;

        for (Entity e : model.entities()) {
            if (e instanceof Enemy) {
                count++;
            }
        }

        return count;
    }

    @Test
    public void extraInitialStateIsMapChoice() {
        PengoModel model = newModel();

        assertEquals(PengoModel.GameState.CHOIX_MAP, model.state());
        assertTrue(model.menuVisible());
        assertFalse(model.running());
        assertEquals(0, model.score());
        assertFalse(model.won());
        assertFalse(model.lost());
    }

    @Test
    public void extraResetStartsPlayingAndClearsScore() {
        PengoModel model = newModel();

        model.addScore(100);
        model.activateDoubleScore(1000);

        model.reset();

        assertEquals(PengoModel.GameState.PLAYING, model.state());
        assertTrue(model.running());
        assertFalse(model.menuVisible());
        assertEquals(0, model.score());
        assertFalse(model.doubleScore());
    }

    @Test
    public void extraChooseMapStoresSelectedMapAndStartsGame() {
        PengoModel model = newModel();

        model.chooseMap("Asset/rsrc/maps/test.txt");

        assertEquals("Asset/rsrc/maps/test.txt", model.selectedMapFile());
        assertEquals(PengoModel.GameState.PLAYING, model.state());
    }

    @Test
    public void extraPauseResumeAndTogglePause() {
        PengoModel model = newPlayingModel();

        model.pause();
        assertEquals(PengoModel.GameState.PAUSED, model.state());

        model.resume();
        assertEquals(PengoModel.GameState.PLAYING, model.state());

        model.togglePause();
        assertEquals(PengoModel.GameState.PAUSED, model.state());

        model.togglePause();
        assertEquals(PengoModel.GameState.PLAYING, model.state());
    }

    @Test
    public void extraStateListenerIsCalled() {
        PengoModel model = newModel();

        AtomicReference<PengoModel.GameState> lastState = new AtomicReference<>();

        model.setStateListener(state -> lastState.set(state));

        model.reset();

        assertEquals(PengoModel.GameState.PLAYING, lastState.get());

        model.pause();

        assertEquals(PengoModel.GameState.PAUSED, lastState.get());
    }

    @Test
    public void extraSceneBuilderRunsOnReset() {
        PengoModel model = newModel();

        AtomicInteger calls = new AtomicInteger(0);

        model.setSceneBuilder(() -> calls.incrementAndGet());

        model.reset();

        assertEquals(1, calls.get());
        assertEquals(PengoModel.GameState.PLAYING, model.state());
    }

    @Test
    public void extraAddScoreIgnoresZeroAndNegative() {
        PengoModel model = newModel();

        model.addScore(0);
        model.addScore(-100);

        assertEquals(0, model.score());
    }

    @Test
    public void extraAddScoreWithDoubleScoreDoublesPoints() {
        PengoModel model = newModel();

        model.activateDoubleScore(1000);
        model.addScore(100);

        assertEquals(200, model.score());
        assertTrue(model.doubleScore());
    }

    @Test
    public void extraDoubleScoreExpiresDuringTick() {
        PengoModel model = newPlayingModel();

        model.activateDoubleScore(1);

        assertTrue(model.doubleScore());

        model.tick(2);

        assertFalse(model.doubleScore());
    }

    @Test
    public void extraFreezeEnemiesFreezesNormalEnemiesOnly() {
        PengoModel model = newModel();

        Enemy normal = enemyAt(3, 3);
        Enemy dragged = enemyAt(4, 3);

        dragged.startDraggedByIce();

        model.add(normal);
        model.add(dragged);

        model.freezeEnemies(1000);

        assertTrue(normal.frozen());
        assertFalse(dragged.frozen());
    }

    @Test
    public void extraEnemiesRemainingCountsEnemyAndSnoBeeBlock() {
        PengoModel model = newModel();

        Enemy enemy = enemyAt(3, 3);
        IceBlock egg = new IceBlock(true, 1000);
        egg.setPosition(Game.grid().new Position(4, 4));
        egg.setSize(Game.grid().new Dimension(1, 1));

        model.add(enemy);
        model.add(egg);

        assertEquals(2, model.enemiesRemaining());
    }

    @Test
    public void extraCheckVictoryNoEnemiesWins() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(1, 1);
        model.setPlayer(player);

        model.checkVictory();

        assertTrue(model.won());
        assertEquals(PengoModel.GameState.WON, model.state());
    }

    @Test
    public void extraCheckVictoryWithEnemyDoesNotWin() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(1, 1);
        model.setPlayer(player);

        Enemy enemy = enemyAt(8, 8);
        model.add(enemy);

        model.checkVictory();

        assertFalse(model.won());
    }

    @Test
    public void extraDiamondHorizontalAlignmentWins() {
        PengoModel model = newPlayingModel();

        model.setPlayer(playerAt(1, 1));

        model.add(diamondAt(2, 5));
        model.add(diamondAt(3, 5));
        model.add(diamondAt(4, 5));

        model.add(enemyAt(8, 8));

        model.checkVictory();

        assertTrue(model.won());
    }

    @Test
    public void extraDiamondVerticalAlignmentWins() {
        PengoModel model = newPlayingModel();

        model.setPlayer(playerAt(1, 1));

        model.add(diamondAt(5, 2));
        model.add(diamondAt(5, 3));
        model.add(diamondAt(5, 4));

        model.add(enemyAt(8, 8));

        model.checkVictory();

        assertTrue(model.won());
    }

    @Test
    public void extraWinByDiamondAlignmentSetsWonState() {
        PengoModel model = newPlayingModel();

        model.add(enemyAt(8, 8));

        model.winByDiamondAlignment();

        assertTrue(model.won());
        assertEquals(PengoModel.GameState.WON, model.state());
    }

    @Test
    public void extraLoseLifeWithoutPlayerDoesNothing() {
        PengoModel model = newPlayingModel();

        model.loseLife();

        assertFalse(model.lost());
    }

    @Test
    public void extraLoseLifeMakesPlayerInvincibleTemporarily() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        model.loseLife();

        assertEquals(2, player.lives());

        model.loseLife();

        assertEquals(
            2,
            player.lives(),
            "La deuxième perte de vie doit être ignorée pendant l'invincibilité"
        );
    }

    @Test
    public void extraLoseLifeEventuallyGameOver() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        Enemy enemy = enemyAt(8, 8);
        model.add(enemy);

        model.loseLife();
        model.tick(2001);

        model.loseLife();
        model.tick(2001);

        model.loseLife();

        assertTrue(model.lost());
        assertEquals(PengoModel.GameState.GAME_OVER, model.state());
    }

    @Test
    public void extraStartWallVibrationMarksWallAndAdjacentEnemy() {
        PengoModel model = newModel();

        Wall wall = wallAt(5, 5);
        Enemy enemy = enemyAt(6, 5);

        model.add(wall);
        model.add(enemy);

        model.startWallVibration(wall, 1000);

        assertTrue(model.wallVibration());
        assertTrue(model.isVibrating(wall));
        assertTrue(model.isVibrating(enemy));
        assertTrue(enemy.passedOut());
    }

    @Test
    public void extraStartWallVibrationWithNullDoesNothing() {
        PengoModel model = newModel();

        model.startWallVibration(null, 1000);

        assertFalse(model.wallVibration());
    }

    @Test
    public void extraWallVibrationExpiresDuringTick() {
        PengoModel model = newPlayingModel();

        Wall wall = wallAt(5, 5);
        Enemy enemy = enemyAt(8, 8);

        model.add(wall);
        model.add(enemy);

        model.startWallVibration(wall, 1);

        assertTrue(model.wallVibration());

        model.tick(2);

        assertFalse(model.wallVibration());
    }

    @Test
    public void extraKillEnemyAddsScore() {
        PengoModel model = newModel();

        Enemy enemy = enemyAt(5, 5);
        model.add(enemy);

        int before = model.score();

        model.killEnemy(enemy);

        assertTrue(enemy.dead() || enemy.dying());
        assertTrue(model.score() > before);
    }

    @Test
    public void extraKillEnemyNullDoesNothing() {
        PengoModel model = newModel();

        model.killEnemy(null);

        assertEquals(0, model.score());
    }

    @Test
    public void extraEatFrozenEnemyAddsScore() {
        PengoModel model = newModel();

        Enemy enemy = enemyAt(5, 5);
        model.add(enemy);

        enemy.freeze(1000);

        int before = model.score();

        model.eatFrozenEnemy(enemy);

        assertTrue(model.score() > before);
        assertTrue(enemy.dead() || enemy.dying());
    }

    @Test
    public void extraEatNormalEnemyDoesNothing() {
        PengoModel model = newModel();

        Enemy enemy = enemyAt(5, 5);
        model.add(enemy);

        model.eatFrozenEnemy(enemy);

        assertEquals(0, model.score());
        assertFalse(enemy.dead());
    }

    @Test
    public void extraDamageBlockInFrontDamagesIceBlock() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        player.turnTo(0);
        model.setPlayer(player);

        IceBlock block = iceAt(6, 5);
        model.add(block);

        int beforeHp = block.hp();

        model.damageBlockInFront(player);

        assertTrue(block.hp() < beforeHp);
    }

    @Test
    public void extraDamageBlockInFrontDoesNotDamageDiamondBlock() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        player.turnTo(0);
        model.setPlayer(player);

        DiamondBlock diamond = diamondAt(6, 5);
        model.add(diamond);

        int beforeHp = diamond.hp();

        model.damageBlockInFront(player);

        assertEquals(beforeHp, diamond.hp());
    }

    @Test
    public void extraDamageBlockInFrontWithNullDoesNothing() {
        PengoModel model = newModel();

        assertDoesNotThrow(() -> model.damageBlockInFront(null));
    }

    @Test
    public void extraNextPositionWorksInFourDirections() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);

        assertEquals(6, model.nextPosition(player, 0).x());
        assertEquals(6, model.nextPosition(player, 90).y());
        assertEquals(4, model.nextPosition(player, 180).x());
        assertEquals(4, model.nextPosition(player, 270).y());
    }

    @Test
    public void extraNextPositionInvalidDirectionReturnsSameCell() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);

        Grid.Position pos = model.nextPosition(player, 45);

        assertEquals(5, pos.x());
        assertEquals(5, pos.y());
    }

    @Test
    public void extraBlockedReturnsTrueForWallAndIceBlock() {
        PengoModel model = newModel();

        Wall wall = wallAt(2, 2);
        IceBlock ice = iceAt(3, 3);

        model.add(wall);
        model.add(ice);

        assertTrue(model.blocked(Game.grid().new Position(2, 2)));
        assertTrue(model.blocked(Game.grid().new Position(3, 3)));
        assertFalse(model.blocked(Game.grid().new Position(4, 4)));
    }

    @Test
    public void extraPushesOffEdgeWorksForAllDirections() {
        PengoModel model = newModel();

        Entity right = iceAt(9, 5);
        Entity left = iceAt(0, 5);
        Entity down = iceAt(5, 9);
        Entity up = iceAt(5, 0);

        assertTrue(model.pushesOffEdge(right, 0));
        assertTrue(model.pushesOffEdge(left, 180));
        assertTrue(model.pushesOffEdge(down, 90));
        assertTrue(model.pushesOffEdge(up, 270));

        assertFalse(model.pushesOffEdge(iceAt(5, 5), 0));
    }

    @Test
    public void extraTryEnterCellIntoEmptyCellReturnsTrue() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        assertTrue(model.tryEnterCellForPlayer(player, 0, false));
    }

    @Test
    public void extraTryEnterCellAgainstWallReturnsFalse() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        Wall wall = wallAt(6, 5);
        model.add(wall);

        assertFalse(model.tryEnterCellForPlayer(player, 0, false));
        assertTrue(model.wallVibration());
    }

    @Test
    public void extraTryEnterCellAgainstFishBonusReturnsTrue() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        FishBonus fish = new FishBonus();
        fish.setPosition(Game.grid().new Position(6, 5));
        fish.setSize(Game.grid().new Dimension(1, 1));
        model.add(fish);

        assertTrue(model.tryEnterCellForPlayer(player, 0, false));
    }

    @Test
    public void extraTryEnterCellAgainstDangerousEnemyReturnsFalseAndLosesLife() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        Enemy enemy = enemyAt(6, 5);
        model.add(enemy);

        int livesBefore = player.lives();

        assertFalse(model.tryEnterCellForPlayer(player, 0, false));
        assertTrue(player.lives() < livesBefore);
    }

    @Test
    public void extraTryEnterCellAgainstFrozenEnemyReturnsTrueAndAddsScore() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        Enemy enemy = enemyAt(6, 5);
        model.add(enemy);

        enemy.freeze(1000);

        int scoreBefore = model.score();

        assertTrue(model.tryEnterCellForPlayer(player, 0, false));
        assertTrue(model.score() > scoreBefore);
    }

    @Test
    public void extraTryEnterCellAgainstCrackedIceBlockReturnsFalse() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        IceBlock block = iceAt(6, 5);
        model.add(block);

        block.damage();

        assertEquals(2, block.hp());
        assertFalse(model.tryEnterCellForPlayer(player, 0, false));
    }

    @Test
    public void extraTryEnterCellAgainstPassableIceBlockReturnsTrue() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        IceBlock block = iceAt(6, 5);
        model.add(block);

        block.damage();
        block.tick(IceBlock.PLAYER_HIT_DURATION_MS + 1);

        block.damage();
        block.tick(IceBlock.PLAYER_HIT_DURATION_MS + 1);

        assertEquals(1, block.hp());
        assertTrue(block.passableByPlayer());

        assertTrue(model.tryEnterCellForPlayer(player, 0, false));
    }

    @Test
    public void extraHatchSnoBeeCreatesEnemyAndCallsListener() {
        PengoModel model = newModel();

        IceBlock block = iceAt(4, 4);
        model.add(block);

        AtomicReference<Enemy> spawned = new AtomicReference<>();

        model.setEnemySpawnListener(enemy -> spawned.set(enemy));

        model.hatchSnoBee(block);

        assertFalse(model.entities().contains(block));
        assertEquals(1, countEnemies(model));
        assertNotNull(spawned.get());
        assertEquals(4, spawned.get().position().x());
        assertEquals(4, spawned.get().position().y());
    }

    @Test
    public void extraHatchSnoBeeWithNullDoesNothing() {
        PengoModel model = newModel();

        assertDoesNotThrow(() -> model.hatchSnoBee(null));
    }

    @Test
    public void extraScheduleBlockRespawnDoesNotCrash() {
        PengoModel model = newPlayingModel();

        Grid.Position pos = Game.grid().new Position(4, 4);

        model.scheduleBlockRespawn(pos, 1);

        assertDoesNotThrow(() -> model.tick(2));
    }

    @Test
    public void extraGotUsesEnemiesRemaining() {
        PengoModel model = newModel();

        assertTrue(model.got(null, 0));

        Enemy enemy = enemyAt(5, 5);
        model.add(enemy);

        assertFalse(model.got(null, 0));
        assertTrue(model.got(null, 1));
    }
}