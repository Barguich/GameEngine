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
	private boolean breakingAnimation;
	private long breakingAnimationRemaining;
	private int breakingFrame;

	public IceBlock() {
		super("IceBlock");
		sliding = false;
		direction = 0;
		broken = false;
		hp = 3;// destruction totale sur 3 coups
		friction = 0.98;
		breakingAnimation = false;
		breakingAnimationRemaining = 0;
		breakingFrame = 0;
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

	public boolean breakingAnimation() {
		return breakingAnimation;
	}

	public int breakingFrame() {
		return breakingFrame;
	}

	public void damage() {
		if (broken || breakingAnimation) {
			return;
		}

		hp--;

		System.out.println("ICEBLOCK DAMAGE, hp = " + hp);

		breakingAnimation = true;
		breakingAnimationRemaining = 300;
		breakingFrame = 0;
	}

	public void startSlide(int direction) {
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
		if (e == null) {
			return;
		}

		if (e instanceof PengoPlayer) {
			stopSlide();
			return;
		}

		if (sliding && e instanceof Enemy && model instanceof PengoModel) {
			PengoModel pm = (PengoModel) model;
			Enemy enemy = (Enemy) e;

			Grid.Position next = pm.nextPosition(enemy, direction);

			if (pm.blocked(next)) {
				pm.killEnemy(enemy);
			} else {
				enemy.setPosition(next);
			}

			return;
		}

		if (e instanceof Wall || e instanceof IceBlock) {
			stopSlide();
			return;
		}
	}

	@Override
	public void tick(long elapsed) {
		if (breakingAnimation) {
			breakingAnimationRemaining -= elapsed;

			if (breakingAnimationRemaining > 200) {
				breakingFrame = 0;
			} else if (breakingAnimationRemaining > 100) {
				breakingFrame = 1;
			} else {
				breakingFrame = 2;
			}

			if (breakingAnimationRemaining <= 0) {
				breakingAnimation = false;
				breakingAnimationRemaining = 0;

				if (hp <= 0) {
					breakBlock();
					return;
				}
			}
		}
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

		boolean moved = model.move(this, movement);

		if (!moved) {
			stopSlide();
			return;
		}

		if (linearSpeed() != null) {
			linearSpeed().scale(friction);

			if (linearSpeed().norm() < 0.5) {
				stopSlide();
			}
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