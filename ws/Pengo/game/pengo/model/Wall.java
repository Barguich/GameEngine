package pengo.model;

import model.Entity;

/**
 * Représente un mur fixe de la carte.
 *
 * Les murs délimitent l'aire de jeu et arrêtent les déplacements des blocs de
 * glace ainsi que ceux des autres entités.
 *
 * Dans Pengo, lorsqu'un joueur pousse contre une bordure, le mur déclenche une
 * courte animation de vibration afin de reproduire le comportement du jeu
 * original.
 */
public class Wall extends Entity {

	// Temps restant de l'animation de vibration.
	private long vibrationRemaining;

	// Compteur utilisé pour alterner les positions
	// successives pendant l'effet de secousse.
	private int vibrationFrame;

	public Wall() {
		super("Wall");

		this.vibrationRemaining = 0;
		this.vibrationFrame = 0;
	}

	/**
	 * Démarre une courte animation de vibration.
	 *
	 * Cette méthode est appelée lorsqu'un joueur frappe ou pousse une bordure du
	 * niveau.
	 */
	public void vibrate() {
		vibrationRemaining = 200;
		vibrationFrame = 0;
	}

	/**
	 * Met à jour l'état de l'animation.
	 *
	 * Tant que le temps restant n'est pas écoulé, le mur continue à osciller
	 * visuellement.
	 */
	public void tick(long elapsed) {
		if (vibrationRemaining > 0) {

			vibrationRemaining -= elapsed;
			vibrationFrame++;

			if (vibrationRemaining < 0) {
				vibrationRemaining = 0;
			}
		}
	}

	/**
	 * Indique si le mur est actuellement en train de vibrer.
	 */
	public boolean isVibrating() {
		return vibrationRemaining > 0;
	}

	/**
	 * Décalage graphique utilisé pour créer l'effet de vibration.
	 *
	 * La valeur alterne entre gauche et droite pendant toute la durée de
	 * l'animation.
	 */
	public int vibrationOffset() {
		if (!isVibrating()) {
			return 0;
		}

		return (vibrationFrame % 2 == 0) ? 3 : -3;
	}

	@Override
	public void setBounding() {
		if (center == null || size == null) {
			return;
		}

		bounding = new collision.Bounding();

		/*
		 * Les murs utilisent une hitbox rectangulaire légèrement réduite afin d'éviter
		 * les faux contacts entre deux cases adjacentes.
		 */
		bounding.add(collision.Hitbox.shrunkRect(center, size, orientation_degree));
	}
}