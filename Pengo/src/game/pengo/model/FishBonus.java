package game.pengo.model;

import engine.model.Entity;

public class FishBonus extends Entity {

    private boolean consumed;

    public FishBonus() {
        super("FishBonus");
        consumed = false;
    }

    public boolean consumed() {
        return consumed;
    }

    public void consume(PengoPlayer player) {

        if (consumed)
            return;

        consumed = true;

        player.activateSpeedBoost(8000);

        if (model != null)
            model.remove(this);
    }

}