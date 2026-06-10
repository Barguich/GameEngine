package behavior;

import engine.Entity;
import game.Model;

public abstract class Stunt {

    protected Model model;
    protected Entity entity;

    public Stunt(Model model, Entity entity) {
        this.model = model;
        this.entity = entity;
    }

    public void collision(Entity e) {
    }

    public void done() {
    }

}