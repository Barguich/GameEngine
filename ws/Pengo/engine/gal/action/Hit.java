package gal.action;

import gal.arguments.Direction;
import model.Entity;
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
		if (e == null || e.position() == null) {
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
	}

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
