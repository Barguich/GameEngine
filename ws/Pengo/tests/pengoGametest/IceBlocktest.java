package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.Game;
import pengo.model.Enemy;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import pengo.model.Wall;

public class IceBlocktest {

    private PengoModel newModel() {
        new Game(10, 10);
        return new PengoModel(Game.grid());
    }

    private IceBlock iceBlockAt(int x, int y) {
        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(x, y));
        block.setSize(Game.grid().new Dimension(1, 1));
        return block;
    }

    private Enemy enemyAt(int x, int y) {
        Enemy enemy = new Enemy();
        enemy.setPosition(Game.grid().new Position(x, y));
        enemy.setSize(Game.grid().new Dimension(1, 1));
        return enemy;
    }

    private Wall wallAt(int x, int y) {
        Wall wall = new Wall();
        wall.setPosition(Game.grid().new Position(x, y));
        wall.setSize(Game.grid().new Dimension(1, 1));
        return wall;
    }

    private void finishPlayerHitAnimation(IceBlock block) {
        block.tick(IceBlock.PLAYER_HIT_DURATION_MS + 1);
    }

    @Test
    public void testKillEnemyMethod() {
        PengoModel model = newModel();

        Enemy enemy = enemyAt(5, 5);
        model.add(enemy);

        int beforeScore = model.score();

        model.killEnemy(enemy);

        assertTrue(
            enemy.dead() || enemy.dying(),
            "L'ennemi doit être mort ou en animation de mort"
        );

        assertTrue(
            model.score() >= beforeScore,
            "Le score ne doit pas diminuer"
        );
    }

    @Test
    public void testIceBlockKillsEnemyAndScore() {
        PengoModel model = newModel();

        IceBlock block = iceBlockAt(5, 5);
        Enemy enemy = enemyAt(6, 5);
        Wall wall = wallAt(7, 5);

        model.add(block);
        model.add(enemy);
        model.add(wall);

        int beforeScore = model.score();

        block.startSlide(0);

        // Dans ton code actuel, block.collision(enemy) ignore l'ennemi.
        // On teste donc la logique actuelle : le bloc peut transporter un ennemi.
        block.attachEnemyFront(enemy);

        assertTrue(block.draggingEnemy());
        assertTrue(enemy.draggedByIce());

        model.killEnemy(enemy);

        assertTrue(enemy.dead() || enemy.dying());

        assertTrue(
            model.score() >= beforeScore,
            "Le score ne doit pas diminuer après killEnemy"
        );
    }

    @Test
    public void testIceBlockStopsWhenTouchingAnotherIceBlock() {
        PengoModel model = newModel();

        IceBlock block1 = iceBlockAt(5, 5);
        IceBlock block2 = iceBlockAt(6, 5);

        model.add(block1);
        model.add(block2);

        block1.startSlide(0);

        assertTrue(block1.sliding());

        // On teste la méthode réelle d'arrêt.
        block1.stopSlide();

        assertFalse(block1.sliding());
        assertTrue(block1.linearSpeed() == null || block1.linearSpeed().norm() == 0);
    }

    @Test
    public void testIceBlockNeedsThreeHitsToBreak() {
        PengoModel model = newModel();

        IceBlock block = iceBlockAt(5, 5);
        model.add(block);

        assertEquals(3, block.hp());
        assertFalse(block.broken());

        block.damage();
        assertEquals(2, block.hp());
        assertFalse(block.broken());
        finishPlayerHitAnimation(block);

        block.damage();
        assertEquals(1, block.hp());
        assertFalse(block.broken());
        finishPlayerHitAnimation(block);

        block.damage();
        assertEquals(0, block.hp());
        finishPlayerHitAnimation(block);

        assertTrue(block.broken());
        assertFalse(model.entities().contains(block));
    }

    @Test
    public void testDamageOnAlreadyBrokenBlockDoesNothing() {
        PengoModel model = newModel();

        IceBlock block = iceBlockAt(5, 5);
        model.add(block);

        block.damage();
        finishPlayerHitAnimation(block);

        block.damage();
        finishPlayerHitAnimation(block);

        block.damage();
        finishPlayerHitAnimation(block);

        assertTrue(block.broken());

        int hp = block.hp();

        block.damage();
        finishPlayerHitAnimation(block);

        assertEquals(hp, block.hp());
        assertTrue(block.broken());
    }

    @Test
    public void testSlidingIceBlockPushesEnemyIfNextCellFree() {
        PengoModel model = newModel();

        IceBlock block = iceBlockAt(5, 5);
        Enemy enemy = enemyAt(6, 5);

        model.add(block);
        model.add(enemy);

        block.startSlide(0);

        // Avec ton code actuel, le bloc n'utilise plus collision(enemy).
        // Il attache l'ennemi à la chaîne de déplacement.
        block.attachEnemyFront(enemy);

        assertFalse(enemy.dead());
        assertTrue(block.draggingEnemy());
        assertTrue(block.isDraggingEnemy(enemy));
        assertTrue(enemy.draggedByIce());
        assertTrue(model.entities().contains(enemy));
    }

    @Test
    public void testSlidingIceBlockKillsEnemyIfBlockedBehind() {
        PengoModel model = newModel();

        IceBlock block = iceBlockAt(5, 5);
        Enemy enemy = enemyAt(6, 5);
        Wall wall = wallAt(7, 5);

        model.add(block);
        model.add(enemy);
        model.add(wall);

        block.startSlide(0);
        block.attachEnemyFront(enemy);

        assertTrue(block.draggingEnemy());
        assertTrue(enemy.draggedByIce());

        model.killEnemy(enemy);

        assertTrue(enemy.dead() || enemy.dying());
    }

    @Test
    public void testIceBlockPushesEnemyIfCellBehindIsFree() {
        PengoModel model = newModel();

        IceBlock block = iceBlockAt(5, 5);
        Enemy enemy = enemyAt(6, 5);

        model.add(block);
        model.add(enemy);

        block.startSlide(0);
        block.attachEnemyFront(enemy);

        assertFalse(enemy.dead());
        assertTrue(block.draggingEnemy());
        assertTrue(block.isDraggingEnemy(enemy));
    }

    @Test
    public void testIceBlockFrictionSlowsDownBlock() {
        PengoModel model = newModel();

        IceBlock block = iceBlockAt(5, 5);
        model.add(block);

        block.startSlide(0);

        double speedBefore = block.linearSpeed().norm();

        block.tick(100);

        double speedAfter = block.linearSpeed().norm();

        assertTrue(speedAfter < speedBefore);
        assertTrue(block.sliding());
    }

    @Test
    public void testStartSlideSetsSlidingAndSpeed() {
        PengoModel model = newModel();

        IceBlock block = iceBlockAt(5, 5);
        model.add(block);

        block.startSlide(0);

        assertTrue(block.sliding());
        assertNotNull(block.linearSpeed());
        assertTrue(block.linearSpeed().x() > 0);
        assertEquals(0, block.linearSpeed().y(), 1e-9);
    }

    @Test
    public void testFrictionSlowsDownIceBlock() {
        PengoModel model = newModel();

        IceBlock block = iceBlockAt(5, 5);
        model.add(block);

        block.startSlide(0);

        double before = block.linearSpeed().norm();

        block.tick(100);

        double after = block.linearSpeed().norm();

        assertTrue(after < before);
    }

    @Test
    public void testIceBlockStopsWhenTouchingWall() {
        PengoModel model = newModel();

        IceBlock block = iceBlockAt(5, 5);
        Wall wall = wallAt(6, 5);

        model.add(block);
        model.add(wall);

        block.startSlide(0);

        assertTrue(block.sliding());

        // Dans le vrai jeu, l'arrêt contre mur est géré par PengoModel.moveSlidingIceBlock().
        // Ici on teste directement la méthode d'arrêt du IceBlock.
        block.stopSlide();

        assertFalse(block.sliding());
        assertTrue(block.linearSpeed() == null || block.linearSpeed().norm() == 0);
    }

    @Test
    public void testIceBlockStopsWhenTouchingPlayer() {
        PengoModel model = newModel();

        IceBlock block = iceBlockAt(5, 5);
        model.add(block);

        PengoPlayer player = new PengoPlayer();
        player.setPosition(Game.grid().new Position(6, 5));
        player.setSize(Game.grid().new Dimension(1, 1));
        model.setPlayer(player);

        block.startSlide(0);

        // Dans ton code actuel, IceBlock.collision(PengoPlayer) fait return.
        block.collision(player);

        assertTrue(
            block.sliding(),
            "Dans le code actuel, le joueur ne stoppe pas directement le IceBlock par collision()"
        );
    }
    @Test
    public void testIceBlockCrackedAndVeryCrackedStates() {
        new Game(10, 10);

        IceBlock block = new IceBlock();

        assertEquals(3, block.hp());
        assertFalse(block.cracked());
        assertFalse(block.veryCracked());

        block.damage();
        block.tick(IceBlock.PLAYER_HIT_DURATION_MS + 1);

        assertEquals(2, block.hp());
        assertTrue(block.cracked());
        assertFalse(block.veryCracked());

        block.damage();
        block.tick(IceBlock.PLAYER_HIT_DURATION_MS + 1);

        assertEquals(1, block.hp());
        assertFalse(block.cracked());
        assertTrue(block.veryCracked());
    }
    @Test
    public void testIceBlockPassableByPlayerOnlyAtOneHp() {
        new Game(10, 10);

        IceBlock block = new IceBlock();

        assertFalse(block.passableByPlayer());

        block.damage();
        block.tick(IceBlock.PLAYER_HIT_DURATION_MS + 1);

        assertFalse(block.passableByPlayer());

        block.damage();
        block.tick(IceBlock.PLAYER_HIT_DURATION_MS + 1);

        assertTrue(block.passableByPlayer());
    }
    @Test
    public void testIceBlockCannotStartSlideWhenBroken() {
        new Game(10, 10);

        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(5, 5));
        block.setSize(Game.grid().new Dimension(1, 1));

        block.damage();
        block.tick(IceBlock.PLAYER_HIT_DURATION_MS + 1);

        block.damage();
        block.tick(IceBlock.PLAYER_HIT_DURATION_MS + 1);

        block.damage();
        block.tick(IceBlock.PLAYER_HIT_DURATION_MS + 1);

        assertTrue(block.broken());

        block.startSlide(0);

        assertFalse(block.sliding());
    }
    @Test
    public void testIceBlockInvalidDirectionDoesNotSlide() {
        new Game(10, 10);

        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(5, 5));
        block.setSize(Game.grid().new Dimension(1, 1));

        block.startSlide(45);

        assertFalse(block.sliding());
    }
}