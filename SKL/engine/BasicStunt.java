package engine;

import java.util.List;

public class BasicStunt extends Stunt {

    private Grid.Position targetPos;
    private ISU.Coord targetCoord;
    private boolean walking = false;
    protected Bot bot;

    public BasicStunt(Model model, Entity entity) {
        super(model, entity);
    }

    @Override
    public void set(double x, double y) {
        entity.retract();
        entity.setCoord(Game.isu().new Coord(x, y));
        entity.deploy();

        if (walking) {
            // vérifier si on a atteint ou dépassé la cible
            double dist = entity.center().distanceTo(targetCoord);
            // vérifier aussi si on a changé de cellule
            Grid.Position current = entity.position();
            if (dist < 1.0 ||
                    (current.x() == targetPos.x() && current.y() == targetPos.y())) {
                walking = false;
                entity.setlSpeed(null);
                // se repositionner exactement au centre de la cellule cible
                entity.retract();
                entity.setPosition(targetPos);
                entity.deploy();
                done();
            }
        }
    }

    @Override
    public void collision(Entity e) {
        assert e != null;
        walking = false;
        entity.setlSpeed(null);
        if (bot != null)
            bot.collision(e);
    }

    @Override
    public void collision(List<Entity> entities) {
        assert entities != null;
        walking = false;
        entity.setlSpeed(null);
        if (bot != null)
            bot.collision(entities);
    }

    public void walk(int degree) {
        // NE PAS bloquer si déjà walking — permet de changer de direction
        int cardinal = snapToCardinal(degree);

        // calculer la cellule cible
        Grid.Position pos = entity.position();
        int dx = 0, dy = 0;
        switch (cardinal) {
            case 0   -> dx = 1;
            case 90  -> dy = 1;
            case 180 -> dx = -1;
            case 270 -> dy = -1;
        }
        Grid.Position newTarget = entity.grid.new Position(pos.x() + dx, pos.y() + dy);

        // vérifier que la cellule cible n'est pas un mur
        for (Entity e : model.entities()) {
            if (e instanceof game.Obstacle) {
                if (e.position().x() == newTarget.x()
                        && e.position().y() == newTarget.y()) {
                    return; // mur — ne pas avancer
                }
            }
        }

        entity.setOrientation(cardinal);
        targetPos = newTarget;
        targetCoord = targetPos.toISUCoordCentered();

        double speed = 12.0;
        ISU.Vector v = switch (cardinal) {
            case 0   -> Game.isu().new Vector(speed, 0);
            case 90  -> Game.isu().new Vector(0, speed);
            case 180 -> Game.isu().new Vector(-speed, 0);
            case 270 -> Game.isu().new Vector(0, -speed);
            default  -> Game.isu().new Vector(0, 0);
        };

        entity.setlSpeed(v);
        walking = true;
    }

    public void setBot(Bot bot) {
        assert bot != null;
        this.bot = bot;
    }

    public void done() {
        if (bot != null)
            bot.done();
    }

    public int snapToCardinal(int degree) {
        degree = Math.floorMod(degree, 360);
        if (degree <= 45 || degree > 315) return 0;
        if (degree <= 135) return 90;
        if (degree <= 225) return 180;
        return 270;
    }
}