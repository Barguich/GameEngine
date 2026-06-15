package test;

import engine.Game;
import engine.Grid;
import engine.ISU;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ISUTest {

	private ISU isu;
	private double width_cm;
	private double height_cm;

	@BeforeEach
	void setUp() {
		new Game(10, 10);
		this.isu = Game.game().isu;
		this.width_cm = 10 * 3.7;
		this.height_cm = 10 * 3.7;
	}

	@Test
	void dimension_dansLaCarte_inchange() {
		ISU.Dimension d = isu.new Dimension(5, 5);
		assertEquals(5, d.x(), 1e-9);
		assertEquals(5, d.y(), 1e-9);
	}

	@Test
	void dimension_negatif_normalise() {
		ISU.Dimension d = isu.new Dimension(-1, -1);
		assertEquals(width_cm - 1, d.x(), 1e-9);
		assertEquals(height_cm - 1, d.y(), 1e-9);
	}

	@Test
	void dimension_audessusPerimetre_normalise() {
		ISU.Dimension d = isu.new Dimension(width_cm + 2, 5);
		assertEquals(2, d.x(), 1e-9);
	}

	@Test
	void coord_negatif_normalise() {
		ISU.Coord c = isu.new Coord(-1, -1);
		assertEquals(width_cm - 1, c.x(), 1e-9);
		assertEquals(height_cm - 1, c.y(), 1e-9);
	}

	@Test
	void vector_negatif_preserve() {
		ISU.Vector v = isu.new Vector(-1, -1);
		assertEquals(-1, v.x(), 1e-9);
		assertEquals(-1, v.y(), 1e-9);
	}

	@Test
	void vector_audessusPerimetre_preserve() {
		ISU.Vector v = isu.new Vector(width_cm + 10, 0);
		assertEquals(width_cm + 10, v.x(), 1e-9);
	}

	@Test
	void mkVectorToward_direct() {
		ISU.Coord a = isu.new Coord(2, 5);
		ISU.Coord b = isu.new Coord(8, 5);
		ISU.Vector v = a.mkVectorToward(b);
		assertEquals(6, v.x(), 1e-9);
		assertEquals(0, v.y(), 1e-9);
	}

	@Test
	void mkVectorToward_passeBordure_estPlusCourt() {
		ISU.Coord a = isu.new Coord(1, 5);
		ISU.Coord b = isu.new Coord(width_cm - 1, 5);
		ISU.Vector v = a.mkVectorToward(b);
		assertEquals(-2, v.x(), 1e-9);
	}

	@Test
	void translate_traverseBordure_normalise() {
		ISU.Coord c = isu.new Coord(width_cm - 1, 5);
		c.translate(isu.new Vector(2, 0));
		assertEquals(1, c.x(), 1e-9);
	}

	@Test
	void distance_traverseBordure() {
		ISU.Coord a = isu.new Coord(1, 5);
		ISU.Coord b = isu.new Coord(width_cm - 1, 5);
		assertEquals(2, a.distanceTo(b), 1e-9);
	}

	@Test
	void vector_dot() {
		ISU.Vector u = isu.new Vector(3, 4);
		ISU.Vector v = isu.new Vector(2, 1);
		assertEquals(10, u.dot(v), 1e-9);
	}

	@Test
	void vector_norm() {
		ISU.Vector v = isu.new Vector(3, 4);
		assertEquals(5, v.norm(), 1e-9);
	}

	@Test
	void vector_turn90_xVersY() {
		ISU.Vector v = isu.new Vector(1, 0);
		v.turn(90);
		assertEquals(0, v.x(), 1e-9);
		assertEquals(1, v.y(), 1e-9);
	}

	@Test
	void dimension_equals_memesValeurs() {
		ISU.Dimension a = isu.new Dimension(3, 4);
		ISU.Dimension b = isu.new Dimension(3, 4);
		assertEquals(a, b);
	}

	@Test
	void dimension_equals_valeursDifferentes() {
		ISU.Dimension a = isu.new Dimension(3, 4);
		ISU.Dimension b = isu.new Dimension(3, 5);
		assertNotEquals(a, b);
	}

	@Test
	void dimension_equals_apresNormalisation() {
		ISU.Dimension a = isu.new Dimension(-1, -1);
		ISU.Dimension b = isu.new Dimension(width_cm - 1, height_cm - 1);
		assertEquals(a, b);
	}

	@Test
	void dimension_equiv_memesValeurs() {
		ISU.Dimension a = isu.new Dimension(3, 4);
		ISU.Dimension b = isu.new Dimension(3, 4);
		assertTrue(a.equiv(b));
	}

	@Test
	void dimension_equiv_null_renvoieFalse() {
		ISU.Dimension a = isu.new Dimension(3, 4);
		assertFalse(a.equiv(null));
	}

	@Test
	void dimension_setxy_modifie() {
		ISU.Dimension d = isu.new Dimension(3, 4);
		d.setxy(7, 8);
		assertEquals(7, d.x(), 1e-9);
		assertEquals(8, d.y(), 1e-9);
	}

	@Test
	void dimension_normalize_apresSetxy() {
		ISU.Dimension d = isu.new Dimension(3, 4);
		d.setxy(-1, -1);
		d.normalize();
		assertEquals(width_cm - 1, d.x(), 1e-9);
		assertEquals(height_cm - 1, d.y(), 1e-9);
	}

	@Test
	void dimension_mkVector_memesComposantes() {
		ISU.Dimension d = isu.new Dimension(3, 4);
		ISU.Vector v = d.mkVector();
		assertEquals(3, v.x(), 1e-9);
		assertEquals(4, v.y(), 1e-9);
	}

	@Test
	void dimension_mkScaledVector_facteurUniforme() {
		ISU.Dimension d = isu.new Dimension(3, 4);
		ISU.Vector v = d.mkScaledVector(2);
		assertEquals(6, v.x(), 1e-9);
		assertEquals(8, v.y(), 1e-9);
	}

	@Test
	void dimension_mkScaledVector_facteurAsymetrique() {
		ISU.Dimension d = isu.new Dimension(3, 4);
		ISU.Vector v = d.mkScaledVector(2, 0.5);
		assertEquals(6, v.x(), 1e-9);
		assertEquals(2, v.y(), 1e-9);
	}

	@Test
	void dimension_mkScaledVector_neNormalisePas() {
		ISU.Dimension d = isu.new Dimension(5, 0);
		ISU.Vector v = d.mkScaledVector(100);
		assertEquals(500, v.x(), 1e-9);
	}

	@Test
	void coord_mkCopy_memesValeurs_objetDistinct() {
		ISU.Coord a = isu.new Coord(3, 4);
		ISU.Coord b = a.mkCopy();
		assertEquals(a.x(), b.x(), 1e-9);
		assertEquals(a.y(), b.y(), 1e-9);
		assertNotSame(a, b);
	}

	@Test
	void coord_mkCopy_indeépendant() {
		ISU.Coord a = isu.new Coord(3, 4);
		ISU.Coord b = a.mkCopy();
		a.translate(isu.new Vector(1, 0));
		assertEquals(3, b.x(), 1e-9); // b inchangé
	}

	@Test
	void coord_mkTranslated_renvoieNouveau() {
		ISU.Coord a = isu.new Coord(3, 4);
		ISU.Coord b = a.mkTranslated(isu.new Vector(2, 1));
		assertEquals(3, a.x(), 1e-9); // a inchangé
		assertEquals(5, b.x(), 1e-9);
		assertEquals(5, b.y(), 1e-9);
	}

	@Test
	void coord_rotation90_autourOrigine() {
		ISU.Coord c = isu.new Coord(3, 0);
		c.rotation(90);
		assertEquals(0, c.x(), 1e-9);
		assertEquals(3, c.y(), 1e-9);
	}

	@Test
	void coord_rotation180() {
		ISU.Coord c = isu.new Coord(3, 0);
		c.rotation(180);
		assertEquals(width_cm - 3, c.x(), 1e-9);
		assertEquals(0, c.y(), 1e-9);
	}

	@Test
	void coord_rotateAround_90() {
		ISU.Coord c = isu.new Coord(5, 5);
		ISU.Coord centre = isu.new Coord(5, 0);
		c.rotateAround(centre, 90);
		assertEquals(0, c.x(), 1e-9);
		assertEquals(0, c.y(), 1e-9);
	}

	@Test
	void coord_rotateAround_centreInchange() {
		ISU.Coord c = isu.new Coord(5, 5);
		ISU.Coord centre = isu.new Coord(5, 5);
		c.rotateAround(centre, 137);
		assertEquals(5, c.x(), 1e-9);
		assertEquals(5, c.y(), 1e-9);
	}

	@Test
	void coord_toGridPosition() {
		ISU.Coord c = isu.new Coord(7.4, 12);
		Grid.Position p = c.toGridPosition();
		assertEquals(2, p.x());
		assertEquals(3, p.y());
	}

	@Test
	void vector_add() {
		ISU.Vector v = isu.new Vector(3, 4);
		v.add(isu.new Vector(1, 2));
		assertEquals(4, v.x(), 1e-9);
		assertEquals(6, v.y(), 1e-9);
	}

	@Test
	void vector_scale_facteurUniforme() {
		ISU.Vector v = isu.new Vector(3, 4);
		v.scale(2);
		assertEquals(6, v.x(), 1e-9);
		assertEquals(8, v.y(), 1e-9);
	}

	@Test
	void vector_scale_facteurAsymetrique() {
		ISU.Vector v = isu.new Vector(3, 4);
		v.scale(2, 0.5);
		assertEquals(6, v.x(), 1e-9);
		assertEquals(2, v.y(), 1e-9);
	}

	@Test
	void vector_scale_negatif_inverseDirection() {
		ISU.Vector v = isu.new Vector(3, 4);
		v.scale(-1);
		assertEquals(-3, v.x(), 1e-9);
		assertEquals(-4, v.y(), 1e-9);
	}

	// === VECTOR : unity ===
	@Test
	void vector_unity_normeDevientUn() {
		ISU.Vector v = isu.new Vector(3, 4); // norme 5
		v.unity();
		assertEquals(0.6, v.x(), 1e-9);
		assertEquals(0.8, v.y(), 1e-9);
		assertEquals(1, v.norm(), 1e-9);
	}

	@Test
	void vector_unity_zero_resteZero() {
		// cas dégénéré : pas de division par zéro
		ISU.Vector v = isu.new Vector(0, 0);
		v.unity();
		assertEquals(0, v.x(), 1e-9);
		assertEquals(0, v.y(), 1e-9);
	}

	// === VECTOR : turn ===
	@Test
	void vector_turn_yVersMoinsX() {
		// (0, 1) tourné de 90° -> (-1, 0)
		ISU.Vector v = isu.new Vector(0, 1);
		v.turn(90);
		assertEquals(-1, v.x(), 1e-9);
		assertEquals(0, v.y(), 1e-9);
	}

	@Test
	void vector_turn_negatif_inverseSens() {
		// (1, 0) tourné de -90° -> (0, -1)
		ISU.Vector v = isu.new Vector(1, 0);
		v.turn(-90);
		assertEquals(0, v.x(), 1e-9);
		assertEquals(-1, v.y(), 1e-9);
	}

	@Test
	void vector_turn_360_revientAuPointDepart() {
		ISU.Vector v = isu.new Vector(3, 4);
		v.turn(360);
		assertEquals(3, v.x(), 1e-9);
		assertEquals(4, v.y(), 1e-9);
	}

	@Test
	void vector_turn_preserveNorme() {
		ISU.Vector v = isu.new Vector(3, 4);
		double avant = v.norm();
		v.turn(73);
		assertEquals(avant, v.norm(), 1e-9);
	}
}
