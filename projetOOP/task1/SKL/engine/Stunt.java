package engine;

import java.util.List;

public abstract class Stunt {

    protected Model model;
    protected Entity entity;

    public Stunt(Model model, Entity entity) {
        this.model = model;
        this.entity = entity;
    }

    public void set(int orientation){

    }

    public void set(Grid.Cell c){

    }

    public void set(double x,double y){

    }

    public void collision(Entity e){

    }

    public void collision(List<Entity> e){

    }
}
