package engine.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import engine.Game;

/**
 * ViewPort : déplacement, centrage avec clamping aux bords de la map,
 * test d'appartenance (culling), et calculs d'échelle/pixels.
 */
class ViewPortTest {

	private static final double DELTA = 1e-9;

	@BeforeEach
	void setUp() {
		new Game(100, 80);
	}

	// ─── constructeurs ──────────────────────────────────────────────────────

	@Test
	void constructeur_2_args_utilise_les_dimensions_comme_map() {
		ViewPort vp = new ViewPort(50, 30);

		assertEquals(50, vp.getWidth_cm(), DELTA);
		assertEquals(30, vp.getHeight_cm(), DELTA);
		assertEquals(0, vp.x(), DELTA);
		assertEquals(0, vp.getY_cm(), DELTA);
	}

	@Test
	void constructeur_4_args_separe_viewport_et_map() {
		ViewPort vp = new ViewPort(20, 10, 100, 80);

		assertEquals(20, vp.getWidth_cm(), DELTA);
		assertEquals(10, vp.getHeight_cm(), DELTA);
	}

	// ─── MoveTo ─────────────────────────────────────────────────────────────

	@Test
	void moveTo_deplace_sans_clamping() {
		ViewPort vp = new ViewPort(20, 10, 100, 80);

		vp.MoveTo(1000, -500);

		assertEquals(1000, vp.x(), DELTA);
		assertEquals(-500, vp.getY_cm(), DELTA);
	}

	// ─── centerOn ───────────────────────────────────────────────────────────

	@Test
	void centerOn_centre_la_vue_sur_la_cible() {
		ViewPort vp = new ViewPort(20, 10, 100, 80);

		vp.centerOn(Game.isu().new Coord(50, 40));

		assertEquals(40, vp.x(), DELTA);
		assertEquals(35, vp.getY_cm(), DELTA);
	}

	@Test
	void centerOn_clamp_au_bord_inferieur() {
		ViewPort vp = new ViewPort(20, 10, 100, 80);

		vp.centerOn(Game.isu().new Coord(0, 0));

		assertEquals(0, vp.x(), DELTA);
		assertEquals(0, vp.getY_cm(), DELTA);
	}

	@Test
	void centerOn_clamp_au_bord_superieur() {
		ViewPort vp = new ViewPort(20, 10, 100, 80);

		vp.centerOn(Game.isu().new Coord(95, 75));

		assertEquals(100 - 20, vp.x(), DELTA);
		assertEquals(80 - 10, vp.getY_cm(), DELTA);
	}

	// ─── contains ───────────────────────────────────────────────────────────

	@Test
	void contains_vrai_pour_un_point_dans_le_viewport() {
		ViewPort vp = new ViewPort(20, 10, 100, 80);
		vp.MoveTo(10, 5);

		assertTrue(vp.contains(Game.isu().new Coord(15, 8)));
	}

	@Test
	void contains_inclut_les_bords() {
		ViewPort vp = new ViewPort(20, 10, 100, 80);
		vp.MoveTo(10, 5);

		assertTrue(vp.contains(Game.isu().new Coord(10, 5)));
		assertTrue(vp.contains(Game.isu().new Coord(30, 15)));
	}

	@Test
	void contains_faux_hors_du_viewport() {
		ViewPort vp = new ViewPort(20, 10, 100, 80);
		vp.MoveTo(10, 5);

		assertFalse(vp.contains(Game.isu().new Coord(0, 0)));
		assertFalse(vp.contains(Game.isu().new Coord(50, 50)));
	}

	// ─── scale / toPixelX / toPixelY ────────────────────────────────────────

	@Test
	void scale_prend_le_plus_petit_ratio_largeur_hauteur() {
		ViewPort vp = new ViewPort(20, 10, 100, 80);
		FakeCanvas canvas = new FakeCanvas(200, 200);

		// 200/20 = 10 ; 200/10 = 20 -> min = 10
		assertEquals(10, vp.scale(canvas), DELTA);
	}

	@Test
	void toPixelX_toPixelY_convertissent_les_coordonnees_monde() {
		ViewPort vp = new ViewPort(20, 10, 100, 80);
		FakeCanvas canvas = new FakeCanvas(200, 200);
		vp.MoveTo(5, 2);

		double s = vp.scale(canvas);
		int offsetX = (int) ((canvas.getWidth() - vp.getWidth_cm() * s) / 2.0);
		int offsetY = (int) ((canvas.getHeight() - vp.getHeight_cm() * s) / 2.0);

		assertEquals(offsetX + (int) ((10 - 5) * s), vp.toPixelX(canvas, 10));
		assertEquals(offsetY + (int) ((6 - 2) * s), vp.toPixelY(canvas, 6));
	}

	// ─── fill / clip ────────────────────────────────────────────────────────

	@Test
	void fill_remplit_la_zone_du_monde() {
		ViewPort vp = new ViewPort(20, 10, 100, 80);
		FakeCanvas canvas = new FakeCanvas(200, 200);
		FakeGraphics g = new FakeGraphics();

		vp.fill(canvas, g);

		assertEquals(1, g.filledRects.size());
		double s = vp.scale(canvas);
		int offsetX = (int) ((canvas.getWidth() - vp.getWidth_cm() * s) / 2.0);
		int offsetY = (int) ((canvas.getHeight() - vp.getHeight_cm() * s) / 2.0);
		FakeGraphics.Rect rect = g.filledRects.get(0);
		assertEquals(offsetX, rect.x);
		assertEquals(offsetY, rect.y);
		assertEquals((int) (vp.getWidth_cm() * s), rect.w);
		assertEquals((int) (vp.getHeight_cm() * s), rect.h);
	}

	@Test
	void clip_definit_le_clip_sur_la_zone_du_monde() {
		ViewPort vp = new ViewPort(20, 10, 100, 80);
		FakeCanvas canvas = new FakeCanvas(200, 200);
		FakeGraphics g = new FakeGraphics();

		vp.clip(canvas, g);

		assertEquals(1, g.clips.size());
	}
}
