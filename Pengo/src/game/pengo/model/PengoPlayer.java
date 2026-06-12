package game.pengo.model;

import engine.collision.Bounding;
import engine.collision.Circle;
import engine.model.Entity;

public class PengoPlayer extends Entity {

    private int lives;
    private int score;

    private boolean speedBoost;
    private long speedBoostRemaining;

    public PengoPlayer() {
        super("PengoPlayer");
        this.lives = 3;
        this.score = 0;
        this.speedBoost = false;
        this.speedBoostRemaining = 0;
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
    }

    @Override
    public void tick(long elapsed) {
        super.tick(elapsed);

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

        double radius = Math.min(size.x(), size.y()) / 2.0;
        bounding.add(new Circle(center, radius));
    }
}