package pengo.model;

import collision.Bounding;
import collision.Circle;
import model.Entity;

public class FishBonus extends Entity {

	// Indique si le bonus a déjà été récupéré
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

		double radius = Math.min(size.x(), size.y()) * 0.48;
		bounding.add(new Circle(center, radius));
	}

	// Active le bonus lorsqu'il est récupéré par le joueur
	public void consume(PengoPlayer player) {

		// Évite de récupérer plusieurs fois le même bonus
		if (consumed) {
			return;
		}

		if (player == null) {
			return;
		}

		consumed = true;

		// Bonus de vitesse pendant 8 secondes

		long boost = (model instanceof PengoModel) ? ((PengoModel) model).config().fishBoostDuration() : 8000;
		player.activateSpeedBoost(boost);

		// Le bonus disparaît après utilisation
		if (model != null) {
			model.remove(this);
		}
	}
}