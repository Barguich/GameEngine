package geometry_enginetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import collision.Box;

/**
 * Box est une classe pure (aucune dépendance) : testable immédiatement,
 * sans given.jar ni Game.
 */
class BoxTest {

	private static final double DELTA = 1e-9;

	@Test
	void construction_valide_expose_les_bornes() {
		Box b = new Box(0, 0, 4, 2);
		assertEquals(0, b.xmin(), DELTA);
		assertEquals(0, b.ymin(), DELTA);
		assertEquals(4, b.xmax(), DELTA);
		assertEquals(2, b.ymax(), DELTA);
	}

	@Test
	void xmin_superieur_a_xmax_leve_IllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> new Box(5, 0, 1, 4));
	}

	@Test
	void ymin_superieur_a_ymax_leve_IllegalArgumentException() {
		assertThrows(IllegalArgumentException.class, () -> new Box(0, 5, 4, 1));
	}

	@Test
	void width_height_et_centre_sont_calcules() {
		Box b = new Box(2, 4, 8, 10);
		assertEquals(6, b.width(), DELTA);
		assertEquals(6, b.height(), DELTA);
		assertEquals(5, b.centerX(), DELTA);
		assertEquals(7, b.centerY(), DELTA);
	}

	@Test
	void overlaps_vrai_quand_les_boites_se_chevauchent() {
		Box a = new Box(0, 0, 4, 4);
		Box b = new Box(2, 2, 6, 6);
		assertTrue(a.overlaps(b));
		assertTrue(b.overlaps(a));
	}

	@Test
	void overlaps_faux_quand_les_boites_sont_disjointes() {
		Box a = new Box(0, 0, 2, 2);
		Box b = new Box(5, 5, 7, 7);
		assertFalse(a.overlaps(b));
	}

	@Test
	void overlaps_avec_null_retourne_faux() {
		Box a = new Box(0, 0, 2, 2);
		assertFalse(a.overlaps(null));
	}

	/**
	 * Test de caractérisation : overlaps utilise des inégalités STRICTES,
	 * donc deux boîtes qui ne se touchent que par une arête ne se chevauchent
	 * PAS. On documente le comportement réel sans le corriger.
	 */
	@Test
	void overlaps_faux_quand_les_boites_se_touchent_seulement_par_une_arete() {
		Box a = new Box(0, 0, 2, 2);
		Box b = new Box(2, 0, 4, 2);
		assertFalse(a.overlaps(b));
	}

	@Test
	void union_de_deux_boites_donne_la_boite_englobante() {
		Box a = new Box(0, 0, 2, 2);
		Box b = new Box(3, 1, 5, 6);
		Box u = Box.union(a, b);
		assertEquals(0, u.xmin(), DELTA);
		assertEquals(0, u.ymin(), DELTA);
		assertEquals(5, u.xmax(), DELTA);
		assertEquals(6, u.ymax(), DELTA);
	}

	@Test
	void union_avec_null_retourne_l_autre_boite() {
		Box a = new Box(0, 0, 2, 2);
		assertEquals(a, Box.union(a, null));
		assertEquals(a, Box.union(null, a));
		assertNull(Box.union(null, null));
	}

	@Test
	void toString_contient_les_bornes() {
		Box a = new Box(0, 1, 2, 3);
		assertTrue(a.toString().contains("0.0"));
		assertTrue(a.toString().contains("3.0"));
	}
}
