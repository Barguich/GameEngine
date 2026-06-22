package pengo.model;

import java.util.ArrayList;
import java.util.List;

import geometry.Grid;
import model.Entity;
import model.Model;

public class PengoModel extends Model {

    public enum GameState {
        PLAYING, PAUSED, GAME_OVER, WON
    }

    public interface StateListener {
        void onStateChanged(GameState state);
    }

    private GameState state = GameState.PLAYING;
    private StateListener stateListener;

    private PengoPlayer player;
    private int score;

    private boolean won;
    private boolean lost;

    private boolean resolvingIceEnemyCollision;

    private boolean doubleScore;
    private long doubleScoreRemaining;

    private boolean wallVibration;
    private long wallVibrationRemaining;
    private List<Entity> vibratingEntities;

    private long invincibleRemaining;

    private Runnable sceneBuilder;

    public PengoModel(Grid grid) {
        super(grid);

        player = null;
        score = 0;

        won = false;
        lost = false;

        resolvingIceEnemyCollision = false;

        doubleScore = false;
        doubleScoreRemaining = 0;

        wallVibration = false;
        wallVibrationRemaining = 0;
        vibratingEntities = new ArrayList<Entity>();

        invincibleRemaining = 0;
    }

    public void setPlayer(PengoPlayer player) {
        if (player == null) {
            return;
        }

        this.player = player;

        if (!entities().contains(player)) {
            add(player);
        }
    }

    public PengoPlayer player() {
        return player;
    }

    public GameState state() {
        return state;
    }

    public void setStateListener(StateListener listener) {
        this.stateListener = listener;
    }

    private void setState(GameState newState) {
        if (state == newState) {
            return;
        }

        state = newState;

        if (stateListener != null) {
            stateListener.onStateChanged(state);
        }
    }

    public boolean running() {
        return state == GameState.PLAYING;
    }

    public boolean menuVisible() {
        return state != GameState.PLAYING;
    }

    public void pause() {
        if (state == GameState.PLAYING) {
            setState(GameState.PAUSED);
        }
    }

    public void resume() {
        if (state == GameState.PAUSED) {
            setState(GameState.PLAYING);
        }
    }

    public void togglePause() {
        if (state == GameState.PLAYING) {
            pause();
        } else if (state == GameState.PAUSED) {
            resume();
        }
    }

    public void setSceneBuilder(Runnable sceneBuilder) {
        this.sceneBuilder = sceneBuilder;
    }

    public void reset() {
        clear();

        player = null;
        score = 0;

        won = false;
        lost = false;

        resolvingIceEnemyCollision = false;

        doubleScore = false;
        doubleScoreRemaining = 0;

        invincibleRemaining = 0;

        wallVibration = false;
        wallVibrationRemaining = 0;
        vibratingEntities.clear();

        if (sceneBuilder != null) {
            sceneBuilder.run();
        }

        setState(GameState.PLAYING);
    }

    public int score() {
        return score;
    }

    public void addScore(int points) {
        if (points <= 0) {
            return;
        }

        if (doubleScore) {
            score += points * 2;
        } else {
            score += points;
        }

        System.out.println("Score = " + score);
    }

    public void activateDoubleScore(long duration) {
        if (duration <= 0) {
            return;
        }

        doubleScore = true;
        doubleScoreRemaining = duration;
    }

    public boolean doubleScore() {
        return doubleScore;
    }

    public void freezeEnemies(long duration) {
        if (duration <= 0) {
            return;
        }

        for (Entity e : new ArrayList<Entity>(entities())) {
            if (e instanceof Enemy) {
                Enemy enemy = (Enemy) e;

                if (!enemy.dead() && !enemy.dying() && !enemy.draggedByIce()) {
                    enemy.freeze(duration);
                }
            }
        }
    }

