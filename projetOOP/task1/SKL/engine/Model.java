package engine;

import java.util.ArrayList;
import java.util.List;

public class Model {
    // FIELDS
    private Grid grid;
    private List<Entity> entities;


    // CONSTRUCTOR
    public Model() {
        this.grid = Game.game().grid;
        this.entities = new ArrayList<Entity>();
    }

    // ADD, REMOVE Entity
    public void add(Entity e) {
        e.deploy();
        entities.add(e);
    }

    public void remove(Entity e) {
        e.retract();
        entities.remove(e);
    }

    public void move(Entity e, int degree) {
        if(e.stunt() instanceof BasicStunt stunt) {
            stunt.walk(degree);
        }
    }

    public void tick(double elapsed) {
        for(Entity e : entities) {
            if(e.bot() != null) {
                e.bot().tick(elapsed);
            }
            ISU.Vector motion = e.linearSpeed().mkCopy();
            motion.scale(elapsed / 1000.0);
            e.translate(motion);
            e.turn((int)(e.angularSpeed() * elapsed));
        }
    }

    public List<Entity> entities(){
        return entities;
    }

}