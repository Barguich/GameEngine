package engine.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import engine.collision.Circle;
import engine.geometry.ISU;
import engine.Game;
import engine.collision.Box;

/**
 * Circle : intersection cercle-cercle et boîte englobante.
 * Coordonnées centrales pour éviter les copies virtuelles de tore.
 */
class CircleTest {

	private static final double DELTA = 1e-9;

	@BeforeEach
	void setUp() {
		new Game(20, 20); // 74 cm de côté
	}

	private ISU.Coord coord(double x, double y) {
		return Game.isu().new Coord(x, y);
	}

	@Test
	void deux_cercles_qui_se_chevauchent_s_intersectent() {
		Circle a = new Circle(coord(10, 10), 2);
		Circle b = new Circle(coord(13, 10), 2);
		// distance des centres = 3 <= 2 + 2 = 4
		assertTrue(a.intersects(b));
	}

	@Test
	void deux_cercles_eloignes_ne_s_intersectent_pas() {
		Circle a = new Circle(coord(10, 10), 1);
		Circle b = new Circle(coord(20, 10), 1);
		// distance = 10 > 1 + 1
		assertFalse(a.intersects(b));
	}

	@Test
	void cercles_tangents_s_intersectent_inegalite_large() {
		Circle a = new Circle(coord(10, 10), 2);
		Circle b = new Circle(coord(14, 10), 2);
		// distance = 4 == somme des rayons → true (<=)
		assertTrue(a.intersects(b));
	}

	@Test
	void box_d_un_cercle_est_le_carre_englobant() {
		Circle a = new Circle(coord(10, 10), 2);
		Box box = a.box();
		assertEquals(8, box.xmin(), DELTA);
		assertEquals(8, box.ymin(), DELTA);
		assertEquals(12, box.xmax(), DELTA);
		assertEquals(12, box.ymax(), DELTA);
	}
}
