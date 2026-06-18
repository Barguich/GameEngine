package engine.view;

import oop.graphics.Graphics;
import oop.graphics.Color;
import engine.model.Entity;

public class ShapeAvatar extends Avatar {

	public enum Shape {
		OVAL, RECT
	}

	private final Shape shape;
	private final int a, r, g, b;
	private Color color;
	private Color flashColor;
	private long flashUntil = 0;

	public ShapeAvatar(Entity entity, Shape shape, int a, int r, int g, int b) {
		super(entity);
		this.shape = shape;
		this.a = a;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	@Override
	public void buildBounding(engine.collision.Bounding bounding, engine.geometry.ISU.Coord center, engine.geometry.ISU.Dimension size) {
		if (shape == Shape.OVAL) {
			double r = Math.min(size.x(), size.y()) / 2;
			bounding.add(new engine.collision.Circle(center, r));
		} else {
			bounding.add(new engine.collision.Rect(center, size, entity.orientation()));
		}
	}

	public void flashCollision() {
		flashUntil = System.currentTimeMillis() + 300;
	}

	@Override
	public void paint(Graphics g, int xPix, int yPix, double scale) {
		if (color == null) {
			color = g.getColor(a, r, this.g, b);
		}
		if (entity.size() == null) {
			return;
		}
		int halfW = (int) (entity.size().x() / 2 * scale);
		int halfH = (int) (entity.size().y() / 2 * scale);

		boolean flashing = System.currentTimeMillis() < flashUntil;
		Color active = flashing ? getFlashColor(g) : color;

		Object saved = saveTransform(g);
		g.translate(xPix, yPix);
		g.rotate(Math.toRadians(entity.orientation()));
		g.setColor(active);
		if (shape == Shape.OVAL) {
			g.fillOval(-halfW, -halfH, halfW * 2, halfH * 2);
		} else {
			g.fillRect(-halfW, -halfH, halfW * 2, halfH * 2);
		}
		restoreTransform(g, saved);
	}

	private Color getFlashColor(Graphics g) {
		if (flashColor == null)
			flashColor = g.getColor(255, 255, 50, 50);
		return flashColor;
	}

}
