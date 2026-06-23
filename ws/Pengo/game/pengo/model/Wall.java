package pengo.model;

import collision.Bounding;
import collision.Rect;
import model.Entity;

public class Wall extends Entity {

	public Wall() {
		super("Wall");
	}

	@Override
	public void setBounding() {
		if (center == null || size == null) {
			return;
		}

		bounding = new Bounding();
		bounding.add(new Rect(center, size, orientation_degree));
	}
}