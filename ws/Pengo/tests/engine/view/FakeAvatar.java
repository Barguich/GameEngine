package engine.view;

import model.Entity;
import oop.graphics.Graphics;
import view.Avatar;
import view.View;

/** Sous-classe concrète minimale de Avatar (abstrait) pour les tests. */
public class FakeAvatar extends Avatar {

	public int paintCount = 0;
	public int lastXPix;
	public int lastYPix;
	public double lastScale;

	public FakeAvatar(Entity entity) {
		super(entity);
	}

	@Override
	public void paint(Graphics g, int xPix, int yPix, double scale) {
		paintCount++;
		lastXPix = xPix;
		lastYPix = yPix;
		lastScale = scale;
	}

	public Object callSaveTransform(Graphics g) {
		return saveTransform(g);
	}

	public void callRestoreTransform(Graphics g, Object saved) {
		restoreTransform(g, saved);
	}

	public Entity entity() {
		return entity;
	}

	public View view() {
		return view;
	}
}