    @Override
    public void tick(long elapsed) {
        if (elapsed < 0) {
            return;
        }

        if (state != GameState.PLAYING) {
            return;
        }

        super.tick(elapsed);

        if (invincibleRemaining > 0) {
            invincibleRemaining -= elapsed;

            if (invincibleRemaining < 0) {
                invincibleRemaining = 0;
            }
        }

        if (doubleScore) {
            doubleScoreRemaining -= elapsed;

            if (doubleScoreRemaining <= 0) {
                doubleScore = false;
                doubleScoreRemaining = 0;
            }
        }

        if (wallVibration) {
            wallVibrationRemaining -= elapsed;

            if (wallVibrationRemaining <= 0) {
                wallVibration = false;
                wallVibrationRemaining = 0;
                vibratingEntities.clear();
            }
        }

        checkVictory();

        if (player != null && player.dead()) {
            lost = true;
            setState(GameState.GAME_OVER);
        }
    }

    public boolean won() {
        return won;
    }

    public boolean lost() {
        return lost;
    }

    public void checkVictory() {
        if (lost || won) {
            return;
        }

        if (diamondBlocksAligned()) {
            won = true;
            setState(GameState.WON);
            System.out.println("YOU WIN - DIAMOND ALIGNMENT");
            return;
        }

        if (allEnemiesDead()) {
            won = true;
            setState(GameState.WON);
            System.out.println("YOU WIN - ALL ENEMIES DEAD");
        }
    }

    private boolean allEnemiesDead() {
        for (Entity e : entities()) {
            if (e instanceof Enemy) {
                return false;
            }
        }

        return true;
    }

    public int enemiesRemaining() {
        int count = 0;

        for (Entity e : entities()) {
            if (e instanceof Enemy) {
                count++;
            }
        }

        return count;
    }

    private boolean diamondBlocksAligned() {
        List<DiamondBlock> diamonds = new ArrayList<DiamondBlock>();

        for (Entity e : entities()) {
            if (e instanceof DiamondBlock) {
                diamonds.add((DiamondBlock) e);
            }
        }

        if (diamonds.size() < 3) {
            return false;
        }

        for (DiamondBlock d : diamonds) {
            if (d.position() == null) {
                continue;
            }

            int x = d.position().x();
            int y = d.position().y();

            if (diamondAt(diamonds, x + 1, y)
                    && diamondAt(diamonds, x + 2, y)) {
                return true;
            }

            if (diamondAt(diamonds, x, y + 1)
                    && diamondAt(diamonds, x, y + 2)) {
                return true;
            }
        }

        return false;
    }

    private boolean diamondAt(List<DiamondBlock> diamonds, int x, int y) {
        for (DiamondBlock d : diamonds) {
            if (d.position() == null) {
                continue;
            }

            if (d.position().x() == x && d.position().y() == y) {
                return true;
            }
        }

        return false;
    }

    public void loseLife() {
        if (player == null) {
            return;
        }

        if (resolvingIceEnemyCollision) {
            System.out.println("LOSE LIFE IGNORED DURING ICE/ENEMY COLLISION");
            return;
        }

        if (invincibleRemaining > 0) {
            return;
        }

        player.loseLife();
        invincibleRemaining = 2000;

        System.out.println("Le joueur perd une vie");

        if (player.dead()) {
            lost = true;
            setState(GameState.GAME_OVER);
            System.out.println("GAME OVER");
        } else {
            respawnPlayerNearSafePlace();
        }
    }

    public void respawnPlayerNearSafePlace() {
        if (player == null) {
            return;
        }

        int[][] positions = {
            {2, 2},
            {2, 3},
            {3, 2},
            {3, 3},
            {1, 2}
        };

        for (int[] p : positions) {
            boolean safe = true;

            for (Entity e : entities()) {
                if (e instanceof Enemy && e.position() != null) {
                    if (e.position().x() == p[0]
                            && e.position().y() == p[1]) {
                        safe = false;
                        break;
                    }
                }
            }

            if (safe) {
                player.setPosition(grid().new Position(p[0], p[1]));
                player.setBounding();
                player.stop();
                return;
            }
        }
    }

