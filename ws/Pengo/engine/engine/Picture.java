package engine;

import java.io.PrintStream;

import geometry.ISU;

public class Picture {

	// Permet de convertir les coordonnées du monde en pixels
	private Game game;

	// Dimensions de l'image affichée
	private int width_pixel;
	private int height_pixel;

	public Picture(Game game) {
		this.game = game;

		// Conversion de la taille du monde en pixels
		this.width_pixel = toPixelLength(game.width_cm);
		this.height_pixel = toPixelLength(game.height_cm);
	}

	// Conversion d'une longueur exprimée en cm vers les pixels
	public int toPixelLength(double length_cm) {
		return (int) Math.round(length_cm * game.pixelPerCm);
	}

	public int toPixelX(double x_cm) {
		return toPixelLength(x_cm);
	}

	public int toPixelY(double y_cm) {
		return toPixelLength(y_cm);
	}

	// Conversion d'une coordonnée du monde vers une position écran
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

	/*
	 * Représente une position à l'écran en pixels.
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

		public void show(PrintStream ps) {
			ps.println("Pixel(" + x_pixel + "," + y_pixel + ")");
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Pixel)) {
				return false;
			}

			Pixel p = (Pixel) o;

			// Deux pixels sont égaux s'ils ont les mêmes coordonnées
			return x_pixel == p.x_pixel && y_pixel == p.y_pixel;
		}
	}
}