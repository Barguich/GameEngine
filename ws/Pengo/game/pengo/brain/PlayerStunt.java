package pengo.brain;

import java.util.List;

import geometry.Grid;
import geometry.Grid.Cell;
import model.Entity;
import model.Stunt;
import pengo.model.Enemy;
import pengo.model.FishBonus;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import pengo.model.Wall;

public class PlayerStunt extends Stunt {

	private double speed_cm_s = 15.0;
	private boolean moving;
	private Grid.Position targetCell;
	private Grid.Position originCell;
	private FishBonus pendingFishBonus;

	public PlayerStunt(PengoModel model, PengoPlayer player) {
		super(model, player);
	}

	public void setSpeed(double cm_s) {
		this.speed_cm_s = cm_s;
	}

	public boolean busy() {
		return moving;
	}

	@Override
	public void walk(int direction) {
		attemptOneCellMove(direction, false);
	}

	public void actInDirection(int direction) {
		attemptOneCellMove(direction, true);
	}

	private void attemptOneCellMove(int direction, boolean allowPush) {
		if (moving)
			return;
		if (entity.position() == null)
			return;

		PengoPlayer pp = (PengoPlayer) entity;
		PengoModel pm = (PengoModel) model;

		if (!pm.tryEnterCellForPlayer(pp, direction, allowPush)) {
			return;
		}

		int x = pp.position().x();
		int y = pp.position().y();
		switch (direction) {
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
			return;
		}
		targetCell = pm.grid().new Position(x, y);
		originCell = pm.grid().new Position(pp.position().x(), pp.position().y());

		pendingFishBonus = null;
		Entity onTarget = pm.firstAt(targetCell);
		if (onTarget instanceof FishBonus) {
			pendingFishBonus = (FishBonus) onTarget;
		}

		pp.turnTo(direction);
		double s = speed_cm_s * pp.speedMultiplier();
		switch (direction) {
		case 0:
			pp.setLinearSpeed(pp.center().isu().new Vector(s, 0));
			break;
		case 90:
			pp.setLinearSpeed(pp.center().isu().new Vector(0, s));
			break;
		case 180:
			pp.setLinearSpeed(pp.center().isu().new Vector(-s, 0));
			break;
		case 270:
			pp.setLinearSpeed(pp.center().isu().new Vector(0, -s));
			break;
		}
		moving = true;
	}

	@Override
	public void update(long elapsed) {
		if (!moving)
			return;
		if (entity.position() == null || targetCell == null)
			return;

		if (entity.position().x() == targetCell.x() && entity.position().y() == targetCell.y()) {
			finishOnTarget();
		}
	}

	@Override
	public void collision(Entity e) {
		if (e == null)
			return;

		PengoModel pm = (PengoModel) model;

		if (pm.lost() || pm.won()) {
			cclean();
			return;
		}

		if (e instanceof Wall) {
			pm.startWallVibration(e, 1500);
			cancelAndSnapBack();
			return;
		}

		if (e instanceof Enemy) {
			Enemy enemy = (Enemy) e;

			if (enemy.eatableByPlayer()) {
				pm.eatFrozenEnemy(enemy);
				finishOnTarget();
				return;
			}

			if (enemy.harmlessForPlayer()) {
				cancelAndSnapBack();
				return;
			}
			pm.loseLife();
			cancelAndSnapBack();
			return;
		}

		if (e instanceof FishBonus) {
			pendingFishBonus = (FishBonus) e;
			finishOnTarget();
			return;
		}

		if (e instanceof IceBlock) {
			stopOnCurrentCell();
			return;
		}
	}

	@Override
	public void collision(List<Entity> others) {
		for (Entity e : others)
			collision(e);
	}

	@Override
	public void done() {
		cclean();
	}

	@Override
	public void set(int orientation) {
		entity.turnTo(orientation);
	}

	@Override
	public void set(Cell c) {
		if (c == null)
			return;
		entity.setPosition(c.position());
	}

	@Override
	public void set(double x_cm, double y_cm) {
		entity.setCoord(entity.center().isu().new Coord(x_cm, y_cm));
	}

	private void finishOnTarget() {
		if (targetCell != null) {
			entity.setPosition(targetCell);
		}
		if (pendingFishBonus != null && !pendingFishBonus.consumed()) {
			pendingFishBonus.consume((PengoPlayer) entity);
		}
		cclean();
	}

	private void cancelAndSnapBack() {
		if (originCell != null) {
			entity.setPosition(originCell);
		}
		cclean();
	}

	private void stopOnCurrentCell() {
		if (entity.position() != null) {
			PengoModel pm = (PengoModel) model;
			Grid.Position current = pm.grid().new Position(entity.position().x(), entity.position().y());
			entity.setPosition(current);
		}
		cclean();
	}

	private void cclean() {
		moving = false;
		targetCell = null;
		originCell = null;
		pendingFishBonus = null;
		entity.stop();
	}

	public void reset() {
		cclean();
	}
}
