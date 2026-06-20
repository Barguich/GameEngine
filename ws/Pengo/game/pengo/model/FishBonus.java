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

		consumed = true;

		player.activateSpeedBoost(10000);


        if (player != null) {
            player.activateSpeedBoost(8000); // 8 secondes
        }

        if (model != null) {
            model.remove(this);
        }
    }

}