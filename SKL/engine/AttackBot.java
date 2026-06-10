package engine;

import java.util.List;

public class AttackBot extends Bot{
    private Entity target;
    private long startDelay = 3000; // 3 secondes avant de bouger
    private long elapsed = 0;
    public AttackBot(Brain brain, BasicStunt stunt,Entity target) {
        super(brain, stunt);
        assert target!=null;
        this.target=target;
    }
    public void setStartDelay(long delay) {
        this.startDelay = delay;
    }
    @Override
    public void think() {
        elapsed += 33;
        if (elapsed < startDelay) return;
        if(target==null)return;
        Entity self = stunt.entity;
        if (self.position() == null || target.position() == null) return;

        ISU.Coord selfCenter = self.center();
        ISU.Coord targetCenter = target.center();
        ISU.Vector v = selfCenter.mkVectorToward(targetCenter);

        double angle = Math.toDegrees(Math.atan2(v.y(), v.x()));
        stunt.walk((int) Math.round(angle));

    }
    @Override
    public void done() {
        think();
    }

    @Override
    public void collision(Entity e) {
        stunt.entity.setlSpeed(null);
    }

    @Override
    public void collision(List<Entity> entities) {
        stunt.entity.setlSpeed(null);
    }

    public Entity target() {
        return target;
    }

    public void setTarget(Entity target) {
        this.target = target;
    }
}
