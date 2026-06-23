package pengo.model;

import model.Entity;

public class GoldBlock extends IceBlock {

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

	// Le GoldBlock est un bloc spécial : un ennemi ne le détruit pas.
	@Override
	public boolean destructibleByEnemy() {
		return false;
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

		// Durées lues depuis le fichier de configuration (equilibrage 3.9)
		long freeze = model.config().goldFreezeDuration();
		long doubleScore = model.config().goldDoubleScoreDuration();

		model.freezeEnemies(freeze);
		model.activateDoubleScore(doubleScore);

		active = true;
		activeRemaining = freeze;
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

		// Durées lues depuis le fichier de configuration (equilibrage 3.9)
		long freeze = model.config().goldFreezeDuration();
		long doubleScore = model.config().goldDoubleScoreDuration();

		enemy.freeze(freeze);
		model.activateDoubleScore(doubleScore);

		active = true;
		activeRemaining = freeze;
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
