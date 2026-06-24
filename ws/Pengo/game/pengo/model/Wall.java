package pengo.model;

import collision.Bounding;
import collision.Rect;
import model.Entity;

public class Wall extends Entity {

	private long vibrationRemaining;
	private int vibrationFrame;

	public Wall() {
		super("Wall");
		this.vibrationRemaining = 0;
		this.vibrationFrame = 0;
	}

	public void vibrate() {
		vibrationRemaining = 200; // 200 ms
		vibrationFrame = 0;
	}

	public void tick(long elapsed) {
		if (vibrationRemaining > 0) {
			vibrationRemaining -= elapsed;
			vibrationFrame++;

			if (vibrationRemaining < 0)
				vibrationRemaining = 0;
		}
	}

	public boolean isVibrating() {
		return vibrationRemaining > 0;
	}

	public int vibrationOffset() {
		if (!isVibrating())
			return 0;
 
		return (vibrationFrame % 2 == 0) ? 3 : -3;
	}

	@Override
	public void setBounding() {
		if (center == null || size == null) {
			return;
		}

		bounding = new collision.Bounding();
		bounding.add(collision.Hitbox.shrunkRect(center, size, orientation_degree));
	}
}