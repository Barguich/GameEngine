package behavior;

import engine.Entity;

public abstract class Bot {

    protected BasicStunt stunt;

    public Bot(BasicStunt stunt) {
        this.stunt = stunt;
    }

    public abstract void think();

    public void collision(Entity e) {
    }

    public void done() {
    }
}