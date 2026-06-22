package brain;

import model.Entity;
import model.Stunt;

public class AttackBot extends Bot {

	// Indique si l'ennemi est déjà en train de se déplacer
	private boolean moving;

	// Cible à poursuivre (généralement le joueur)
	private Entity target;

	public AttackBot(Stunt stunt, Entity target) {
		super(stunt);
		this.target = target;
		this.moving = false;
	}

	@Override
	public void think() {

		// Si un déplacement est déjà en cours,
		// on ne calcule pas une nouvelle direction
		if (moving) {
			return;
		}

		// Entité contrôlée par ce bot
		Entity me = stunt.entity();

		// Position actuelle de l'ennemi
		double ex = me.center().x();
		double ey = me.center().y();

		// Position de la cible
		double tx = target.center().x();
		double ty = target.center().y();

		// Un nouveau déplacement va commencer
		moving = true;

		/*
		 * Le principe est simple : on compare la position de l'ennemi avec celle de la
		 * cible puis on avance dans la direction qui permet de s'en rapprocher.
		 */

		if (tx > ex) {

			// La cible est à droite
			stunt.walk(0);

		} else if (tx < ex) {

			// La cible est à gauche
			stunt.walk(180);

		} else if (ty > ey) {

			// La cible est en dessous
			stunt.walk(270);

		} else if (ty < ey) {

			// La cible est au-dessus
			stunt.walk(90);

		} else {

			// L'ennemi est déjà sur la même position que la cible
			moving = false;
		}
	}

	@Override
	public void done() {

		// Le déplacement est terminé,
		// le bot pourra choisir une nouvelle direction
		moving = false;
	}

	@Override
	public void collision(Entity e) {

		// En cas de collision, on arrête le déplacement actuel
		// afin de recalculer une direction au prochain tick
		moving = false;
	}
}