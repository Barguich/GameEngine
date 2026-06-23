package gal.action;

import java.util.List;

import gal.arguments.Direction;
import model.Entity;
import model.Model;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import geometry.Grid;

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

		int angle = resolveAngle(direction, e.orientation());

		int x = e.position().x();
		int y = e.position().y();

		switch (angle) {
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
				return false;
		}

		if (!(e.model() instanceof PengoModel pm)) {
			return false;
		}

		Grid.Position target = pm.grid().new Position(x, y);
		Entity hit = pm.firstAt(target);

		if (hit instanceof IceBlock ice) {
			ice.damage();
			return true;
		}

		return false;

	private int resolveAngle(Direction d, int orientation) {
		if (d.isAbsolute()) {
			return d.toAngle();
		}
		if (d == Direction.H) {
			return orientation;
		}
		return ((orientation + d.toAngle()) % 360 + 360) % 360;
	}
}
