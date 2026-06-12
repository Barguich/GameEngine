package engine.view;

import engine.geometry.ISU;
import oop.graphics.Canvas;
import oop.graphics.Graphics;

public class ViewPort {

	private double x_cm = 0;
	private double y_cm = 0;

	private double width_cm;
	private double height_cm;

	// Dimensions de la map — pour le clamping (contrainte Non-Tore)
	private final double mapWidth_cm;
	private final double mapHeight_cm;


	public ViewPort(double width_cm, double height_cm) {
		this.height_cm = height_cm;
		this.width_cm = width_cm;
		this.mapHeight_cm = height_cm;
		this.mapWidth_cm = width_cm;
	}

	public ViewPort(double width_cm, double height_cm, double mapWidth_cm, double mapHeight_cm) {
		this.height_cm = height_cm;
		this.width_cm = width_cm;
		this.mapHeight_cm = mapHeight_cm;
		this.mapWidth_cm = mapWidth_cm;
	}

	public void MoveTo(double x_cm, double y_cm) {
		this.x_cm = x_cm;
		this.y_cm = y_cm;
	}

	/* centre le viewport sur une coordonée ISU */
	public void centerOn(ISU.Coord target) {
		double nx =  target.x() - width_cm / 2;
		double ny = target.y() - height_cm / 2;
		this.x_cm = Math.max(0, Math.min(nx, mapWidth_cm - width_cm));
		this.y_cm = Math.max(0, Math.min(ny, mapHeight_cm - height_cm));
	}

	public double x() {
		return this.x_cm;
	}

	public double getY_cm() {
		return y_cm;
	}

	public double getWidth_cm() {
		return width_cm;
	}

	public double getHeight_cm() {
		return height_cm;
	}

	/**
	 * Teste si une coordonnée ISU est dans le viewport.
	 * Utile pour le culling — ne rendre que les entités visibles.
	 */
	public boolean contains(ISU.Coord coord) {
		double cx = coord.x();
		double cy = coord.y();
		return cx >= x_cm && cx <= x_cm + width_cm
				&& cy >= y_cm && cy <= y_cm + height_cm;
	}

	/** Échelle px/cm — ratio d'aspect préservé (letterbox). */
	public double scale(Canvas c) {
		return Math.min(c.getWidth() / width_cm, c.getHeight() / height_cm);
	}

	private int offsetX(Canvas c) {
		return (int) ((c.getWidth() - width_cm * scale(c)) / 2.0);
	}

	private int offsetY(Canvas c) {
		return (int) ((c.getHeight() - height_cm * scale(c)) / 2.0);
	}

	public int toPixelX(Canvas c, double world_x_cm) {
		return offsetX(c) + (int) ((world_x_cm - x_cm) * scale(c));
	}

	public int toPixelY(Canvas c, double world_y_cm) {
		return offsetY(c) + (int) ((world_y_cm - y_cm) * scale(c));
	}

	/** Remplit la zone du monde (fond). */
	public void fill(Canvas c, Graphics g) {
		double s = scale(c);
		g.fillRect(offsetX(c), offsetY(c), (int) (width_cm * s), (int) (height_cm * s));
	}

	/** Clip graphique sur la zone du monde. */
	public void clip(Canvas c, Graphics g) {
		double s = scale(c);
		g.setClip(offsetX(c), offsetY(c), (int) (width_cm * s), (int) (height_cm * s));
	}
}
