package engine.view;

import engine.model.Entity;
import oop.graphics.Graphics;

public abstract class Avatar {
	protected Entity entity;
	protected View view;
	protected int ShakeX = 0;
	protected int ShakeY = 0;

	public Avatar(Entity entity) {
		this.entity = entity;
	}

	public abstract void paint(Graphics g, int xPix, int yPix, double scale);

	public void setShake(int dx, int dy){
		this.ShakeX = dx;
		this.ShakeY = dy;
	}

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
