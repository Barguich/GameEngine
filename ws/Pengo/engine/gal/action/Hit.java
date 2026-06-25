package gal.action;

import java.util.List;

import gal.arguments.Direction;
import gal_engine.GALStunt;
import geometry.Grid;
import model.Entity;
import model.Model;

public class Hit extends GALAction {

	// Direction dans laquelle l'action Hit doit être appliquée.
	// Elle peut être absolue (N, S, E, W) ou relative (F, B, L, R).
	private final Direction direction;

	public Hit(Direction direction) {
		this.direction = direction;
	}

	@Override
	public boolean exec(Entity e) {
		if (e == null || e.position() == null || e.model() == null) {
			return false;
		}

		// On cherche la case ciblée par le coup.
		Grid.Position target = targetPosition(e);

		if (target == null) {
			return false;
		}

		Model model = e.model();
		List<Entity> occupants = model.entitiesAt(target);

		if (occupants.isEmpty()) {
			return false;
		}

		/*
		 * L'action Hit s'applique à la première entité de la case cible
		 * capable de recevoir un coup GAL.
		 *
		 * La méthode receiveGalHit(...) permet de laisser chaque entité
		 * décider elle-même de sa réaction.
		 */
		for (Entity victim : occupants) {
			if (victim != e && victim.receiveGalHit(e)) {

				// Après un coup réussi, l'entité reste bloquée un court instant.
				if (e.stunt() instanceof GALStunt stunt) {
					stunt.startWaiting(750);
				}

				return true;
			}
		}

		return false;
	}

	// Calcule la case visée par l'action Hit.
	private Grid.Position targetPosition(Entity e) {
		Grid.Position pos = e.position();

		/*
		 * Les directions GAL peuvent être relatives à l'entité.
		 * On commence donc par convertir la direction demandée
		 * en angle absolu dans le repère du moteur.
		 */
		int absAngle = resolveAngle(e);

		int dx = 0;
		int dy = 0;

		// Convention du moteur :
		// 0° = Est, 90° = Sud, 180° = Ouest, 270° = Nord.
		switch (absAngle) {
			case 0:
				dx = 1;
				break;

			case 90:
				dy = 1;
				break;

			case 180:
				dx = -1;
				break;

			case 270:
				dy = -1;
				break;

			default:
				dx = (int) Math.round(Math.cos(Math.toRadians(absAngle)));
				dy = -(int) Math.round(Math.sin(Math.toRadians(absAngle)));
				break;
		}

		Grid.Position result = pos.copy();
		result.translate(pos.grid().new Vector(dx, dy));

		return result;
	}

	// Convertit une direction GAL en angle absolu.
	private int resolveAngle(Entity e) {
		if (direction == null || direction == Direction.F) {
			return e.orientation();
		}

		if (direction == Direction.B) {
			return (e.orientation() + 180) % 360;
		}

		if (direction == Direction.L) {
			return (e.orientation() + 270) % 360;
		}

		if (direction == Direction.R) {
			return (e.orientation() + 90) % 360;
		}

		if (direction == Direction.H) {
			return e.orientation();
		}

		if (direction == Direction.N) {
			return 270;
		}

		if (direction == Direction.S) {
			return 90;
		}

		if (direction == Direction.E) {
			return 0;
		}

		if (direction == Direction.W) {
			return 180;
		}

		if (direction.isAbsolute()) {
			return ((direction.toAngle() % 360) + 360) % 360;
		}

		return e.orientation();
	}
}