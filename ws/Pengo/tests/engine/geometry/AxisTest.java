package engine.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;


/**
 * Axis est une classe pure : normalisation et distance avec/sans tore.
 * Testable immédiatement.
 */
class AxisTest {

	private static final double DELTA = 1e-9;

	// ─── normalize(int) ──────────────────────────────────────────────────────

	@Test
	void normalize_int_sans_tore_retourne_la_valeur_inchangee() {
		Axis axis = new Axis(false, 10);
		assertEquals(12, axis.normalize(12));
		assertEquals(-3, axis.normalize(-3));
	}

	@Test
	void normalize_int_sur_tore_ramene_dans_0_perimeter() {
		Axis axis = new Axis(true, 10);
		assertEquals(2, axis.normalize(12));
		assertEquals(0, axis.normalize(0));
		assertEquals(0, axis.normalize(10));
	}

	@Test
	void normalize_int_sur_tore_gere_les_negatifs_en_restant_positif() {
		Axis axis = new Axis(true, 10);
		assertEquals(9, axis.normalize(-1));
		assertEquals(7, axis.normalize(-13));
	}

	// ─── normalize(double) ───────────────────────────────────────────────────

	@Test
	void normalize_double_sans_tore_retourne_la_valeur_inchangee() {
		Axis axis = new Axis(false, 10);
		assertEquals(12.5, axis.normalize(12.5), DELTA);
	}

	@Test
	void normalize_double_sur_tore_ramene_dans_0_perimeter() {
		Axis axis = new Axis(true, 10);
		assertEquals(2.5, axis.normalize(12.5), DELTA);
		assertEquals(9.5, axis.normalize(-0.5), DELTA);
	}

	// ─── distance ────────────────────────────────────────────────────────────

	@Test
	void distance_sans_tore_est_la_valeur_absolue() {
		Axis axis = new Axis(false, 10);
		assertEquals(8, axis.distance(1, 9), DELTA);
		assertEquals(8, axis.distance(9, 1), DELTA);
	}

	@Test
	void distance_sur_tore_prend_le_plus_court_chemin() {
		Axis axis = new Axis(true, 10);
		// |1-9| = 8 ; 8 >= perimeter/2 (5) → 10 - 8 = 2
		assertEquals(2, axis.distance(1, 9), DELTA);
		// |2-4| = 2 ; 2 < 5 → reste 2
		assertEquals(2, axis.distance(2, 4), DELTA);
	}
}
