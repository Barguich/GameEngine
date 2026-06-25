package engine;

import java.io.PrintStream;

import geometry.ISU;

/**
 * Représentation de la projection graphique du monde.
 *
 * Cette classe assure la conversion entre les coordonnées
 * physiques du moteur (en centimètres) et les coordonnées
 * utilisées pour l'affichage (en pixels).
 *
 * Elle joue le rôle d'intermédiaire entre le modèle du jeu
 * et la couche graphique.
 */
public class Picture {

	// Référence vers le monde afin de récupérer
	// les paramètres de conversion (pixelPerCm).
	private Game game;

	// Taille de la zone d'affichage en pixels.
	private int width_pixel;
	private int height_pixel;

	public Picture(Game game) {
		this.game = game;

		// La taille de l'image est calculée à partir
		// des dimensions réelles du monde.
		this.width_pixel = toPixelLength(game.width_cm);
		this.height_pixel = toPixelLength(game.height_cm);
	}

	/**
	 * Convertit une distance exprimée dans le repère
	 * physique du moteur (cm) vers une distance écran (pixels).
	 */
	public int toPixelLength(double length_cm) {
		return (int) Math.round(length_cm * game.pixelPerCm);
	}

	/**
	 * Conversion de l'abscisse d'un point du monde.
	 */
	public int toPixelX(double x_cm) {
		return toPixelLength(x_cm);
	}

	/**
	 * Conversion de l'ordonnée d'un point du monde.
	 */
	public int toPixelY(double y_cm) {
		return toPixelLength(y_cm);
	}

	/**
	 * Conversion complète d'une coordonnée du monde
	 * vers une position écran.
	 */
	public Pixel toPixel(ISU.Coord coord) {
		return new Pixel(toPixelX(coord.x()), toPixelY(coord.y()));
	}

	public Game game() {
		return game;
	}

	public int width() {
		return width_pixel;
	}

	public int height() {
		return height_pixel;
	}

	/**
	 * Représentation d'un point écran.
	 *
	 * Cette classe est utilisée après projection
	 * des coordonnées du monde dans le repère graphique.
	 */
	public class Pixel {

		private int x_pixel;
		private int y_pixel;

		public Pixel(int x_pixel, int y_pixel) {
			this.x_pixel = x_pixel;
			this.y_pixel = y_pixel;
		}

		public int x() {
			return x_pixel;
		}

		public int y() {
			return y_pixel;
		}

		/**
		 * Affichage utilisé principalement pour le debug.
		 */
		public void show(PrintStream ps) {
			ps.println("Pixel(" + x_pixel + "," + y_pixel + ")");
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Pixel)) {
				return false;
			}

			Pixel p = (Pixel) o;

			// Deux pixels représentent le même point écran
			// lorsqu'ils possèdent les mêmes coordonnées.
			return x_pixel == p.x_pixel && y_pixel == p.y_pixel;
		}
	}
}