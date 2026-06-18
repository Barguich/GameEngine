package view;

import model.Entity;
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

	public void buildBounding(collision.Bounding bounding, geometry.ISU.Coord center, geometry.ISU.Dimension size) {
		bounding.add(new collision.Rect(center, size, entity.orientation()));
	}
}
