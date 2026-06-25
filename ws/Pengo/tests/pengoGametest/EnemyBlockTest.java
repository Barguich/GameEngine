package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.Game;
import model.Entity;
import model.Model;
import pengo.model.Enemy;
import pengo.model.EnemyBlock;
import pengo.model.PengoModel;

public class EnemyBlockTest {

    private EnemyBlock enemyBlockAt(int x, int y) {
        EnemyBlock block = new EnemyBlock();
        block.setPosition(Game.grid().new Position(x, y));
        block.setSize(Game.grid().new Dimension(1, 1));
        return block;
    }

    private int countEnemies(Model model) {
        int count = 0;

        for (Entity e : model.entities()) {
            if (e instanceof Enemy) {
                count++;
            }
        }

        return count;
    }

    private Enemy firstEnemy(Model model) {
        for (Entity e : model.entities()) {
            if (e instanceof Enemy) {
                return (Enemy) e;
            }
        }

        return null;
    }

    @Test
    public void testEnemyBlockBreakSpawnsEnemyInBasicModel() {
        new Game(10, 10);

        Model model = new Model(Game.grid());

        EnemyBlock block = enemyBlockAt(5, 5);
        model.add(block);

        assertTrue(model.entities().contains(block));
        assertEquals(0, countEnemies(model));

        block.breakBlock();

        assertFalse(
            model.entities().contains(block),
            "Le EnemyBlock doit être retiré du modèle après destruction"
        );

        assertEquals(
            1,
            countEnemies(model),
            "Un Enemy doit apparaître après destruction du EnemyBlock"
        );

        Enemy enemy = firstEnemy(model);

        assertNotNull(enemy);
        assertEquals(5, enemy.position().x());
        assertEquals(5, enemy.position().y());
    }

    @Test
    public void testEnemyBlockSpawnedEnemyHasCorrectSize() {
        new Game(10, 10);

        Model model = new Model(Game.grid());

        EnemyBlock block = enemyBlockAt(3, 4);
        model.add(block);

        block.breakBlock();

        Enemy enemy = firstEnemy(model);

        assertNotNull(enemy);
        assertNotNull(enemy.size());

        assertTrue(enemy.size().x() > 0);
        assertTrue(enemy.size().y() > 0);
    }

    @Test
    public void testEnemyBlockBreakWithoutModelDoesNotCrash() {
        new Game(10, 10);

        EnemyBlock block = enemyBlockAt(5, 5);

        block.breakBlock();

        assertTrue(
            block.broken(),
            "Même sans modèle, le bloc doit être marqué comme cassé"
        );
    }

    @Test
    public void testEnemyBlockBreakWithoutPositionDoesNotCrash() {
        new Game(10, 10);

        Model model = new Model(Game.grid());

        EnemyBlock block = new EnemyBlock();
        block.setSize(Game.grid().new Dimension(1, 1));

        model.add(block);

        block.breakBlock();

        assertEquals(
            0,
            countEnemies(model),
            "Sans position, aucun ennemi ne doit être créé"
        );
    }

    @Test
    public void testEnemyBlockBreakInPengoModelSpawnsEnemy() {
        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        EnemyBlock block = enemyBlockAt(6, 6);
        model.add(block);

        block.breakBlock();

        assertFalse(
            model.entities().contains(block),
            "Le EnemyBlock doit être retiré après breakBlock"
        );

        assertTrue(
            countEnemies(model) >= 1,
            "Un Enemy doit être créé dans le PengoModel"
        );

        boolean enemyAtSpawnPosition = false;

        for (Entity e : model.entities()) {
            if (e instanceof Enemy enemy && enemy.position() != null) {
                if (enemy.position().x() == 6 && enemy.position().y() == 6) {
                    enemyAtSpawnPosition = true;
                }
            }
        }

        assertTrue(
            enemyAtSpawnPosition,
            "Un Enemy doit apparaître à la position du EnemyBlock"
        );
    }
}