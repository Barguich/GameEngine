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
	    setBot(null);

	    /*
	     * Recalcule la hitbox.
	     * Comme draggedByIce = true, la hitbox devient vide.
	     */
	    setBounding();

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
	     * Si l'ennemi est transporté / tué / gelé,
	     * il ne doit plus déclencher de collision normale.
	     */
	    if (harmlessForPlayer()) {
	        return;
	    }

	    /*
	     * Très important :
	     * si un IceBlock glissant touche l'ennemi,
	     * l'ennemi ne doit pas bloquer le IceBlock.
	     */
	    if (e instanceof IceBlock) {
	        IceBlock ice = (IceBlock) e;

	        if (ice.sliding()) {
	            System.out.println("ENEMY IGNORE SLIDING ICEBLOCK COLLISION");
	            return;
	        }
	    }

	    /*
	     * GoldBlock avant IceBlock,
	     * parce que GoldBlock extends IceBlock.
	     */
	    if (e instanceof GoldBlock && model instanceof PengoModel) {
	        GoldBlock gold = (GoldBlock) e;
	        gold.activate((PengoModel) model, this);
	        return;
	    }

	    super.collision(e);
	}
	@Override
	public void setBounding() {
	    if (center == null || size == null) {
	        return;
	    }

	    bounding = new Bounding();

	    /*
	     * Très important :
	     * quand l'ennemi est transporté par un IceBlock,
	     * il ne doit plus bloquer physiquement le moteur.
	     * Il reste visible, mais il n'a plus de hitbox.
	     */
	    if (dead || dying || draggedByIce || crushedByIce) {
	        return;
	    }

	    double radius = Math.min(size.x(), size.y()) * 0.35;
	    bounding.add(new Circle(center, radius));
	}
	public boolean canRunBot() {
	    return !dead
	        && !dying
	        && !frozen
	        && !draggedByIce
	        && !crushedByIce;
	}
}