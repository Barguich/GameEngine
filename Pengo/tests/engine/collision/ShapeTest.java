package engine.collision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import engine.Game;
import engine.geometry.ISU;

/**
 * Shape est la base commune de Circle/Rect : elle copie le centre reçu et
 * conserve une référence vers l'ISU d'origine. Test dans le package
 * engine.collision pour accéder aux champs package-private isu/center.
 */
class ShapeTest {

	private static final double DELTA = 1e-9;

	@BeforeEach
	void setUp() {
		new Game(20, 20);
	}

	@Test
	void le_constructeur_copie_le_centre_reçu() {
		ISU.Coord original = Game.isu().new Coord(10, 10);
		Shape s = new Shape(original);

		assertNotSame(original, s.center);
		assertEquals(10, s.center.x(), DELTA);
		assertEquals(10, s.center.y(), DELTA);
	}

	@Test
	void mutation_du_centre_original_n_affecte_pas_la_shape() {
		ISU.Coord original = Game.isu().new Coord(10, 10);
		Shape s = new Shape(original);

		original.translate(Game.isu().new Vector(5, 5));

		assertEquals(10, s.center.x(), DELTA);
		assertEquals(10, s.center.y(), DELTA);
	}

	@Test
	void le_constructeur_recupere_l_isu_du_centre() {
		ISU.Coord original = Game.isu().new Coord(10, 10);
		Shape s = new Shape(original);

		assertSame(original.isu(), s.isu);
	}
}
