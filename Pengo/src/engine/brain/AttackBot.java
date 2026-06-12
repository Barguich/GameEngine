package engine.brain;

import engine.model.BasicStunt;
import engine.model.Entity;

public class AttackBot extends Bot {
	private boolean moving;
    private Entity target;

    public AttackBot(BasicStunt stunt, Entity target) {
        super(stunt);
        this.target = target;
        this.moving = false;
    }

    @Override
    public void think() {

        int ex = stunt.entity().position().x();
        int ey = stunt.entity().position().y();

        int tx = target.position().x();
        int ty = target.position().y();
        moving = true;

        if (tx > ex)
            stunt.walk(0);

        else if (tx < ex)
            stunt.walk(180);

        else if (ty > ey)
            stunt.walk(270);

        else if (ty < ey)
            stunt.walk(90);
    }
    @Override 
    public void done() {
    	moving = false;
    }
    @Override 
    public void collision(Entity e) {
    	moving = false;
    }
    
}
