package game.pengo.model;

import java.util.ArrayList;
import java.util.List;

import engine.geometry.Grid;
import engine.model.Entity;
import engine.model.Model;

public class PengoModel extends Model {

	private PengoPlayer player;
	private int score;
	private boolean won;
	private boolean lost;

	private boolean doubleScore;
	private long doubleScoreRemaining;

	public PengoModel(Grid grid) {
		super(grid);

		this.player = null;
		this.score = 0;
		this.won = false;
		this.lost = false;

		this.doubleScore = false;
		this.doubleScoreRemaining = 0;
	}

	public void setPlayer(PengoPlayer player) {
		assert player != null;
		this.player = player;

		if (!entities().contains(player)) {
			add(player);
		}
	}

	public PengoPlayer player() {
		return player;
	}

	public int score() {
		return score;
	}

	public void addScore(int points) {
		assert points >= 0;

		if (doubleScore) {
			score += points * 2;
		} else {
			score += points;
		}
	}

	public void activateDoubleScore(long duration) {
		assert duration >= 0;

		doubleScore = true;
		doubleScoreRemaining = duration;
	}

	public boolean doubleScore() {
		return doubleScore;
	}

	public void freezeEnemies(long duration) {
		assert duration >= 0;

		for (Entity e : entities()) {
			if (e instanceof Enemy) {
				((Enemy) e).freeze(duration);
			}
		}
	}

	@Override
	public void tick(long elapsed) {
		assert elapsed >= 0;

		super.tick(elapsed);

		if (doubleScore) {
			doubleScoreRemaining -= elapsed;

			if (doubleScoreRemaining <= 0) {
				doubleScore = false;
				doubleScoreRemaining = 0;
			}
		}

		checkVictory();

		if (player != null && player.dead()) {
			lost = true;
		}
	}

	public void checkVictory() {
		if (allEnemiesDead()) {
			won = true;
			return;
		}

		if (diamondBlocksAligned()) {
			won = true;
		}
	}

	private boolean allEnemiesDead() {
		for (Entity e : entities()) {
			if (e instanceof Enemy) {
				return false;
			}
		}

		return true;
	}

	private boolean diamondBlocksAligned() {
		List<DiamondBlock> diamonds = new ArrayList<DiamondBlock>();

		for (Entity e : entities()) {
			if (e instanceof DiamondBlock) {
				diamonds.add((DiamondBlock) e);
			}
		}

		if (diamonds.size() < 3) {
			return false;
		}

		Grid.Position p0 = diamonds.get(0).position();
		Grid.Position p1 = diamonds.get(1).position();
		Grid.Position p2 = diamonds.get(2).position();

		boolean sameX = p0.x() == p1.x() && p1.x() == p2.x();

		boolean sameY = p0.y() == p1.y() && p1.y() == p2.y();

		return sameX || sameY;
	}

	public void loseLife() {
		if (player == null) {
			return;
		}

		player.loseLife();

		if (player.dead()) {
			lost = true;
		}
	}

	public boolean won() {
		return won;
	}

	public boolean lost() {
		return lost;
	}
}