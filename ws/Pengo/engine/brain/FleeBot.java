package brain;

import model.BasicStunt;
import model.Entity;

public class FleeBot extends Bot {

	private boolean moving;

	private Entity enemy;

	public FleeBot(BasicStunt stunt, Entity enemy) {
		super(stunt);

		this.enemy = enemy;
		this.moving = false;
	}

	/**
	 * Choisit une direction permettant de s'éloigner de l'ennemi. Le bot attend la
	 * fin du déplacement précédent avant de prendre une nouvelle décision.
	 */
	@Override
	public void think() {
		if (moving) {
			return;
		}

		Entity me = stunt.entity();

		int ex = me.position().x();
		int ey = me.position().y();

		int tx = enemy.position().x();
		int ty = enemy.position().y();

		moving = true;

		// Déplacement dans la direction opposée à l'ennemi
		if (tx > ex) {
			stunt.walk(180);
		} else if (tx < ex) {
			stunt.walk(0);
		} else if (ty > ey) {
			stunt.walk(270);
		} else if (ty < ey) {
			stunt.walk(90);
		} else {
			moving = false;
		}
	}

	/**
	 * Appelée lorsque le déplacement est terminé.
	 */
	@Override
	public void done() {
		moving = false;
	}

	/**
	 * Réinitialise le bot après une collision afin qu'il puisse recalculer une
	 * nouvelle direction.
	 */
	@Override
	public void collision(Entity e) {
		moving = false;
	}
}