    public void startWallVibration(Entity source, long duration) {
        if (source == null || duration <= 0) {
            return;
        }

        wallVibration = true;
        wallVibrationRemaining = duration;

        vibratingEntities.clear();

        for (Entity e : entities()) {
            if (e instanceof Enemy) {
                if (e.distanceCenterToCenter(source) <= source.step().x() * 2) {
                    vibratingEntities.add(e);
                }
            }
        }
    }

    public boolean wallVibration() {
        return wallVibration;
    }

    public boolean isVibrating(Entity e) {
        return e != null && vibratingEntities.contains(e);
    }

    public void killEnemy(Enemy enemy) {
        if (enemy == null) {
            return;
        }

        if (enemy.dead() || enemy.dying()) {
            return;
        }

        enemy.kill();
        addScore(100);
    }

    public void damageBlockInFront(PengoPlayer player) {
        if (player == null || player.position() == null) {
            return;
        }

        int x = player.position().x();
        int y = player.position().y();

        switch (player.orientation()) {
            case 0:
                x++;
                break;
            case 90:
                y++;
                break;
            case 180:
                x--;
                break;
            case 270:
                y--;
                break;
            default:
                return;
        }

        Entity e = firstAt(grid().new Position(x, y));

        if (e instanceof IceBlock && !(e instanceof DiamondBlock)) {
            ((IceBlock) e).damage();
        }
    }

    public Grid.Position nextPosition(Entity e, int direction) {
        if (e == null || e.position() == null) {
            return null;
        }

        int x = e.position().x();
        int y = e.position().y();

        switch (direction) {
            case 0:
                x++;
                break;
            case 90:
                y++;
                break;
            case 180:
                x--;
                break;
            case 270:
                y--;
                break;
            default:
                break;
        }

        return grid().new Position(x, y);
    }

    public boolean blocked(Grid.Position p) {
        Entity e = firstAt(p);
        return e instanceof Wall || e instanceof IceBlock;
    }

    public boolean pushesOffEdge(Entity e, int direction) {
        if (e == null || e.position() == null) {
            return false;
        }

        int x = e.position().x();
        int y = e.position().y();

        switch (direction) {
            case 0:
                return x + 1 >= grid().width();
            case 90:
                return y + 1 >= grid().height();
            case 180:
                return x - 1 < 0;
            case 270:
                return y - 1 < 0;
            default:
                return false;
        }
    }

