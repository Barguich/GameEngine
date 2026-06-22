package pengo.model;

import collision.Bounding;
import collision.Rect;
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
	private Enemy draggedEnemy;

	public IceBlock() {
		super("IceBlock");
		sliding = false;
		direction = 0;
		broken = false;
		hp = 3;// destruction totale sur 3 coups
		friction = 0.999; //si on met a 1 plus de frottement 
		breakingAnimation = false;
		breakingAnimationRemaining = 0;
		breakingFrame = 0;
	}
	public Enemy draggedEnemy() {
	    return draggedEnemy;
	}

	public boolean draggingEnemy() {
	    return draggedEnemy != null;
	}

	public void attachEnemy(Enemy enemy) {
	    if (enemy == null) {
	        return;
	    }

	    if (enemy.harmlessForPlayer()) {
	        return;
	    }

	    System.out.println("ICEBLOCK DRAG ENEMY");

	    draggedEnemy = enemy;
	    enemy.startDraggedByIce();
	}
	public void detachEnemy() {
	    if (draggedEnemy != null) {
	        draggedEnemy.stopDraggedByIce();
	    }

	    draggedEnemy = null;
	}

	public void stopSlide() {
	    sliding = false;
	    detachEnemy();
	    stop();
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
	    if (sliding) {
	        return;
	    }

	    if (broken) {
	        return;
	    }

	    System.out.println("ICE START direction = " + direction);

	    this.direction = direction;
	    this.sliding = true;

	    /*
	     * Sécurité : aucun ancien ennemi attaché.
	     */
	    detachEnemy();

	    double speed = 12.0;

	    if (isu == null) {
	        System.out.println("ICE ISU NULL");
	        sliding = false;
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
	    } else {
	        sliding = false;
	        stop();
	        return;
	    }

	    System.out.println("ICE SPEED = " + linearSpeed());
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
	        return;
	    }

	    /*
	     * Très important :
	     * La collision IceBlock / Enemy est gérée AVANT dans PengoModel.
	     * Ici, on ne fait rien, sinon le moteur déclenche une collision normale.
	     */
	    if (sliding && e instanceof Enemy) {
	        return;
	    }

	    super.collision(e);
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

	    geometry.ISU.Vector movement =
	        center.isu().new Vector(
	            linearSpeed().x() * dt,
	            linearSpeed().y() * dt
	        );

	    boolean moved;

	    if (model instanceof PengoModel) {
	        moved = ((PengoModel) model).moveSlidingIceBlock(this, movement);
	    } else {
	        moved = model.move(this, movement);
	    }

	    if (!moved) {
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
	public void finishCrushAt(Grid.Position position) {
	    /*
	     * Le bloc n'est plus en glissade.
	     */
	    sliding = false;

	    /*
	     * On détache l'ennemi transporté.
	     */
	    detachEnemy();

	    /*
	     * On coupe complètement la vitesse.
	     */
	    stop();

	    /*
	     * On place le bloc exactement à la position finale.
	     */
	    if (position != null) {
	        setPosition(position);
	        setBounding();
	    }

	    /*
	     * Sécurité : le bloc doit rester réutilisable après l'écrasement.
	     */
	    direction = 0;
	}
	
	
}