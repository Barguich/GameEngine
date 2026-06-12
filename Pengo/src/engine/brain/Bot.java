package engine.brain;

import engine.model.BasicStunt;
import engine.model.Entity;

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