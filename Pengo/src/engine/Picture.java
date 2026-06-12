package engine;

import java.io.PrintStream;

import engine.geometry.ISU;


public class Picture {

	private Game game;
	private int width_pixel;
	private int height_pixel;

	public Picture(Game game) {
		assert game != null;
		this.game = game;
		this.width_pixel = toPixelLength(game.width_cm);
		this.height_pixel = toPixelLength(game.height_cm);
	}

	public int toPixelLength(double length_cm) {
		return (int) Math.round(length_cm * game.pixelPerCm);
	}

	public int toPixelX(double x_cm) {
		return toPixelLength(x_cm);
	}

	public int toPixelY(double y_cm) {
		return toPixelLength(y_cm);
	}

	public Pixel toPixel(ISU.Coord coord) {
		assert coord != null;
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
			return x_pixel == p.x_pixel && y_pixel == p.y_pixel;
		}
	}
}
