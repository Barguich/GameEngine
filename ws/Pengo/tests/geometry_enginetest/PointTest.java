package geometry_enginetest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import geometry.Point;
import geometry.Vector;


/**
 * Point est une classe pure. translated/vectorToward renvoient des copies,
 * rotateAround mute l'instance.
 */
class PointTest {

	private static final double DELTA = 1e-9;

	@Test
	void translated_renvoie_un_nouveau_point_decale() {
		Point p = new Point(1, 2);
		Point t = p.translated(new Vector(3, 4));
		assertEquals(4, t.x(), DELTA);
		assertEquals(6, t.y(), DELTA);
		// l'original n'est pas muté
		assertEquals(1, p.x(), DELTA);
		assertEquals(2, p.y(), DELTA);
	}

	@Test
	void vectorToward_donne_le_vecteur_entre_deux_points() {
		Point a = new Point(1, 1);
		Point b = new Point(4, 5);
		Vector v = a.vectorToward(b);
		assertEquals(3, v.x(), DELTA);
		assertEquals(4, v.y(), DELTA);
	}

	@Test
	void rotateAround_origine_90_degres_mute_le_point() {
		Point p = new Point(1, 0);
		p.rotateAround(new Point(0, 0), 90);
		assertEquals(0, p.x(), DELTA);
		assertEquals(1, p.y(), DELTA);
	}

	@Test
	void rotateAround_centre_quelconque() {
		Point p = new Point(2, 1);
		p.rotateAround(new Point(1, 1), 90);
		// (2,1) autour de (1,1) : delta (1,0) → (0,1) → (1,2)
		assertEquals(1, p.x(), DELTA);
		assertEquals(2, p.y(), DELTA);
	}
}
