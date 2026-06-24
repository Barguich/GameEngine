package pengo.model;

import collision.Bounding;
import collision.Circle;
import model.Entity;

public class PengoPlayer extends Entity {

	private int lives;
	private int score;

	private boolean speedBoost;
	private long speedBoostRemaining;

	private long walkAnimationClock;

	public PengoPlayer() {
		super("PengoPlayer");
		this.lives = 3;
		this.score = 0;
		this.speedBoost = false;
		this.speedBoostRemaining = 0;
		this.walkAnimationClock = 0;
	}

	@Override
	public double speedMultiplier() {
		return speedBoost ? 2.0 : 1.0;
	}

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

	public void activateSpeedBoost(long duration_ms) {
		assert duration_ms >= 0;
		speedBoost = true;
		speedBoostRemaining = duration_ms;
	}

	@Override
	public void tick(long elapsed) {
		super.tick(elapsed);

		if (linearSpeed() != null && linearSpeed().norm() > 0) {
			walkAnimationClock += elapsed;
		} else {
			walkAnimationClock = 0;
		}

		if (speedBoost) {
			speedBoostRemaining -= elapsed;
			if (speedBoostRemaining <= 0) {
				speedBoost = false;
				speedBoostRemaining = 0;
			}
		}
	}

	@Override
	public void setBounding() {
		if (center == null || size == null) {
			return;
		}
		bounding = new Bounding();
		double radius = Math.min(size.x(), size.y()) * 0.35;
		bounding.add(new Circle(center, radius));
	}
}
