package view;

import model.Entity;

/**
 * Infère une étiquette d'action lisible pour une entité, à partir de ses
 * données publiques (vitesse linéaire, orientation, catégorie).
 *
 * <p>
 * [dette technique] La spec demande d'afficher « l'action » de l'entité. Or
 * {@code Bot} n'expose aucun état d'action courant (think/done/collision
 * uniquement) et appartient à un coéquipier — on ne peut pas le modifier pour
 * exposer un {@code currentAction()}. On infère donc l'action observable depuis
 * la cinématique de l'entité. C'est une approximation : elle décrit ce que
 * l'entité FAIT, pas ce que le bot a DÉCIDÉ.
 *
 * <p>
 * [à refactorer] Le vrai fix serait d'ajouter une méthode
 * {@code String label()} à l'interface Bot (ou un enum d'action partagé) et de
 * la lire ici. À négocier avec le propriétaire de engine.brain.
 */
final class ActionInference {

	/** En dessous de ce seuil (cm/tick), on considère l'entité immobile. */
	private static final double IDLE_SPEED_THRESHOLD = 1e-3;

	private ActionInference() {
	}

	static String describe(Entity e) {
		if (e == null) {
			return "—";
		}

		String motion = motionLabel(e);
		String heading = cardinal(e.orientation());
		String role = roleLabel(e);

		return role + " · " + motion + " · " + heading + " (" + e.orientation() + "°)";
	}

	private static String motionLabel(Entity e) {
		if (e.linearSpeed() == null) {
			return "IDLE";
		}
		double speed = e.linearSpeed().norm();
		return (speed > IDLE_SPEED_THRESHOLD) ? "MOVING" : "IDLE";
	}

	private static String roleLabel(Entity e) {
		// category() peut être null tant que l'entité n'a pas de rôle gameplay.
		return (e.category() == null) ? "ENTITY" : e.category().toString();
	}

	/**
	 * Mappe une orientation en degrés vers la direction cardinale la plus proche.
	 */
	private static String cardinal(int degree) {
		int d = ((degree % 360) + 360) % 360;
		if (d >= 315 || d < 45) {
			return "E";
		}
		if (d < 135) {
			return "S";
		}
		if (d < 225) {
			return "W";
		}
		return "N";
	}
}