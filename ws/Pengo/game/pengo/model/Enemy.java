package pengo.model;

import collision.Bounding;
import model.Entity;

public class Enemy extends Entity {

	// États principaux de l'ennemi
	private boolean frozen;
	private boolean dying;
	private boolean dead;

	// Timers associés aux états temporaires
	private long frozenRemaining;
	private long dyingRemaining;
	private long spawnAnimationRemaining;

	// États liés aux interactions avec les blocs de glace
	private boolean draggedByIce;
	private boolean crushedByIce;
	private IceBlock breakingThroughBlock;

	// Animation et état "passed out" après vibration / choc
	private boolean passedOut;
	private long passedOutRemaining;

	private long frozenAnimationClock = 0;
	private long walkAnimationClock = 0;

	// Animation spécifique lorsque l'ennemi est écrasé par un bloc
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

	// Un ennemi neutralisé ne doit pas tuer Pengo.
	public boolean harmlessForPlayer() {
		return dead || dying || draggedByIce || crushedByIce || passedOut || frozen;
	}

	// Pengo peut manger un ennemi gelé ou assommé.
	public boolean eatableByPlayer() {
		return frozen || passedOut;
	}

	public long frozenAnimationClock() {
		return frozenAnimationClock;
	}

	public long walkAnimationClock() {
		return walkAnimationClock;
	}

	// L'ennemi est assommé pendant une durée donnée.
	public void passOut(long duration) {
		passedOut = true;
		passedOutRemaining = duration;
		stop();
	}

	public boolean passedOut() {
		return passedOut;
	}

	// Lance l'animation d'écrasement par un IceBlock.
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

		// La hitbox devient vide pendant l'animation.
		setBounding();
	}

	public boolean draggedByIce() {
		return draggedByIce;
	}

	// L'ennemi est transporté par un bloc de glace en glissade.
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

		// Comme draggedByIce devient true, la hitbox est vidée.
		setBounding();
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

	// Gèle l'ennemi et suspend son déplacement.
	public void freeze(long duration_ms) {
		if (duration_ms < 0 || dead) {
			return;
		}

		frozen = true;
		frozenRemaining = duration_ms;
		frozenAnimationClock = 0;

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

	// Mort classique de l'ennemi, avec un court délai d'animation.
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

		// Animation d'apparition : l'ennemi ne devient actif qu'après ce délai.
		if (spawnAnimationRemaining > 0) {
			spawnAnimationRemaining -= elapsed;

			if (spawnAnimationRemaining < 0) {
				spawnAnimationRemaining = 0;
			}
		}

		// Animation d'écrasement par un IceBlock.
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

		// Mort classique.
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

		// Tant qu'il est gelé, l'ennemi ne bouge pas.
		if (frozen) {
			frozenAnimationClock += elapsed;
			frozenRemaining -= elapsed;

			if (frozenRemaining <= 0) {
				unfreeze();
			}

			return;
		}

		// Un ennemi transporté est déplacé par IceBlock, pas par son bot.
		if (draggedByIce) {
			stop();
			return;
		}

		// État assommé temporaire.
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

		// Horloge utilisée pour choisir les frames de marche.
		if (moving()) {
			walkAnimationClock += elapsed;
		} else {
			walkAnimationClock = 0;
		}

		super.tick(elapsed);

		// Nettoyage d'une référence vers un bloc supprimé du modèle.
		if (breakingThroughBlock != null && breakingThroughBlock.model() == null) {
			breakingThroughBlock = null;
		}
	}

	@Override
	public boolean intersects(Entity entity) {
		if (dead || dying || draggedByIce || crushedByIce || passedOut) {
			return false;
		}

		// Cas particulier : l'ennemi traverse un bloc déjà détruit.
		if (entity == breakingThroughBlock && breakingThroughBlock != null && breakingThroughBlock.hp() <= 0) {
			return false;
		}

		return super.intersects(entity);
	}

	@Override
	public void collision(Entity e) {
		if (e == null) {
			return;
		}

		// Les états neutralisés ne déclenchent pas de collision normale.
		if (harmlessForPlayer()) {
			return;
		}

		// L'écrasement par un bloc glissant est géré dans PengoModel.
		if (e instanceof IceBlock) {
			IceBlock ice = (IceBlock) e;

			if (ice.sliding()) {
				return;
			}
		}

		// GoldBlock doit être testé avant IceBlock car il en hérite.
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

		// Les ennemis neutralisés ne doivent plus bloquer le jeu.
		if (dead || dying || draggedByIce || crushedByIce) {
			return;
		}

		// Hitbox rectangulaire légèrement réduite pour éviter les faux contacts
		// entre deux cases adjacentes.
		bounding.add(collision.Hitbox.shrunkRect(center, size, orientation_degree));
	}

	// Indique si l'IA peut contrôler cet ennemi.
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
		// Un ennemi actif occupe réellement sa case.
		if (!dead && !dying && !draggedByIce && !crushedByIce && !passedOut) {
			return false;
		}

		// Un ennemi neutralisé devient traversable pour éviter les blocages.
		return true;
	}
}