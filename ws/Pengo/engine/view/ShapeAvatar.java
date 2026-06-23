package view;

import oop.graphics.Graphics;
import model.Entity;
import oop.graphics.Color;

public class ShapeAvatar extends Avatar {

	public enum Shape {
		OVAL, RECT
	}

	protected final Shape shape;
	protected final int a, r, g, b;
	protected Color color;
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

	/** @apiNote forme de collision alignée avec la forme visuelle */
	@Override
	public void buildBounding(collision.Bounding bounding, geometry.ISU.Coord center, geometry.ISU.Dimension size) {
		if (shape == Shape.OVAL) {
			double r = Math.min(size.x(), size.y()) / 2;
			bounding.add(new collision.Circle(center, r));
		} else {
			bounding.add(new collision.Rect(center, size, entity.orientation()));
		}
	}

	public void flashCollision() {
		flashUntil = System.currentTimeMillis() + 300;
	}

	/**
	 * @implNote couleurs résolues paresseusement : {@code getColor} exige un
	 *           Graphics vivant, indisponible au constructeur. Rotation appliquée
	 *           autour du centre via translate puis rotate.
	 */
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
		Color active;

		if (flashing) {
			active = getFlashColor(g);
		} else {
			active = resolveBaseColor(g);
		}

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

	/**
	 * Couleur de fond de la forme hors flash. Le moteur renvoie la couleur fixe
	 * fournie au constructeur ; un jeu peut surcharger cette méthode pour faire
	 * varier la couleur selon l'état de son entité (sans coupler le moteur au jeu).
	 */
	protected Color resolveBaseColor(Graphics g) {
		return color;
	}

	private Color getFlashColor(Graphics g) {
		if (flashColor == null)
			flashColor = g.getColor(255, 255, 50, 50);
		return flashColor;
	}

}