    /*
     * ==========================================================
     * LOGIQUE PRINCIPALE ICEBLOCK / ENEMY
     * ==========================================================
     *
     * Règle :
     * - IceBlock glisse.
     * - S'il touche Enemy :
     *      - si obstacle juste derrière Enemy : Enemy disparaît,
     *        IceBlock prend sa place.
     *      - sinon Enemy est transporté devant IceBlock.
     * - Enemy est écrasé uniquement contre Wall / IceBlock.
     */
    public boolean moveSlidingIceBlock(IceBlock ice, geometry.ISU.Vector movement) {
        if (ice == null || movement == null) {
            return false;
        }

        /*
         * CAS 1 :
         * Le bloc transporte déjà un ennemi.
         */
        if (ice.draggingEnemy()) {
            Enemy enemy = ice.draggedEnemy();

            if (enemy == null || enemy.dead() || enemy.dying()) {
                ice.detachEnemy();

                boolean moved = move(ice, movement);

                if (!moved) {
                    ice.stopSlide();
                    return false;
                }

                return true;
            }

            return slideWithEnemyInFront(ice, enemy, movement);
        }

        /*
         * CAS 2 :
         * Avant de bouger le bloc, on vérifie s'il va toucher un ennemi.
         */
        Enemy enemy = enemyReachedDuringThisMovement(ice, movement);

        if (enemy != null) {
            System.out.println("ICEBLOCK TOUCHES ENEMY");

            Grid.Position enemyPos = copyPosition(enemy.position());
            Grid.Position enemyNextCell = nextPosition(enemy, ice.direction());
            Entity obstacle = firstSolidAt(enemyNextCell, ice, enemy);

            /*
             * Si un obstacle est directement derrière l'ennemi,
             * on l'écrase immédiatement.
             */
            if (obstacle != null) {
                System.out.println(
                    "ENEMY IMMEDIATELY CRUSHED AGAINST "
                    + obstacle.getClass().getSimpleName()
                );

                crushEnemyByIce(ice, enemy, enemyPos);
                return true;
            }

            /*
             * Sinon, le bloc l'emporte avec lui.
             */
            ice.attachEnemy(enemy);
            enemy.setBot(null);
            enemy.stop();

            return slideWithEnemyInFront(ice, enemy, movement);
        }

        /*
         * CAS 3 :
         * Aucun ennemi, glissade normale.
         */
        boolean moved = move(ice, movement);

        if (!moved) {
            /*
             * Sécurité :
             * si le move a été refusé car un ennemi est vraiment collé devant,
             * on essaye de l'attacher.
             */
            Enemy directEnemy = enemyDirectlyInFront(ice);

            if (directEnemy != null) {
                System.out.println("ICE MOVE REFUSED BY ENEMY - ATTACH NOW");

                ice.attachEnemy(directEnemy);
                directEnemy.setBot(null);
                directEnemy.stop();

                return slideWithEnemyInFront(ice, directEnemy, movement);
            }

            ice.stopSlide();
            return false;
        }

        return true;
    }

    private boolean slideWithEnemyInFront(IceBlock ice, Enemy enemy, geometry.ISU.Vector movement) {
        if (ice == null || enemy == null || movement == null) {
            return false;
        }

        resolvingIceEnemyCollision = true;

        try {
            /*
             * Position actuelle de l'ennemi.
             * Si obstacle devant lui, le IceBlock prendra cette position.
             */
            Grid.Position enemyPos = copyPosition(enemy.position());

            /*
             * On regarde uniquement la case devant l'ennemi.
             * Si obstacle devant : écrasement.
             */
            Grid.Position enemyNextCell = nextPosition(enemy, ice.direction());
            Entity obstacle = firstSolidAt(enemyNextCell, ice, enemy);

            if (obstacle != null) {
                System.out.println(
                    "ENEMY CRUSHED AGAINST "
                    + obstacle.getClass().getSimpleName()
                );

                crushEnemyByIce(ice, enemy, enemyPos);
                return true;
            }

            /*
             * L'ennemi est draggedByIce, donc sa hitbox est vide.
             * Il peut bouger sans bloquer le IceBlock.
             */
            boolean enemyMoved = move(enemy, movement);

            /*
             * Le IceBlock avance avec le même movement.
             */
            boolean iceMoved = move(ice, movement);

            if (!iceMoved) {
                /*
                 * Si le IceBlock est bloqué alors qu'il n'y avait pas
                 * d'obstacle devant l'ennemi, on arrête juste la glissade.
                 */
                ice.stopSlide();
                enemy.stop();
                return false;
            }

            if (!enemyMoved) {
                /*
                 * Sécurité : si l'ennemi n'a pas bougé pour une raison quelconque,
                 * on le remet une case devant le IceBlock.
                 */
                placeEnemyInFrontOfIce(ice, enemy);
            }

            enemy.stop();

            return true;

        } finally {
            resolvingIceEnemyCollision = false;
        }
    }

    private void placeEnemyInFrontOfIce(IceBlock ice, Enemy enemy) {
        if (ice == null || enemy == null || ice.position() == null) {
            return;
        }

        Grid.Position front = nextPosition(ice, ice.direction());

        if (front == null) {
            return;
        }

        enemy.setPosition(front);
        enemy.setBounding();
        enemy.stop();
    }

