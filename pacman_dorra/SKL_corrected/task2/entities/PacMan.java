package entities;

import engine.Entity;
import engine.Grid;
import intersection.Bounding;
import intersection.Circle;
import view.PacManAvatar;

public class PacMan extends Entity {

    private boolean dying;
    private long deathStartMs;
    private long movingUntilMs;

    public PacMan(Grid.Position position) {
        super("PacMan");
        setPosition(position);
        double cell = this.step.x();
        setSize(isu.new Dimension(cell / 2, cell / 2));
        setBounding();
        setAvatar(new PacManAvatar(this));
        this.dying = false;
        this.deathStartMs = 0L;
        this.movingUntilMs = 0L;
    }

    public void markMoving() {
        movingUntilMs = System.currentTimeMillis() + 260L;
    }

    public boolean isMovingForAnimation() {
        return !dying && System.currentTimeMillis() < movingUntilMs;
    }

    public boolean isDying() {
        return dying;
    }

    public void revive() {
        dying = false;
        deathStartMs = 0L;
        movingUntilMs = 0L;
        stop();
        setBounding();
    }

    public long deathStartMs() {
        return deathStartMs;
    }

    public void die() {
        if (!dying) {
            dying = true;
            deathStartMs = System.currentTimeMillis();
            stop();
        }
    }

    @Override
    public void setBounding() {
        bounding = new Bounding();
        double radius = size.x() / 2.0;
        bounding.add(new Circle(center.mkCopy(), radius));
    }

    @Override
    public void collision(Entity e) {
        if (dying) {
            return;
        }

        if (e instanceof Gum) {
            model.remove(e);
            model.addScore(10);
            System.out.println("Score = " + model.score());
            return;
        }

        if (e instanceof Ghost) {
            System.out.println("COLLISION DANGEREUSE : PacMan touche Ghost");
            model.pacmanCaughtByGhost();
            return;
        }

        if (e instanceof Obstacle || e instanceof Boss) {
            System.out.println("PacMan bloque contre " + e.getClass().getSimpleName());
            stop();
            return;
        }

        super.collision(e);
    }
}
