package pengo.model;

import collision.Bounding;
import collision.Circle;
import geometry.Grid;
import model.Entity;

public class PengoPlayer extends Entity {

	private int lives;
	private int score;

	private boolean speedBoost;
	private long speedBoostRemaining;
	private boolean movingOneCell;
	private Grid.Position targetCell;
	private Grid.Position originCell;
	private FishBonus pendingFishBonus;
	private boolean iceSlow;
	private long iceSlowRemaining;

	private long walkAnimationClock = 0;

	public PengoPlayer() {
		super("PengoPlayer");
		this.lives = 3;
		this.score = 0;
		this.speedBoost = false;
		this.speedBoostRemaining = 0;
		this.movingOneCell = false;
		this.targetCell = null;
		this.pendingFishBonus = null;
		this.iceSlow = false;
		this.iceSlowRemaining = 0;

	}

	@Override
	public double speedMultiplier() {
		double m = 1.0;

		if (speedBoost) {
			m *= 2.0;
		}

		if (iceSlow) {
			m *= 0.45; // ralentissement
		}

		return m;
	}

	public long walkAnimationClock() {
		return walkAnimationClock;
	}

	public void attack() {
		if (model instanceof PengoModel) {
			((PengoModel) model).damageBlockInFront(this);
		}
	}

	public boolean movingOneCell() {
		return movingOneCell;
	}

	public void cancelGridMove() {
		movingOneCell = false;
		originCell = null;
		targetCell = null;
		pendingFishBonus = null;
		stop();

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

		System.out.println("SPEED BOOST ACTIVATED");
	}

	@Override
	public void tick(long elapsed) {
		super.tick(elapsed);

		if (movingOneCell) {
			walkAnimationClock += elapsed;
		} else {
			walkAnimationClock = 0;
		}

		if (movingOneCell && targetCell != null && position() != null) {
			if (position().x() == targetCell.x() && position().y() == targetCell.y()) {

				finishGridMoveOnTarget();
				return;
			}
		}

		if (speedBoost) {
			speedBoostRemaining -= elapsed;

			if (speedBoostRemaining <= 0) {
				speedBoost = false;
				speedBoostRemaining = 0;

			}
		}
		if (iceSlow) {
			iceSlowRemaining -= elapsed;

			if (iceSlowRemaining <= 0) {
				iceSlow = false;
				iceSlowRemaining = 0;
			}
		}
	}
	public void slowAfterIcePassage(long duration) {
		iceSlow = true;
		iceSlowRemaining = duration;
	}

	@Override
	public void setBounding() {
		if (center == null || size == null) {
			return;
		}

		bounding = new Bounding();

		double radius = Math.min(size.x(), size.y()) * 0.5;

		bounding.add(new Circle(center, radius));
	}

	@Override
	public void collision(Entity e) {
		if (e == null) {
			return;
		}

		if (model instanceof PengoModel pm) {
			if (pm.lost() || pm.won()) {
				cancelGridMove();
				return;
			}
		}

		if (e instanceof Wall && model instanceof PengoModel) {
			((PengoModel) model).startWallVibration(e, 1500);
			cancelGridMoveAndSnapBack();
			return;
		}

		if (e instanceof Enemy && model instanceof PengoModel pm) {
			Enemy enemy = (Enemy) e;

			if (enemy.passedOut()) {
				pm.killEnemy(enemy);
				finishGridMoveOnTarget();
				return;
			}

			if (enemy.harmlessForPlayer()) {
				cancelGridMoveAndSnapBack();
				return;
			}
			((PengoModel) model).loseLife();
			cancelGridMoveAndSnapBack();
			return;
		}

		if (e instanceof FishBonus) {
			pendingFishBonus = (FishBonus) e;
			finishGridMoveOnTarget();
			return;
		}

		if (e instanceof IceBlock) {
		    IceBlock block = (IceBlock) e;

		    // HP=1 ou HP=0 : on ignore la collision
		    if (block.passableByPlayer() || block.hp() <= 0 || block.broken()) {
		        return;
		    }

		    // HP=2 ou HP=3 : Pengo bloque
		    stopCleanlyOnCurrentCell();
		    return;
		}
		super.collision(e);
	}

