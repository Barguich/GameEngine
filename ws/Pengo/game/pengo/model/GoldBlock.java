package pengo.model;

import model.Entity;

public class GoldBlock extends IceBlock {

    private static final long FREEZE_DURATION = 5000;
    private static final long DOUBLE_SCORE_DURATION = 5000;

    /*
     * Cooldown pour éviter que le GoldBlock active l'effet
     * 30 fois par seconde si l'ennemi reste collé dessus.
     */
    private boolean active;
    private long activeRemaining;

    public GoldBlock() {
        super();

        active = false;
        activeRemaining = 0;
    }

    public boolean active() {
        return active;
    }

    /*
     * Activation générale : utile si tu veux un bouton/test
     * qui gèle tous les ennemis.
     */
    public void activate(PengoModel model) {
        if (model == null) {
            return;
        }

        if (active) {
            return;
        }

        System.out.println("GOLD BLOCK ACTIVATED - ALL ENEMIES FREEZE");

        model.freezeEnemies(FREEZE_DURATION);
        model.activateDoubleScore(DOUBLE_SCORE_DURATION);

        active = true;
        activeRemaining = FREEZE_DURATION;
    }

    /*
     * Activation normale du jeu :
     * un ennemi touche le GoldBlock, donc seulement cet ennemi est gelé.
     */
    public void activate(PengoModel model, Enemy enemy) {
        if (model == null || enemy == null) {
            return;
        }

        if (active) {
            return;
        }

        if (enemy.dead() || enemy.dying() || enemy.draggedByIce()) {
            return;
        }

        System.out.println("GOLD BLOCK ACTIVATED - ENEMY FREEZE");

        enemy.freeze(FREEZE_DURATION);
        model.activateDoubleScore(DOUBLE_SCORE_DURATION);

        active = true;
        activeRemaining = FREEZE_DURATION;
    }

    @Override
    public void collision(Entity e) {
        if (e == null) {
            return;
        }

        /*
         * Si un ennemi touche le GoldBlock,
         * on déclenche l'effet.
         */
        if (e instanceof Enemy && model instanceof PengoModel) {
            activate((PengoModel) model, (Enemy) e);
            return;
        }

        /*
         * Sinon, le GoldBlock reste un bloc de glace spécial :
         * il peut être poussé comme un IceBlock.
         */
        super.collision(e);
    }

    @Override
    public void tick(long elapsed) {
        super.tick(elapsed);

        if (active) {
            activeRemaining -= elapsed;

            if (activeRemaining <= 0) {
                active = false;
                activeRemaining = 0;

                System.out.println("GOLD BLOCK READY AGAIN");
            }
        }
    }
}