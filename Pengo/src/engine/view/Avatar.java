package engine.view;

import engine.model.Entity;
import oop.graphics.Graphics;

public abstract class Avatar {
	protected Entity entity;
	protected View view;

	public Avatar(Entity entity) {
		this.entity = entity;
	}

	public abstract void paint(Graphics g, int xPix, int yPix);

	protected Object saveTransform(Graphics g) {
		return g.getTransform();
	}

	protected void restoreTransform(Graphics g, Object saved) {
		g.setTransform(saved);
	}

	public void setView(View view) {
		this.view = view;
	}
}