    private Enemy enemyDirectlyInFront(IceBlock ice) {
        if (ice == null || ice.position() == null) {
            return null;
        }

        Grid.Position front = nextPosition(ice, ice.direction());

        if (front == null) {
            return null;
        }

        for (Entity e : new ArrayList<Entity>(entities())) {
            if (!(e instanceof Enemy)) {
                continue;
            }

            Enemy enemy = (Enemy) e;

            if (enemy.dead() || enemy.dying() || enemy.draggedByIce()) {
                continue;
            }

            if (enemy.position() == null) {
                continue;
            }

            if (enemy.position().x() == front.x()
                    && enemy.position().y() == front.y()) {
                return enemy;
            }
        }

        return null;
    }

    private Enemy enemyReachedDuringThisMovement(IceBlock ice, geometry.ISU.Vector movement) {
        if (ice == null || movement == null) {
            return null;
        }

        Enemy closestEnemy = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity e : new ArrayList<Entity>(entities())) {
            if (!(e instanceof Enemy)) {
                continue;
            }

            Enemy enemy = (Enemy) e;

            if (enemy.dead() || enemy.dying() || enemy.draggedByIce()) {
                continue;
            }

            if (!entityIsInDirection(ice, enemy, ice.direction())) {
                continue;
            }

            double distance = ice.distanceCenterToCenter(enemy);
            double movementLength = Math.abs(movement.x()) + Math.abs(movement.y());
            double contactDistance = ice.step().x();

            /*
             * Epsilon assez large pour détecter l'ennemi avant que
             * le moteur de collision bloque le IceBlock.
             */
            double epsilon = 0.35;

            if (distance <= contactDistance + movementLength + epsilon) {
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestEnemy = enemy;
                }
            }
        }

        return closestEnemy;
    }

    private boolean entityIsInDirection(Entity from, Entity target, int direction) {
        if (from == null || target == null) {
            return false;
        }

        if (from.position() == null || target.position() == null) {
            return false;
        }

        int fx = from.position().x();
        int fy = from.position().y();

        int tx = target.position().x();
        int ty = target.position().y();

        switch (direction) {
            case 0:
                return ty == fy && tx > fx;
            case 90:
                return tx == fx && ty > fy;
            case 180:
                return ty == fy && tx < fx;
            case 270:
                return tx == fx && ty < fy;
            default:
                return false;
        }
    }

    private Entity firstSolidAt(Grid.Position p, Entity ignoreA, Entity ignoreB) {
        if (p == null) {
            return null;
        }

        for (Entity e : new ArrayList<Entity>(entities())) {
            if (e == null) {
                continue;
            }

            if (e == ignoreA || e == ignoreB) {
                continue;
            }

            if (e.position() == null) {
                continue;
            }

            if (e.position().x() != p.x()
                    || e.position().y() != p.y()) {
                continue;
            }

            if (isCrushObstacle(e)) {
                return e;
            }
        }

        return null;
    }

    private boolean isCrushObstacle(Entity e) {
        if (e == null) {
            return false;
        }

        /*
         * DiamondBlock et GoldBlock héritent de IceBlock,
         * donc ils sont inclus ici.
         */
        return e instanceof Wall || e instanceof IceBlock;
    }

    private void crushEnemyByIce(IceBlock ice, Enemy enemy, Grid.Position finalIcePosition) {
        if (ice == null || enemy == null) {
            return;
        }

        System.out.println("CRUSH ENEMY BY ICE");

        enemy.markCrushedByIce();

        remove(enemy);

        addScore(100);

        ice.detachEnemy();
        ice.stopSlide();

        /*
         * Le IceBlock prend exactement la place de l'ennemi.
         */
        if (finalIcePosition != null) {
            ice.setPosition(finalIcePosition);
            ice.setBounding();
        }
    }

    private Grid.Position copyPosition(Grid.Position p) {
        if (p == null) {
            return null;
        }

        return grid().new Position(p.x(), p.y());
    }
}