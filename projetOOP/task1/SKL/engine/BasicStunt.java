package engine;

import java.util.List;

public class BasicStunt extends Stunt{

    public BasicStunt(Model model, Entity entity){
        super(model, entity);
    }

    @Override
    public void collision(Entity e){
        entity.setLinearSpeed(Game.game().isu.new Vector(0,0));
    }

    @Override
    public void collision(List<Entity> entities) {
        for(Entity e : entities) {
            collision(e);
        }
    }

    public void walk(int degree) {
        degree = normalizeDirection(degree);
        entity.turn(degree - entity.orientation());
        double speed = Game.game().cmPerCell;
        ISU.Vector v = entity.center().isu().new Vector(80, 0);
        v.turn(degree);
        System.out.println(
                "degree=" + degree +
                        " vx=" + v.x() +
                        " vy=" + v.y()
        );
        entity.setLinearSpeed(v);
    }

    private int normalizeDirection(int degree) {
        degree %= 360;
        if(degree < 0)
            degree += 360;
        if(degree >= 315 || degree < 45)
            return 0;
        if(degree < 135)
            return 90;
        if(degree < 225)
            return 180;
        return 270;
    }

}
