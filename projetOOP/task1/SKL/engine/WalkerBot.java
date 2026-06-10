package engine;

public class WalkerBot extends Bot {
    public WalkerBot(Entity e) {
        super(e);
    }

    @Override
    public void tick(double elapsed) {
        BasicStunt stunt = (BasicStunt) entity.stunt();
        stunt.walk(0);
    }

    @Override
    public void collision(Entity impactor, double elapsed) {
    }
}
