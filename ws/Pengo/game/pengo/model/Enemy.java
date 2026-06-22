package pengo.model;

import collision.Bounding;
import collision.Circle;
import model.Entity;

public class Enemy extends Entity {

	// États possibles de l'ennemi
	private boolean frozen;
	private boolean dying;
	private boolean dead;

	// Timers pour les animations et effets temporaires
	private long frozenRemaining;
	private long dyingRemaining;
	private long spawnAnimationRemaining;

	// États liés aux interactions avec les blocs de glace
	private boolean draggedByIce;
	private boolean crushedByIce;

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

	// Indique si l'ennemi ne doit plus blesser le joueur
	public boolean harmlessForPlayer() {
		return dead || dying || draggedByIce || crushedByIce;
	}

	// Ennemi considéré comme écrasé par un bloc de glace
	public void markCrushedByIce() {
		crushedByIce = true;
		draggedByIce = false;
		frozen = false;
		frozenRemaining = 0;
		stop();
	}

	public boolean draggedByIce() {
		return draggedByIce;
	}

	// L'ennemi est temporairement emporté par un bloc de glace
	public void startDraggedByIce() {
		if (dead || dying || crushedByIce) {
			return;
		}

	    draggedByIce = true;

	    frozen = false;
	    frozenRemaining = 0;

	    stop();
	    setBot(null);

	    //recalcule la hitbox :Comme draggedByIce = true, la hitbox devient vide.
	 
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

	// Gèle l'ennemi pendant une durée donnée
	public void freeze(long duration_ms) {
		if (duration_ms < 0 || dead) {
			return;
		}

		frozen = true;
		frozenRemaining = duration_ms;
		stop();
	}

	public void unfreeze() {
		if (dead) {
			return;
		}

		frozen = false;
		frozenRemaining = 0;
	}

	public boolean moving() {
		return linearSpeed() != null && linearSpeed().norm() > 0;
	}

	// Lance l'animation de mort avant de supprimer l'ennemi
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
	}

	@Override
	public void tick(long elapsed) {
		if (elapsed < 0) {
			return;
		}

		// Animation d'apparition au début
		if (spawnAnimationRemaining > 0) {
			spawnAnimationRemaining -= elapsed;

			if (spawnAnimationRemaining < 0) {
				spawnAnimationRemaining = 0;
			}
		}

		// Animation de mort avant suppression du modèle
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

		// Tant que l'ennemi est gelé, il ne se déplace pas
		if (frozen) {
			frozenRemaining -= elapsed;

			if (frozenRemaining <= 0) {
				unfreeze();
			}

			return;
		}

		// Si l'ennemi est emporté par un bloc, il ne décide plus seul
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

		// Collision circulaire pour simplifier les contacts avec l'ennemi
		bounding = new Bounding();

		// quand l'ennemi est transporté par un IceBlock,
		// il ne doit plus bloquer physiquement le moteur.

		if (dead || dying || draggedByIce || crushedByIce) {
			return;
		}

		double radius = Math.min(size.x(), size.y()) * 0.35;
		bounding.add(new Circle(center, radius));
	}

	public boolean canRunBot() {
		return !dead && !dying && !frozen && !draggedByIce && !crushedByIce;
	}
}