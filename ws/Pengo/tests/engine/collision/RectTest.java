package engine.collision;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import collision.Box;
import collision.Circle;
import collision.Rect;
import engine.Game;
import geometry.ISU;

/**
 * Rect : intersection rect-rect, rect-cercle et boîtes englobantes.
 * Game(20,20) -> width_cm = height_cm = 74 (torus actif sur les deux axes).
 */
class RectTest {

	private static final double DELTA = 1e-9;

	@BeforeEach
	void setUp() {
		new Game(20, 20);
	}

	private ISU.Coord coord(double x, double y) {
		return Game.isu().new Coord(x, y);
	}

	private ISU.Dimension dim(double x, double y) {
		return Game.isu().new Dimension(x, y);
	}

	// ─── box() / box_360() ─────────────────────────────────────────────────

	@Test
	void box_d_un_rect_non_tourne_est_son_rectangle_englobant() {
		Rect r = new Rect(coord(37, 37), dim(10, 4), 0);
		Box box = r.box();
		assertEquals(32, box.xmin(), DELTA);
		assertEquals(35, box.ymin(), DELTA);
		assertEquals(42, box.xmax(), DELTA);
		assertEquals(39, box.ymax(), DELTA);
	}

	@Test
	void box_d_un_rect_tourne_90_degres_echange_largeur_et_hauteur() {
		Rect r = new Rect(coord(37, 37), dim(10, 4), 90);
		Box box = r.box();
		assertEquals(35, box.xmin(), DELTA);
		assertEquals(32, box.ymin(), DELTA);
		assertEquals(39, box.xmax(), DELTA);
		assertEquals(42, box.ymax(), DELTA);
	}

	@Test
	void box_360_est_le_cercle_englobant_quel_que_soit_l_angle() {
		Rect r = new Rect(coord(37, 37), dim(6, 8), 0);
		Box box = r.box_360();
		// rayon = sqrt(3^2 + 4^2) = 5
		assertEquals(32, box.xmin(), DELTA);
		assertEquals(32, box.ymin(), DELTA);
		assertEquals(42, box.xmax(), DELTA);
		assertEquals(42, box.ymax(), DELTA);
	}

	// ─── Rect / Rect, axes alignés ──────────────────────────────────────────

	@Test
	void deux_rects_alignes_qui_se_chevauchent_s_intersectent() {
		Rect a = new Rect(coord(37, 37), dim(10, 10), 0);
		Rect b = new Rect(coord(40, 37), dim(10, 10), 0);
		assertTrue(a.intersects(b));
		assertTrue(b.intersects(a));
	}

	@Test
	void deux_rects_alignes_eloignes_ne_s_intersectent_pas() {
		Rect a = new Rect(coord(10, 10), dim(4, 4), 0);
		Rect b = new Rect(coord(30, 30), dim(4, 4), 0);
		assertFalse(a.intersects(b));
		assertFalse(b.intersects(a));
	}

	// ─── Rect / Rect, rotation : SAT ────────────────────────────────────────

	@Test
	void deux_rects_tournes_qui_se_chevauchent_par_les_coins_s_intersectent() {
		// Deux carrés tournés de 45° dont les coins se touchent au centre.
		Rect a = new Rect(coord(35, 37), dim(6, 6), 45);
		Rect b = new Rect(coord(39, 37), dim(6, 6), 45);
		assertTrue(a.intersects(b));
	}

	@Test
	void deux_rects_tournes_separes_par_un_axe_ne_s_intersectent_pas() {
		Rect a = new Rect(coord(10, 10), dim(4, 4), 30);
		Rect b = new Rect(coord(30, 30), dim(4, 4), 60);
		assertFalse(a.intersects(b));
	}

	// ─── Rect / Circle ──────────────────────────────────────────────────────

	@Test
	void rect_et_cercle_qui_se_chevauchent_s_intersectent() {
		Rect r = new Rect(coord(37, 37), dim(10, 10), 0);
		Circle c = new Circle(coord(37, 37), 2);
		assertTrue(r.intersects(c));
		assertTrue(c.intersects(r));
	}

	@Test
	void rect_et_cercle_eloignes_ne_s_intersectent_pas() {
		Rect r = new Rect(coord(10, 10), dim(4, 4), 0);
		Circle c = new Circle(coord(30, 30), 2);
		assertFalse(r.intersects(c));
		assertFalse(c.intersects(r));
	}

	@Test
	void rect_et_cercle_tangents_au_bord_s_intersectent() {
		// Rect [32,42] x [35,39]. Cercle centré juste au bord droit, rayon 1.
		Rect r = new Rect(coord(37, 37), dim(10, 4), 0);
		Circle c = new Circle(coord(43, 37), 1);
		assertTrue(r.intersects(c));
	}

	// ─── Torus : copies virtuelles ──────────────────────────────────────────

	@Test
	void rect_pres_du_bord_intersecte_un_cercle_pres_du_bord_oppose_via_le_tore() {
		// width_cm = height_cm = 74. Un rect centré en (1,37) dépasse à gauche
		// (xmin < 0) et possède donc une copie virtuelle à droite (x + 74).
		Rect r = new Rect(coord(1, 37), dim(4, 4), 0);
		// Le cercle est très proche du bord droit (x = 73), donc proche de la
		// copie virtuelle du rect à x = 75.
		Circle c = new Circle(coord(73.5, 37), 1);
		assertTrue(r.intersects(c));
	}

	@Test
	void rect_pres_du_bord_intersecte_un_rect_pres_du_bord_oppose_via_le_tore() {
		Rect a = new Rect(coord(1, 37), dim(4, 4), 0);
		Rect b = new Rect(coord(73.5, 37), dim(4, 4), 0);
		assertTrue(a.intersects(b));
		assertTrue(b.intersects(a));
	}
}
