package pengo.model;

import model.Entity;

public class GoldBlock extends IceBlock {

	// Durée du gel appliqué aux ennemis
	private static final long FREEZE_DURATION = 5000;

	// Durée du bonus de score x2
	private static final long DOUBLE_SCORE_DURATION = 5000;

	/*
	 * Empêche l'activation répétée du GoldBlock tant que son effet est encore
	 * actif.
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
	 * Activation globale : tous les ennemis sont gelés.
	 */

	public void activate(PengoModel model) {
		if (model == null) {
			return;
		}

		if (active) {
			return;
		}

		model.freezeEnemies(FREEZE_DURATION);
		model.activateDoubleScore(DOUBLE_SCORE_DURATION);

		active = true;
		activeRemaining = FREEZE_DURATION;
	}

	/*
	 * Activation normale : seul l'ennemi ayant touché le GoldBlock est gelé.
	 */

	public void activate(PengoModel model, Enemy enemy) {
		if (model == null || enemy == null) {
			return;
		}

		if (active) {
			return;
		}

		// Les ennemis déjà neutralisés ne déclenchent pas l'effet
		if (enemy.dead() || enemy.dying() || enemy.draggedByIce()) {
			return;
		}

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

		// Un ennemi qui touche le GoldBlock déclenche son pouvoir
		if (e instanceof Enemy && model instanceof PengoModel) {
			activate((PengoModel) model, (Enemy) e);
			return;
		}

		// Pour les autres interactions, le comportement reste celui d'un IceBlock

		super.collision(e);
	}

	@Override
	public void tick(long elapsed) {
		super.tick(elapsed);

		// Gestion du temps de recharge du GoldBlock
		if (active) {
			activeRemaining -= elapsed;

			if (activeRemaining <= 0) {
				active = false;
				activeRemaining = 0;
			}
		}
	}
}