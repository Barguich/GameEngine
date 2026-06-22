package pengo.model;

import collision.Bounding;
import collision.Rect;
import geometry.Grid;
import model.Entity;

public class IceBlock extends Entity {

	// État du bloc de glace
	private boolean sliding;
	private int direction;
	private boolean broken;

	// Points de vie du bloc : il se casse après 3 coups
	private int hp;

	// Ralentissement progressif pendant la glissade
	private double friction;

	// Animation de fissure / destruction
	private boolean breakingAnimation;
	private long breakingAnimationRemaining;
	private int breakingFrame;

	// Ennemi éventuellement emporté par le bloc en glissade
	private Enemy draggedEnemy;

	public IceBlock() {
		super("IceBlock");

		sliding = false;
		direction = 0;
		broken = false;
		hp = 3;
		friction = 0.999;

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

	// Attache un ennemi au bloc lorsqu'il est emporté pendant la glissade
	public void attachEnemy(Enemy enemy) {
		if (enemy == null) {
			return;
		}

		if (enemy.harmlessForPlayer()) {
			return;
		}

		draggedEnemy = enemy;
		enemy.startDraggedByIce();
	}

	// Détache l'ennemi transporté par le bloc
	public void detachEnemy() {
		if (draggedEnemy != null) {
			draggedEnemy.stopDraggedByIce();
		}

		draggedEnemy = null;
	}

	// Arrête complètement la glissade du bloc
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

	public boolean cracked() {
		return hp == 2;
	}

	public boolean veryCracked() {
		return hp == 1;
	}

	public boolean breakingAnimation() {
		return breakingAnimation;
	}

	public int breakingFrame() {
		return breakingFrame;
	}

	// Abîme le bloc et déclenche une courte animation de fissure
	public void damage() {
		if (broken || breakingAnimation) {
			return;
		}

		hp--;

		breakingAnimation = true;
		breakingAnimationRemaining = 300;
		breakingFrame = 0;
	}

	// Lance la glissade du bloc dans une direction donnée
	public void startSlide(int direction) {
		if (sliding || broken) {
			return;
		}

		this.direction = direction;
		this.sliding = true;

		// Sécurité : on repart sans ancien ennemi attaché
		detachEnemy();

		double speed = 12.0;

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
	}

	// Détruit le bloc et le retire du modèle
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

		// Le joueur peut pousser le bloc : cette interaction est gérée ailleurs
		if (e instanceof PengoPlayer) {
			return;
		}

		// La collision bloc glissant / ennemi est traitée dans PengoModel
		if (sliding && e instanceof Enemy) {
			return;
		}

		super.collision(e);
	}

	@Override
	public void tick(long elapsed) {

		// Mise à jour de l'animation de fissure
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

		// Si le bloc ne glisse pas, comportement normal d'une entité
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

		// Déplacement = vitesse * temps
		geometry.ISU.Vector movement =
				center.isu().new Vector(
						linearSpeed().x() * dt,
						linearSpeed().y() * dt);

		boolean moved;

		// Cas spécifique Pengo : gestion spéciale des blocs glissants
		if (model instanceof PengoModel) {
			moved = ((PengoModel) model).moveSlidingIceBlock(this, movement);
		} else {
			moved = model.move(this, movement);
		}

		if (!moved) {
			return;
		}

		// Application d'un léger frottement pour ralentir progressivement le bloc
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

		// Collision rectangulaire pour un bloc de glace
		bounding = new Bounding();
		bounding.add(new Rect(center, size, orientation_degree));
	}

	// Finalise l'écrasement d'un ennemi par le bloc
	public void finishCrushAt(Grid.Position position) {

		sliding = false;

		// L'ennemi transporté est détaché après l'écrasement
		detachEnemy();

		stop();

		// Repositionne le bloc exactement sur la case finale
		if (position != null) {
			setPosition(position);
			setBounding();
		}

		// Le bloc redevient disponible après l'action
		direction = 0;
	}
}