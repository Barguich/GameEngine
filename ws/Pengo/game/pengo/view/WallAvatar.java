package pengo.view;

import model.Entity;
import oop.graphics.BufferedImage;
import oop.graphics.Graphics;
import view.Avatar;

/**
 * Avatar graphique des murs.
 *
 * Cette classe ne contient aucune logique de jeu : elle se contente d'afficher
 * l'image associée à un mur.
 *
 * Les collisions, vibrations et interactions sont gérées par la classe Wall
 * côté modèle.
 */
public class WallAvatar extends Avatar {

	// Sprite utilisé pour représenter une bordure du niveau.
	private static final String WALL_PATH = "Asset/border/border.png";

	public WallAvatar(Entity entity) {
		super(entity);
	}

	@Override
	public void paint(Graphics g, int xPix, int yPix, double scale) {

		// Impossible de dessiner un mur sans taille définie.
		if (entity == null || entity.size() == null) {
			return;
		}

		// Chargement du sprite via le cache partagé.
		BufferedImage img = Sprites.get(g, WALL_PATH);

		/*
		 * Conversion de la taille du mur en pixels. Un mur occupe généralement une case
		 * complète.
		 */
		int side = (int) Math.round(entity.size().x() * scale);

		// Dessin centré sur la position de l'entité.
		g.drawImage(img, xPix - side / 2, yPix - side / 2, side, side);
	}
}