package engine;

public class KeyboardBot extends Bot {

    private int direction = 0;

    public KeyboardBot(Entity e) {
        super(e);
    }

    public void direction(int dir) {
        this.direction = dir;
    }

    @Override
    public void tick(double elapsed) {
        ((BasicStunt)stunt()).walk(direction);
    }

    @Override
    public void collision(Entity impactor, double elapsed) {
    }
}