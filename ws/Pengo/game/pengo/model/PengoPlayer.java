package pengo.model;

import collision.Bounding;
import collision.Circle;
import model.Entity;

/**
 * Entité contrôlée par le joueur.
 *
 * Pengo possède ses propres états de gameplay : - nombre de vies ; - score ; -
 * bonus de vitesse ; - ralentissement après passage dans un bloc très abîmé ; -
 * horloge d'animation de marche.
 */
public class PengoPlayer extends Entity {

	private int lives;
	private int score;

	// Bonus temporaire donné par le FishBonus.
	private boolean speedBoost;
	private long speedBoostRemaining;

	// Ralentissement temporaire après avoir traversé un IceBlock hp == 1.
	private boolean iceSlow;
	private long iceSlowRemaining;

	// Bloc traversé récemment par Pengo.
	// On le garde pour pouvoir l'abîmer une fois que Pengo l'a dépassé.
	private IceBlock crossedIceBlock;

	// Horloge utilisée par l'avatar pour changer les frames de marche.
	private long walkAnimationClock;

	public PengoPlayer() {
		super("PengoPlayer");

		this.lives = 3;
		this.score = 0;

		this.speedBoost = false;
		this.speedBoostRemaining = 0;

		this.iceSlow = false;
		this.iceSlowRemaining = 0;
		this.crossedIceBlock = null;

		this.walkAnimationClock = 0;
	}

	@Override
	public double speedMultiplier() {
		double m = 1.0;

		if (speedBoost) {
			m *= 2.0;
		}

		if (iceSlow) {
			m *= 0.45;
		}

		return m;
	}

	// Attaque le bloc situé devant Pengo.
	public void attack() {
		if (model instanceof PengoModel) {
			((PengoModel) model).damageBlockInFront(this);
		}
	}

	public int lives() {
		return lives;
	}

	public int score() {
		return score;
	}

	public boolean speedBoosted() {
		return speedBoost;
	}

	public long walkAnimationClock() {
		return walkAnimationClock;
	}

	public void addScore(int points) {
		assert points >= 0;
		score += points;
	}

	public void loseLife() {
		lives--;

		if (lives < 0) {
			lives = 0;
		}
	}

	public boolean dead() {
		return lives == 0;
	}

	// Active le bonus de vitesse pendant une durée donnée.
	public void activateSpeedBoost(long duration_ms) {
		assert duration_ms >= 0;

		speedBoost = true;
		speedBoostRemaining = duration_ms;
	}

	@Override
	public void tick(long elapsed) {
		super.tick(elapsed);

		// Mise à jour de l'horloge de marche.
		// Elle avance uniquement lorsque Pengo se déplace.
		if (linearSpeed() != null && linearSpeed().norm() > 0) {
			walkAnimationClock += elapsed;
		} else {
			walkAnimationClock = 0;
		}

		// Fin automatique du bonus de vitesse.
		if (speedBoost) {
			speedBoostRemaining -= elapsed;

			if (speedBoostRemaining <= 0) {
				speedBoost = false;
				speedBoostRemaining = 0;
			}
		}

		// Fin automatique du ralentissement après traversée d'un bloc.
		if (iceSlow) {
			iceSlowRemaining -= elapsed;

			if (iceSlowRemaining <= 0) {
				iceSlow = false;
				iceSlowRemaining = 0;
			}
		}

		/*
		 * Lorsque Pengo a suffisamment dépassé le bloc traversable, on applique les
		 * dégâts au bloc.
		 *
		 * Cela évite de casser le bloc au moment exact où Pengo entre dedans :
		 * visuellement, on voit d'abord Pengo passer à travers.
		 */
		if (crossedIceBlock != null && crossedIceBlock.position() != null) {
			double cell = step().x();

			if (distanceCenterToCenter(crossedIceBlock) > cell * 0.6) {
				crossedIceBlock.damage();
				crossedIceBlock = null;
			}
		}
	}

	// Applique un ralentissement temporaire après passage dans un bloc abîmé.
	public void slowAfterIcePassage(long duration) {
		iceSlow = true;
		iceSlowRemaining = duration;
	}

	// Signale que Pengo vient de traverser un IceBlock.
	public void crossIceBlock(IceBlock block) {
		if (block == null) {
			return;
		}

		crossedIceBlock = block;
		slowAfterIcePassage(2000);
	}

	@Override
	public void setBounding() {
		if (center == null || size == null) {
			return;
		}

		bounding = new Bounding();

		/*
		 * Pengo utilise une hitbox circulaire plus petite que son sprite. Cela rend les
		 * déplacements plus souples entre les blocs.
		 */
		double radius = Math.min(size.x(), size.y()) * 0.35;

		bounding.add(new Circle(center, radius));
	}
}