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
	private boolean draggedByIce;
	private boolean crushedByIce;

	private boolean dead;

	public Enemy() {
		super("Enemy");

		this.frozen = false;
		this.frozenRemaining = 0;
		this.dead = false;
		this.dying = false;
		this.dyingRemaining = 0;
		this.spawnAnimationRemaining = 800;
		this.draggedByIce = false;
		this.crushedByIce = false;
	}
	public boolean crushedByIce() {
	    return crushedByIce;
	}

	public boolean harmlessForPlayer() {
	    return dead || dying || draggedByIce || crushedByIce;
	}

	public void markCrushedByIce() {
	    crushedByIce = true;
	    draggedByIce = false;
	    frozen = false;
	    frozenRemaining = 0;
	    stop();

	    System.out.println("ENEMY MARKED CRUSHED BY ICE");
	}
	public boolean draggedByIce() {
	    return draggedByIce;
	}

	public void startDraggedByIce() {
	    if (dead || dying || crushedByIce) {
	        return;
	    }

	    draggedByIce = true;
	    frozen = false;
	    frozenRemaining = 0;
	    stop();

	    System.out.println("ENEMY START DRAGGED BY ICE");
	}

	public void stopDraggedByIce() {
	    draggedByIce = false;
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
	    draggedByIce = false;

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
		if (draggedByIce) {
		    stop();
		    return;
		}

		super.tick(elapsed);
	}
	@Override
	public void collision(Entity e) {
	    if (e == null) {
	        return;
	    }

	    /*
	     * Si l'ennemi est mort, en train de mourir,
	     * transporté ou écrasé par un IceBlock,
	     * il ne doit jamais déclencher de collision normale.
	     */
	    if (harmlessForPlayer()) {
	        return;
	    }

	    /*
	     * Si un IceBlock en glissade le touche,
	     * on ne laisse pas le moteur gérer ça normalement.
	     * C'est PengoModel.moveSlidingIceBlock() qui gère.
	     */
	    if (e instanceof IceBlock) {
	        IceBlock ice = (IceBlock) e;

	        if (ice.sliding()) {
	            return;
	        }
	    }

	    super.collision(e);
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