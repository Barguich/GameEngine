package view;

import model.Entity;
import oop.graphics.Graphics;

/**
 * Représentation visuelle d'une {@link Entity}. Chaque entité possède un avatar
 * qui sait se dessiner et déclarer sa forme de collision
 */
public abstract class Avatar {
	protected Entity entity;
	protected View view;
	protected int ShakeX = 0;
	protected int ShakeY = 0;

	public Avatar(Entity entity) {
		this.entity = entity;
	}

	public abstract void paint(Graphics g, int xPix, int yPix, double scale);

	/* Effet de tremblement */
	public void setShake(int dx, int dy) {
		this.ShakeX = dx;
		this.ShakeY = dy;
	}

	/**
	 * @apiNote encadrent tout mouvement pour assurer l'état du graphics entre les
	 *          avatars
	 */
	protected Object saveTransform(Graphics g) {
		return g.getTransform();
	}

	protected void restoreTransform(Graphics g, Object saved) {
		g.setTransform(saved);
	}

	public void setView(View view) {
		this.view = view;
	}

	/* ajoute au bounding la forme de la collision de l'avatar */
	public void buildBounding(collision.Bounding bounding, geometry.ISU.Coord center, geometry.ISU.Dimension size) {
		bounding.add(new collision.Rect(center, size, entity.orientation()));
	}
}
