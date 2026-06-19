package pengo.model;

import collision.Bounding;
import collision.Rect;
import model.Entity;

public class IceBlock extends Entity {

	private boolean sliding;
	private int direction;
	private boolean broken;
	private int hp;

	public IceBlock() {
		super("IceBlock");
		sliding = false;
		direction = 0;
		broken = false;
		hp = 3;
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

		if (e instanceof Wall || e instanceof IceBlock) {
			stopSlide();
		}

		// Important :
		// PAS de super.collision(e), sinon Entity.collision() fait stop()
		// et le bloc s'arrête immédiatement.
	}

	@Override
	public void tick(long elapsed) {
		if (broken) {
			return;
		}

		super.tick(elapsed);

		if (sliding && linearSpeed() != null && linearSpeed().norm() == 0) {
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