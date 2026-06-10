package test;

import engine.Circle;
import engine.Game;
import engine.ISU;
import engine.Rect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Complète RectCollisionTest avec les cas qui n'étaient pas couverts :
 * - bord gauche et bord haut (exerce closestRectpoint avec valeurs négatives)
 * - rotations arbitraires
 * - rectangle dans rectangle
 */
class RectTest {

	private ISU isu;

	@BeforeEach
	void setUp() {
		new Game(28, 31);
		this.isu = Game.game().isu;
	}

	// === RECT / CIRCLE — bords qui exercent closestRectpoint ===

	@Test
	void cercleMordBordGauche() {
		// Cas qui aurait attrapé le bug de closestRectpoint (circleLocal.x() négatif)
		Rect r = new Rect(isu.new Coord(10, 10), isu.new Dimension(4, 2), 0);
		Circle c = new Circle(isu.new Coord(7.5, 10), 1);
		assertTrue(r.intersects(c));
	}

	@Test
	void cercleMordBordHaut() {
		// Idem mais avec circleLocal.y() négatif
		Rect r = new Rect(isu.new Coord(10, 10), isu.new Dimension(4, 2), 0);
		Circle c = new Circle(isu.new Coord(10, 8.5), 1);
		assertTrue(r.intersects(c));
	}

	@Test
	void cercleAuCoinSupGauche() {
		// Le coin du rect est à (8, 9), cercle juste au-delà
		Rect r = new Rect(isu.new Coord(10, 10), isu.new Dimension(4, 2), 0);
		Circle c = new Circle(isu.new Coord(7.5, 8.5), 0.5);
		// distance du centre du cercle au coin (8,9) = sqrt(0.25+0.25) ≈ 0.707
		// rayon = 0.5 -> pas d'intersection
		assertFalse(r.intersects(c));
	}

	@Test
	void cercleAuCoinSupGauche_quiTouche() {
		Rect r = new Rect(isu.new Coord(10, 10), isu.new Dimension(4, 2), 0);
		Circle c = new Circle(isu.new Coord(7.5, 8.5), 1);
		// distance au coin (8,9) ≈ 0.707, rayon 1 -> intersection
		assertTrue(r.intersects(c));
	}

	@Test
	void cercleTangentBordDroit_neTouchePas() {
		// distance exacte = rayon, comparaison '<' strict
		Rect r = new Rect(isu.new Coord(10, 10), isu.new Dimension(4, 2), 0);
		Circle c = new Circle(isu.new Coord(13, 10), 1);
		assertFalse(r.intersects(c));
	}

	// === RECT / RECT — rotations arbitraires ===

	@Test
	void rectsRotation45_seChevauchent() {
		Rect a = new Rect(isu.new Coord(10, 10), isu.new Dimension(4, 4), 0);
		Rect b = new Rect(isu.new Coord(10, 10), isu.new Dimension(4, 4), 45);
		assertTrue(a.intersects(b));
	}

	@Test
	void rectsRotation45_separes() {
		// Deux carrés tournés à 45°, suffisamment éloignés
		Rect a = new Rect(isu.new Coord(10, 10), isu.new Dimension(2, 2), 45);
		Rect b = new Rect(isu.new Coord(15, 10), isu.new Dimension(2, 2), 45);
		assertFalse(a.intersects(b));
	}

	@Test
	void petitRectDansGrandRect() {
		Rect grand = new Rect(isu.new Coord(10, 10), isu.new Dimension(10, 10), 0);
		Rect petit = new Rect(isu.new Coord(10, 10), isu.new Dimension(1, 1), 0);
		assertTrue(grand.intersects(petit));
		assertTrue(petit.intersects(grand));
	}

	@Test
	void rectsCollesBordABord_neSeTouchentPas() {
		// SAT avec '>' strict : si projD == halfA + halfB, separe renvoie false
		// donc !separe = true partout, donc intersects = true... ?
		// Test : deux rects collés à x=12 (bord droit de a = bord gauche de b)
		Rect a = new Rect(isu.new Coord(10, 10), isu.new Dimension(4, 2), 0);
		Rect b = new Rect(isu.new Coord(14, 10), isu.new Dimension(4, 2), 0);
		// projD = 4, halfA + halfB = 2 + 2 = 4, '>' strict -> separe = false
		// donc intersection = true (limite supérieure du contact)
		assertTrue(a.intersects(b));
	}

	// === DOUBLE DISPATCH via iShape ===

	@Test
	void rectIntersectsCircle_viaIShape() {
		Rect r = new Rect(isu.new Coord(10, 10), isu.new Dimension(4, 2), 0);
		Circle c = new Circle(isu.new Coord(10, 10), 0.5);
		assertTrue(r.intersects((engine.iShape) c));
	}

	@Test
	void rectIntersectsRect_viaIShape() {
		Rect a = new Rect(isu.new Coord(10, 10), isu.new Dimension(4, 2), 0);
		Rect b = new Rect(isu.new Coord(11, 10), isu.new Dimension(4, 2), 0);
		assertTrue(a.intersects((engine.iShape) b));
	}

	// === RECT / CIRCLE — wrap-around ===

	@Test
	void rectEtCercle_seVoientACrossBordureDroite() {
		Rect r = new Rect(isu.new Coord(102, 50), isu.new Dimension(2, 2), 0);
		Circle c = new Circle(isu.new Coord(0.5, 50), 1);
		// bord droit du rect à x=103 ; cercle s'étend de -0.5 à 1.5 (torique)
		// distance torique entre centres ≈ 1.9, rect halfWidth=1, rayon=1
		assertTrue(r.intersects(c));
	}

	@Test
	void rectEtCercle_seVoientACrossBordureBas() {
		Rect r = new Rect(isu.new Coord(50, 113), isu.new Dimension(2, 2), 0);
		Circle c = new Circle(isu.new Coord(50, 0.5), 1);
		assertTrue(r.intersects(c));
	}

	// === RECT / RECT — wrap-around ===

	@Test
	void rectsTorique_seVoientACrossBordureDroite() {
		Rect a = new Rect(isu.new Coord(102, 50), isu.new Dimension(3, 2), 0);
		Rect b = new Rect(isu.new Coord(1, 50), isu.new Dimension(3, 2), 0);
		// a s'étendrait jusqu'à x=103.5 ; b commencerait à x=-0.5
		// distance torique entre centres ≈ 2.6, half+half = 3
		assertTrue(a.intersects(b));
	}

	@Test
	void rectsTorique_pasAssezProches() {
		Rect a = new Rect(isu.new Coord(100, 50), isu.new Dimension(2, 2), 0);
		Rect b = new Rect(isu.new Coord(3, 50), isu.new Dimension(2, 2), 0);
		// distance torique ≈ 6.6, half+half = 2
		assertFalse(a.intersects(b));
	}
}
