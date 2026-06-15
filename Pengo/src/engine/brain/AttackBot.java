package engine.brain;

import engine.model.BasicStunt;
import engine.model.Entity;

public class AttackBot extends Bot {

    private boolean moving;
    private Entity target;

    public AttackBot(BasicStunt stunt, Entity target) {
        super(stunt);

        assert target != null;

        this.target = target;
        this.moving = false;
    }

    @Override
    public void think() {
        if (moving) {
            return;
        }

        Entity me = stunt.entity();

        double ex = me.center().x();
        double ey = me.center().y();

        double tx = target.center().x();
        double ty = target.center().y();

        moving = true;

        if (tx > ex) {
            stunt.walk(0);       // droite
        }
        else if (tx < ex) {
            stunt.walk(180);     // gauche
        }
        else if (ty > ey) {
            stunt.walk(270);     // bas, car y augmente vers le bas
        }
        else if (ty < ey) {
            stunt.walk(90);      // haut
        }
        else {
            moving = false;
        }
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