	public void startGridMove(int direction, double speed) {
		if (movingOneCell) {
			return;
		}

		if (model instanceof PengoModel pm) {
			if (pm.lost() || pm.won()) {
				cancelGridMove();
				return;
			}
		}

		if (position() == null || isu == null) {
			return;
		}

		turnTo(direction);

		int x = position().x();
		int y = position().y();

		switch (direction) {

		case 0:
			x++;
			break;

		case 90:
			y++;
			break;

		case 180:
			x--;
			break;

		case 270:
			y--;
			break;

		default:
			return;
		}

		if (!(model instanceof PengoModel)) {
			return;
		}

		PengoModel pm = (PengoModel) model;
		Grid.Position nextCell = pm.grid().new Position(x, y);
		Entity front = pm.firstAt(nextCell);

		// WALL devant ==Pengo reste sur sa case.

		if (front instanceof Wall) {
			pm.startWallVibration(front, 1500);
			cancelGridMove();
			return;
		}
		if (front instanceof IceBlock) {
			IceBlock block = (IceBlock) front;

			// HP = 1 : Pengo traverse + ralentissement
			if (block.passableByPlayer()) {
				slowAfterIcePassage(3000);
			}

			// HP = 2 : bloque, pas poussable
			else if (block.cracked()) {
				cancelGridMove();
				return;
			}

			// HP = 3 : poussable
			else {
				if (block.sliding()) {
					cancelGridMove();
					return;
				}

				block.startSlide(direction);
				cancelGridMove();
				return;
			}
		}

		// ENEMY devant ==Pengo perd une vie sauf si l'ennemi est harmless
		if (front instanceof Enemy) {
			Enemy enemy = (Enemy) front;

			if (enemy.passedOut()) {
				pm.killEnemy(enemy);

				originCell = pm.grid().new Position(position().x(), position().y());
				targetCell = nextCell;
				movingOneCell = true;

				if (direction == 0) {
					setLinearSpeed(isu.new Vector(speed, 0));
				} else if (direction == 90) {
					setLinearSpeed(isu.new Vector(0, speed));
				} else if (direction == 180) {
					setLinearSpeed(isu.new Vector(-speed, 0));
				} else if (direction == 270) {
					setLinearSpeed(isu.new Vector(0, -speed));
				}

				return;
			}

			if (!enemy.harmlessForPlayer()) {
				pm.loseLife();
				cancelGridMove();
				return;
			}
		}
		// FISH BONUS devant == on le mémorise, il sera consommé quand Pengo arrive
		// dessus.

		pendingFishBonus = null;

		if (front instanceof FishBonus) {
			pendingFishBonus = (FishBonus) front;
		}

		// case libre ou bonus == Pengo avance d'une case.

		originCell = pm.grid().new Position(position().x(), position().y());
		targetCell = nextCell;
		movingOneCell = true;

		if (direction == 0) {
			setLinearSpeed(isu.new Vector(speed, 0));
		} else if (direction == 90) {
			setLinearSpeed(isu.new Vector(0, speed));
		} else if (direction == 180) {
			setLinearSpeed(isu.new Vector(-speed, 0));
		} else if (direction == 270) {
			setLinearSpeed(isu.new Vector(0, -speed));
		}
	}

	public void cancelGridMoveAndSnapBack() {

		if (originCell != null) {
			setPosition(originCell);
			setBounding();
		}

		movingOneCell = false;
		originCell = null;
		targetCell = null;
		pendingFishBonus = null;
		stop();
	}

	private void finishGridMoveOnTarget() {
		if (targetCell != null) {
			setPosition(targetCell);
			setBounding();
		}

		// if la case cible has un FishBonus on le consomme seulement quand Pengo arrive
		// vraiment dessus.

		if (pendingFishBonus != null && !pendingFishBonus.consumed()) {
			pendingFishBonus.consume(this);
		}

		movingOneCell = false;
		originCell = null;
		targetCell = null;
		pendingFishBonus = null;

		stop();
	}

	private void stopCleanlyOnCurrentCell() {
		if (model instanceof PengoModel && position() != null) {
			PengoModel pm = (PengoModel) model;

			Grid.Position current = pm.grid().new Position(position().x(), position().y());

			setPosition(current);
			setBounding();
		}
		movingOneCell = false;
		originCell = null;
		targetCell = null;
		pendingFishBonus = null;

		stop();
	}

}
