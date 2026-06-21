package geometry_enginetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import engine.Game;
import engine.Picture;
import geometry.ISU;

/**
 * Picture convertit des cm en pixels (× pixelPerCm = 2) avec arrondi.
 * Dépend d'un Game initialisé.
 */
class PictureTest {

	private Game game;

	@BeforeEach
	void setUp() {
		this.game = new Game(20, 20); // width_cm = height_cm = 74.0
	}

	@Test
	void toPixelLength_multiplie_par_pixelPerCm_et_arrondit() {
		Picture pic = new Picture(game);
		assertEquals(2, pic.toPixelLength(1.0));   // 1 × 2
		assertEquals(10, pic.toPixelLength(5.0));  // 5 × 2
		assertEquals(1, pic.toPixelLength(0.5));   // 1.0 → round = 1
	}

	@Test
	void dimensions_en_pixels_derivees_des_cm_du_jeu() {
		Picture pic = new Picture(game);
		// 74.0 × 2 = 148
		assertEquals(148, pic.width());
		assertEquals(148, pic.height());
	}

	@Test
	void toPixelX_et_toPixelY_convertissent_une_coordonnee() {
		Picture pic = new Picture(game);
		assertEquals(20, pic.toPixelX(10.0));
		assertEquals(40, pic.toPixelY(20.0));
	}

	@Test
	void toPixel_construit_un_Pixel_aux_bonnes_coordonnees() {
		Picture pic = new Picture(game);
		ISU.Coord c = Game.isu().new Coord(10.0, 20.0);
		Picture.Pixel p = pic.toPixel(c);
		assertEquals(20, p.x());
		assertEquals(40, p.y());
	}

	@Test
	void pixel_equals_compare_les_coordonnees() {
		Picture pic = new Picture(game);
		Picture.Pixel a = pic.new Pixel(3, 4);
		Picture.Pixel b = pic.new Pixel(3, 4);
		Picture.Pixel c = pic.new Pixel(3, 5);
		assertEquals(a, b);
		assertNotEquals(a, c);
	}
}
