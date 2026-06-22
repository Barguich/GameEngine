package pengo.model;

import collision.Bounding;
import collision.Circle;
import model.Entity;

public class FishBonus extends Entity {

    private boolean consumed;

    public FishBonus() {
        super("FishBonus");
        consumed = false;
    }

    public boolean consumed() {
        return consumed;
    }

    @Override
    public void setBounding() {
        if (center == null || size == null) {
            return;
        }

        bounding = new Bounding();

        double radius = Math.min(size.x(), size.y()) / 3.0;
        bounding.add(new Circle(center, radius));
    }

    public void consume(PengoPlayer player) {
        if (consumed) {
            return;
        }

        if (player == null) {
            return;
        }

        consumed = true;

        System.out.println("FISH BONUS CONSUMED");

        /*
         * Speed boost pendant 8 secondes.
         */
        player.activateSpeedBoost(8000);

        if (model != null) {
            model.remove(this);
        }
    }
}