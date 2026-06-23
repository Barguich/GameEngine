package pengo.model;

import java.util.ArrayList;
import java.util.List;

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
	private List<Enemy> draggedEnemies; // liste des ennemies a emporter lors de sliding
	private boolean containsSnoBee;
	private long hatchRemaining;

	public IceBlock() {
		super("IceBlock");
		sliding = false;
		direction = 0;
		broken = false;
		hp = 3;// destruction totale sur 3 coups
		friction = 0.9992; // si on met a 1 plus de frottement
		breakingAnimation = false;
		breakingAnimationRemaining = 0;
		breakingFrame = 0;
		draggedEnemies = new ArrayList<Enemy>();

	}

	public IceBlock(boolean containsSnoBee, long hatchDelay) {
		this();
		this.containsSnoBee = containsSnoBee;
		this.hatchRemaining = hatchDelay;
	}

	public boolean containsSnoBee() {
		return containsSnoBee;
	}

	public List<Enemy> draggedEnemies() {
		return draggedEnemies;
	}

	public Enemy draggedEnemy() {
		if (draggedEnemies.isEmpty()) {
			return null;
		}
		// le premier de la liste est l'ennemi le plus devant.

		return draggedEnemies.get(0);
	}

	public boolean draggingEnemy() {
		return !draggedEnemies.isEmpty();
	}

	public boolean isDraggingEnemy(Enemy enemy) {
		return enemy != null && draggedEnemies.contains(enemy);
	}

	public void attachEnemy(Enemy enemy) {
		attachEnemyFront(enemy);
	}

	public void attachEnemyFront(Enemy enemy) {
		if (enemy == null) {
			return;
		}

		if (draggedEnemies.contains(enemy)) {
			return;
		}

		if (enemy.harmlessForPlayer() && !enemy.draggedByIce()) {
			return;
		}

		System.out.println("ICEBLOCK DRAG ENEMY");

		// ajoute devant la chaîne.

		draggedEnemies.add(0, enemy);

		enemy.startDraggedByIce();
		enemy.setBot(null);
		enemy.stop();
	}

	public void detachEnemy() {
		for (Enemy enemy : new ArrayList<Enemy>(draggedEnemies)) {
			if (enemy != null) {
				enemy.stopDraggedByIce();
			}
		}

		draggedEnemies.clear();
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

	public boolean destroyByEnemy() {
		if (broken) {
			return false;
		}
		if (breakingAnimation) {
			return true;
		}
		hp = 0;
		stopSlide();
		breakingAnimation = true;
		breakingAnimationRemaining = 300;
		breakingFrame = 0;
		System.out.println("ICEBLOCK DESTROYED BY ENEMY");
		return true;
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
		if (broken) {
			return;
		}

		// si le bloc est déjà en train de glisser==on ne relance PAS le slide.

		if (sliding) {
			System.out.println("ICE ALREADY SLIDING - PUSH IGNORED");
			return;
		}

		System.out.println("ICE START direction = " + direction);

		this.direction = direction;
		this.sliding = true;

		detachEnemy();

		double speed = 20.0;
		if (model instanceof PengoModel) {
			PengoConfig cfg = ((PengoModel) model).config();
			speed = cfg.iceSpeed();
			friction = cfg.iceFriction();
		}

		if (isu == null) {
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
		if (broken) {
			return;
		}

		broken = true;
		stopSlide();

		if (containsSnoBee && model instanceof PengoModel) {
			containsSnoBee = false;
			hatchRemaining = 0;

			((PengoModel) model).hatchSnoBee(this);
			return;
		}

		if (model != null) {
			model.remove(this);
		}
	}

	@Override
	public void collision(Entity e) {
		if (e == null) {
			return;
		}

		// un IceBlock qui glisse ne doit PAS être bloqué par un Enemy.
		// sinon il s'arrête au contact de l'ennemi

		if (sliding && e instanceof Enemy) {
			System.out.println("ICEBLOCK IGNORE ENEMY COLLISION WHILE SLIDING");
			return;
		}

		// joueur ne bloque pas directement le IceBlock.

		if (e instanceof PengoPlayer) {
			return;
		}

		super.collision(e);
	}

	@Override
	public void tick(long elapsed) {
		if (containsSnoBee && hatchRemaining > 0) {
			hatchRemaining -= elapsed;

			if (hatchRemaining <= 0 && model instanceof PengoModel) {
				containsSnoBee = false;
				((PengoModel) model).hatchSnoBee(this);
				return;
			}
		}
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
			// seuil plus bas = glissabde plus smooth
			if (linearSpeed().norm() < 0.25) {
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
		// Le bloc n'est plus en glissade.

		sliding = false;

		// On détache l'ennemi transporté.

		detachEnemy();
		// on coupe complètement la vitesse.
		stop();

		// On place le bloc exactement à la position finale.

		if (position != null) {
			setPosition(position);
			setBounding();
		}
		// securité : le bloc doit rester réutilisable après l'écrasement.

		direction = 0;
	}

	public boolean passableByPlayer() {
		return hp == 1 && !broken && !sliding && !breakingAnimation;
	}

}