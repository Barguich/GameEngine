package gal.action;

import java.util.List;

import gal.arguments.Direction;
import geometry.Grid;
import model.Entity;
import model.Model;
import pengo.model.IceBlock;

public class Hit extends GALAction {

	private final Direction direction;

	public Hit(Direction direction) {
		this.direction = direction;
	}

	@Override
	public boolean exec(Entity e) {
		if (e == null || e.position() == null || e.model() == null)
			return false;

		Grid.Position target = targetPosition(e);
		if (target == null)
			return false;

		Model model = e.model();
		List<Entity> occupants = model.entitiesAt(target);

		if (occupants.isEmpty())
			return false;

		// Frappe la première entité destructible trouvée (pas l'entité elle-même)
		for (Entity victim : occupants) {
			if (victim == e)
				continue;

			// Les blocs destructibles par l'ennemi (IceBlock et sous-classes
			// comme BlockRespawn). On s'appuie sur destructibleByEnemy() pour
			// exclure les blocs spéciaux (Diamond, Gold).
			if (victim instanceof IceBlock
					&& ((IceBlock) victim).destructibleByEnemy()) {
				return ((IceBlock) victim).destroyByEnemy();
			}
		}
		return false;
	}

	// ── Calcul de la case cible ──────────────────────────────────────────────

	private Grid.Position targetPosition(Entity e) {
		Grid.Position pos = e.position();

		// Résolution de la direction relative en angle absolu
		int absAngle = resolveAngle(e);

		int dx = 0, dy = 0;
		// Convention moteur : 0°=Est, 90°=Sud, 180°=Ouest, 270°=Nord
		switch (absAngle) {
			case 0:
				dx = 1;
				dy = 0;
				break; // Est
			case 90:
				dx = 0;
				dy = 1;
				break; // Sud
			case 180:
				dx = -1;
				dy = 0;
				break; // Ouest
			case 270:
				dx = 0;
				dy = -1;
				break; // Nord
			default:
				dx = (int) Math.round(Math.cos(Math.toRadians(absAngle)));
				dy = -(int) Math.round(Math.sin(Math.toRadians(absAngle)));
				break;
		}

		Grid.Position result = pos.copy();
		result.translate(pos.grid().new Vector(dx, dy));
		return result;
	}

	private int resolveAngle(Entity e) {
		if (direction == null || direction == Direction.F)
			return e.orientation();
		if (direction == Direction.B)
			return (e.orientation() + 180) % 360;
		if (direction == Direction.L)
			return (e.orientation() + 270) % 360;
		if (direction == Direction.R)
			return (e.orientation() + 90) % 360;
		if (direction == Direction.H)
			return e.orientation();
		if (direction.isAbsolute())
			return ((direction.toAngle() % 360) + 360) % 360;
		return e.orientation();
	}
}