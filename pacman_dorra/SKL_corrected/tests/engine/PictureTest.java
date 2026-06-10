package engine;

import org.junit.jupiter.api.Test;

import engine.Picture;
import game.Game;

public class PictureTest {

	private void verifint(int expected, int result, String msg) {

		if (expected != result) {

			throw new Error(msg + " expected " + expected + " but got " + result);
		}
	}

	private void verifnotnull(Object obj, String msg) {

		if (obj == null) {

			throw new Error(msg + " is null");
		}
	}

	@Test
	public void test00PictureCreation() {

		Game game = new Game(10, 10);

		Picture picture = new Picture(game);

		verifnotnull(picture, "picture creation");
	}

	@Test
	public void test01PixelCreation() {

		Game game = new Game(10, 10);

		Picture picture = new Picture(game);

		Picture.Pixel pixel = picture.new Pixel(100, 200);

		verifint(100, pixel.x(), "pixel x");

		verifint(200, pixel.y(), "pixel y");
	}

	@Test
	public void test02MultiplePixels() {

		Game game = new Game(10, 10);

		Picture picture = game.pict();

		Picture.Pixel p1 = picture.new Pixel(0, 0);

		Picture.Pixel p2 = picture.new Pixel(50, 75);

		verifint(0, p1.x(), "p1 x");

		verifint(0, p1.y(), "p1 y");

		verifint(50, p2.x(), "p2 x");

		verifint(75, p2.y(), "p2 y");
	}

}