package pengo.model;

import collision.Bounding;
import collision.Circle;
import model.Entity;

public class Enemy extends Entity {

	private boolean frozen;
	private long frozenRemaining;
	private boolean dying;
	private long dyingRemaining;
	private long spawnAnimationRemaining;

	private boolean dead;

	public Enemy() {
		super("Enemy");

		this.frozen = false;
		this.frozenRemaining = 0;
		this.dead = false;
		this.dying = false;
		this.dyingRemaining = 0;
		this.spawnAnimationRemaining = 800;
	}

	public boolean frozen() {
		return frozen;
	}

	public boolean dead() {
		return dead;
	}
	public boolean dying() {
	    return dying;
	}

	public long dyingRemaining() {
	    return dyingRemaining;
	}
	public boolean spawning() {
	    return spawnAnimationRemaining > 0;
	}

	public long spawnAnimationRemaining() {
	    return spawnAnimationRemaining;
	}

	public void freeze(long duration_ms) {
		assert duration_ms >= 0;

		if (dead) {
			return;
		}

		System.out.println("ENEMY FREEZE");

		frozen = true;
		frozenRemaining = duration_ms;
		stop();
	}

	public void unfreeze() {
		System.out.println("ENEMY UNFREEZE");

		if (dead) {
			return;
		}

		frozen = false;
		frozenRemaining = 0;
	}
	public boolean moving() {
	    return linearSpeed() != null && linearSpeed().norm() > 0;
	}

	public void kill() {
	    if (dead || dying) {
	        return;
	    }

	    dying = true;
	    dyingRemaining = 1000;
	    frozen = false;
	    frozenRemaining = 0;
	    stop();

	    System.out.println("Enemy dying animation");
	}

	@Override
	public void tick(long elapsed) {
		assert elapsed >= 0;
		if (spawnAnimationRemaining > 0) {
		    spawnAnimationRemaining -= elapsed;

		    if (spawnAnimationRemaining < 0) {
		        spawnAnimationRemaining = 0;
		    }
		}
		if (dying) {
		    dyingRemaining -= elapsed;

		    if (dyingRemaining <= 0) {
		        dead = true;
		        dying = false;
		        dyingRemaining = 0;

		        if (model != null) {
		            model.remove(this);
		        }
		    }

		    return;
		}

		if (dead) {
			return;
		}

		if (frozen) {
			frozenRemaining -= elapsed;

			if (frozenRemaining <= 0) {
				unfreeze();
			}

			return;
		}

		super.tick(elapsed);
	}

	@Override
	public void setBounding() {
		if (center == null || size == null) {
			return;
		}

		bounding = new Bounding();

		double radius = Math.min(size.x(), size.y()) / 2.0;
		bounding.add(new Circle(center, radius));
	}
}