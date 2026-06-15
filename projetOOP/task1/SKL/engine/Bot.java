package engine;

public abstract class Bot {

    protected Entity entity;

    public Bot(Entity entity) {
        this.entity = entity;
    }

    public Entity entity() {
        return entity;
    }

    public Stunt stunt() {
        return entity.stunt();
    }

    public abstract void tick(double elapsed);
    public abstract void collision(Entity impactor, double elapsed);
}