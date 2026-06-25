package pengo.model;

import collision.Bounding;
import collision.Circle;
import collision.Rect;
import model.Entity;

public class Enemy extends Entity {

	private boolean frozen;
	private long frozenRemaining;
	private boolean dying;
	private long dyingRemaining;
	private long spawnAnimationRemaining;
	private boolean draggedByIce;
	private boolean crushedByIce;
	private IceBlock breakingThroughBlock;
	private boolean dead;
	private long frozenAnimationClock = 0;
	private long walkAnimationClock = 0;
	private boolean passedOut;
	private long passedOutRemaining;
	//champs pour lanimation de l'ecrasement de lenemie
	private boolean crushAnimation;
	private long crushAnimationRemaining;
	private int crushFrame;
	private int crushDirection;

	public Enemy() {
		super("Enemy");

		this.frozen = false;
		this.frozenRemaining = 0;
		this.dead = false;
		this.dying = false;
		this.dyingRemaining = 0;
		this.spawnAnimationRemaining = 800;
		this.breakingThroughBlock = null;
		this.draggedByIce = false;
		this.crushedByIce = false;
		this.passedOut = false;
		this.passedOutRemaining = 0;
		this.crushAnimation = false;
		this.crushAnimationRemaining = 0;
		this.crushFrame = 0;
		this.crushDirection = 0;
	}
	//getter d'animation
	public boolean crushAnimation() {
	    return crushAnimation;
	}
	public int crushDirection() {
		return crushDirection;
	}

	public int crushFrame() {
	    return crushFrame;
	}

	public long crushAnimationRemaining() {
	    return crushAnimationRemaining;
	}

	public boolean crushedByIce() {
		return crushedByIce;
	}

	public boolean harmlessForPlayer() {
		return dead || dying || draggedByIce || crushedByIce || passedOut || frozen;
	}

	public boolean eatableByPlayer() {
		return frozen || passedOut;
	}

	public long frozenAnimationClock() {
		return frozenAnimationClock;
	}

	public long walkAnimationClock() {
		return walkAnimationClock;
	}

	public void passOut(long duration) {
		passedOut = true;
		passedOutRemaining = duration;
		stop();
	}

	public boolean passedOut() {
		return passedOut;
	}

	public void markCrushedByIce(int direction) {
		if (crushedByIce || dead) {
			return;
		}

		crushedByIce = true;
		draggedByIce = false;
		frozen = false;
		frozenRemaining = 0;
		passedOut = false;
		passedOutRemaining = 0;

		crushDirection = direction;

		crushAnimation = true;
		crushAnimationRemaining = 350;
		crushFrame = 0;

		stop();
		setBot(null);
		setBounding();

		System.out.println("ENEMY CRUSH ANIMATION START direction = " + direction);
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
		passedOut = false;
		passedOutRemaining = 0;

		stop();
		setBot(null);

		// recalcule la hitbox :Comme draggedByIce = true, la hitbox devient vide.

		setBounding();

		System.out.println("ENEMY START DRAGGED BY ICE");
	}

	public void stopDraggedByIce() {
		draggedByIce = false;
		setBounding();
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

		frozen = true;
		frozenRemaining = duration_ms;
		frozenAnimationClock = 0;
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
		//animation de dying ennemy
		if (crushAnimation) {
		    crushAnimationRemaining -= elapsed;

		    if (crushAnimationRemaining > 240) {
		        crushFrame = 0;
		    } else if (crushAnimationRemaining > 120) {
		        crushFrame = 1;
		    } else {
		        crushFrame = 2;
		    }

		    if (crushAnimationRemaining <= 0) {
		        crushAnimation = false;
		        crushAnimationRemaining = 0;
		        crushFrame = 2;
		        dead = true;

		        if (model != null) {
		            model.remove(this);
		        }
		    }

		    return;
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
			frozenAnimationClock += elapsed;
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
		if (passedOut) {
			passedOutRemaining -= elapsed;

			if (passedOutRemaining <= 0) {
				passedOut = false;
				passedOutRemaining = 0;
			} else {
				stop();
				return;
			}
		}

		// Horloge d'animation de marche : accumule pendant un déplacement,
		// se remet à 0 dès que SnoBee est arrêté (mur, etc.) — pose stable.
		if (moving()) {
			walkAnimationClock += elapsed;
		} else {
			walkAnimationClock = 0;
		}

		super.tick(elapsed);
		if (breakingThroughBlock != null && breakingThroughBlock.model() == null) {
			breakingThroughBlock = null;
		}
	}

	@Override
	public boolean intersects(Entity entity) {
		if (dead || dying || draggedByIce || crushedByIce || passedOut) {
			return false;
		}
		//cas spécial : l'ennemi est en train de traverser un bloc détruit.
		if (entity == breakingThroughBlock
	            && breakingThroughBlock != null
	            && breakingThroughBlock.hp() <= 0) {
	        return false;
	    }

		return super.intersects(entity);
	}

	@Override
	public void collision(Entity e) {
		if (e == null) {
			return;
		}

		// si l'ennemi est transporté / tué / gelé il ne doit plus déclencher de
		// collision normale.

		if (harmlessForPlayer()) {
			return;
		}

		// si un IceBlock glissant touche l'ennemi,l'ennemi ne doit pas bloquer le
		// IceBlock.

		if (e instanceof IceBlock) {
			IceBlock ice = (IceBlock) e;

			if (ice.sliding()) {
				System.out.println("ENEMY IGNORE SLIDING ICEBLOCK COLLISION");
				return;
			}
		}

		// GoldBlock avant IceBlock, parce que GoldBlock extends IceBlock
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

		bounding = new collision.Bounding();

		if (dead || dying || draggedByIce || crushedByIce) {
			return;
		}

		double w = size.x();
		double h = size.y();

		bounding.add(collision.Hitbox.shrunkRect(center, size, orientation_degree));
	}

	public boolean canRunBot() {

		return !dead && !dying && !frozen && !spawning() && !draggedByIce && !crushedByIce;

	}

	public void beginBreakingThrough(IceBlock block) {
		breakingThroughBlock = block;
	}

	public void stopBreakingThrough() {
		breakingThroughBlock = null;
	}

	public boolean breakingThrough() {
		return breakingThroughBlock != null;
	}

	@Override
	public boolean canShareCellWith(Entity other) {
	    //un ennemi normal ne partage pas sa case.
	     
	    if (!dead
	            && !dying
	            && !draggedByIce
	            && !crushedByIce
	            && !passedOut) {
	        return false;
	    }

	    //ennemi neutralisé = ghost.
	    
	    return true;
	}

}
