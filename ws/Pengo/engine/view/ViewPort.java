package view;

import geometry.ISU;
import oop.graphics.Canvas;
import oop.graphics.Graphics;

/**
 * Caméra du moteur : fenêtre rectangulaire du monde projetée sur le
 * canvas. Convertit les coordonnées monde.
 *
 * @apiNote la zone de la map est figée à la construction et borne le
 *          déplacement
 *          de la caméra. La caméra ne sort jamais de la
 *          map.
 */

public class ViewPort {

	private double x_cm = 0;
	private double y_cm = 0;

	private double width_cm;
	private double height_cm;

	// Dimensions de la map — pour le clamping (contrainte Non-Tore)
	private double mapWidth_cm;
	private  double mapHeight_cm;

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

	/** @apiNote place l'origine de la caméra sans clamping */
	public void MoveTo(double x_cm, double y_cm) {
		this.x_cm = x_cm;
		this.y_cm = y_cm;
	}

	/* centre le viewport sur une coordonée ISU */
	public void centerOn(ISU.Coord target) {
		double nx = target.x() - width_cm / 2;
		double ny = target.y() - height_cm / 2;
		this.x_cm = clampX(nx);
		this.y_cm = clampY(ny);
	}

	/**
	 * Suivi élastique : le joueur peut bouger dans une zone morte (dead-zone)
	 * centrée sur le viewport sans déclencher de déplacement de la caméra.
	 * La caméra ne se décale que de la quantité dont {@code target} dépasse
	 * cette zone.
	 *
	 * @param target      position à suivre (centre du joueur), en ISU
	 * @param marginX_cm  demi-largeur de la dead-zone depuis le centre, en cm
	 * @param marginY_cm  demi-hauteur de la dead-zone depuis le centre, en cm
	 */
	public void followElastic(ISU.Coord target, double marginX_cm, double marginY_cm) {
		// Position du joueur relative au coin courant de la caméra.
		double relX = target.x() - x_cm;
		double relY = target.y() - y_cm;

		// Bornes de la dead-zone (centrée dans le viewport).
		double leftBound = width_cm / 2 - marginX_cm;
		double rightBound = width_cm / 2 + marginX_cm;
		double topBound = height_cm / 2 - marginY_cm;
		double bottomBound = height_cm / 2 + marginY_cm;

		// On ne pousse la caméra que de ce qui dépasse la dead-zone.
		if (relX < leftBound) {
			x_cm -= (leftBound - relX);
		} else if (relX > rightBound) {
			x_cm += (relX - rightBound);
		}
		if (relY < topBound) {
			y_cm -= (topBound - relY);
		} else if (relY > bottomBound) {
			y_cm += (relY - bottomBound);
		}

		// Clamping aux bords de la map (contrainte Non-Tore).
		x_cm = clampX(x_cm);
		y_cm = clampY(y_cm);
	}

	/** Borne l'origine X de la caméra à l'intérieur de la map. */
	private double clampX(double x) {
		return Math.max(0, Math.min(x, mapWidth_cm - width_cm));
	}

	/** Borne l'origine Y de la caméra à l'intérieur de la map. */
	private double clampY(double y) {
		return Math.max(0, Math.min(y, mapHeight_cm - height_cm));
	}
	/**
	 *  applique la taille réelle de la nouvelle map (en cm) pour
	 * le clamping, sans toucher à la taille visible de la fenêtre (width_cm/height_cm).
	 * Recentre/reclamp la caméra dans les nouvelles bornes.
	 */
	public void resizeMap(double mapWidth_cm, double mapHeight_cm) {
		this.mapWidth_cm = mapWidth_cm;
		this.mapHeight_cm = mapHeight_cm;

		// Si la nouvelle map est plus petite que la fenêtre visible,
		// on ne peut pas avoir une zone de map négative pour le clamp.
		this.x_cm = clampX(0);
		this.y_cm = clampY(0);
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
