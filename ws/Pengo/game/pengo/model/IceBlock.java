package pengo.model;

import collision.Bounding;
import collision.Rect;
import geometry.Grid;
import model.Entity;

public class IceBlock extends Entity {

	private boolean sliding;
	private int direction;
	private boolean broken;
	private int hp;
	private double friction;
	private Enemy draggedEnemy;

	public IceBlock() {
		super("IceBlock");
		sliding = false;
		direction = 0;
		broken = false;
		hp = 3;// destruction totale sur 3 coups
		friction = 0.98;
		draggedEnemy = null;
	}

	public double friction() {
		return friction;
	}

	public boolean sliding() {
		return sliding;
	}

	public int direction() {
		return direction;
	}

	public boolean broken() {
		return broken;
	}

	public int hp() {
		return hp;
	}

	public boolean cracked() {// 1er coup une fissure simple
		return hp == 2;
	}

	public boolean veryCracked() {// le 2 emme coup a bigger crack
		return hp == 1;
	}

	public void damage() {
		if (broken) {
			return;
		}

		hp--;

		System.out.println("ICEBLOCK DAMAGE, hp = " + hp);

		if (hp <= 0) {
			breakBlock();
		}
	}

	public void startSlide(int direction) {

		if (sliding) {
			return;
		}

		System.out.println("ICE START direction = " + direction);

		this.direction = direction;
		this.sliding = true;
		double speed = 12.0;

		if (isu == null) {
			System.out.println("ICE ISU NULL");
			return;
		}

		if (direction == 0) {
			setLinearSpeed(isu.new Vector(speed, 0));
		} else if (direction == 90) {
			setLinearSpeed(isu.new Vector(0, speed));
		} else if (direction == 180) {
			setLinearSpeed(isu.new Vector(-speed, 0));
		} else if (direction == 270) {
			setLinearSpeed(isu.new Vector(0, -speed));
		}

		System.out.println("ICE SPEED = " + linearSpeed());
	}

	public void stopSlide() {
		sliding = false;
		stop();
	}

	public void breakBlock() {
		broken = true;

		if (model != null) {
			model.remove(this);
		}
	}

	@Override
	public void collision(Entity e) {

		if (e == null)
			return;

		if (e instanceof PengoPlayer)
			return;

		if (sliding && e instanceof Enemy) {
			draggedEnemy = (Enemy) e;
		}
	}

	@Override
	public void tick(long elapsed) {

		if (broken) {
			return;
		}

		if (!sliding) {
			super.tick(elapsed);
			return;
		}

		if (model == null || center == null || linearSpeed() == null) {
			return;
		}

		if (linearSpeed().norm() == 0) {
			stopSlide();
			return;
		}

		double dt = elapsed / 1000.0;

		geometry.ISU.Vector movement = center.isu().new Vector(linearSpeed().x() * dt, linearSpeed().y() * dt);

		translate(movement);

		java.util.List<Entity> cols = model.collisions(this);

		boolean blocked = false;

		for (Entity other : cols) {

			if (other instanceof Enemy) {
				draggedEnemy = (Enemy) other;
			}

			if (other instanceof Wall || other instanceof IceBlock) {
				blocked = true;
			}
		}

		if (draggedEnemy != null && !draggedEnemy.dead()) {
			draggedEnemy.translate(movement);
		}

		if (blocked) {

			translate(center.isu().new Vector(-movement.x(), -movement.y()));

			if (draggedEnemy != null) {

				draggedEnemy.kill();

				if (model instanceof PengoModel) {
					((PengoModel) model).addScore(100);
				}

				draggedEnemy = null;
			}

			stopSlide();
		}
	}

	@Override
	public void setBounding() {
		if (center == null || size == null) {
			return;
		}

		bounding = new Bounding();
		bounding.add(new Rect(center, size, orientation_degree));
	}
}