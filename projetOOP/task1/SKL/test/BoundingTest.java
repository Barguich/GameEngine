package test;

import engine.Bounding;
import engine.Circle;
import engine.Game;
import engine.ISU;
import engine.Rect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BoundingTest {

	private ISU isu;

	@BeforeEach
	void setUp() {
		new Game(28, 31);
		this.isu = Game.game().isu;
	}

	// === BOUNDING VIDE ===

	@Test
	void boundingVide_nIntersecteRien() {
		Bounding b = new Bounding();
		Circle c = new Circle(isu.new Coord(10, 10), 1);
		assertFalse(b.intersects(c));
	}

	@Test
	void boundingVide_vsBoundingVide() {
		Bounding a = new Bounding();
		Bounding b = new Bounding();
		assertFalse(a.intersects(b));
	}

	// === BOUNDING vs SHAPE ===

	@Test
	void boundingUnShape_intersecteSiShapeIntersecte() {
		Bounding b = new Bounding();
		b.add(new Circle(isu.new Coord(10, 10), 2));

		Circle proche = new Circle(isu.new Coord(11, 10), 1);
		Circle loin = new Circle(isu.new Coord(50, 50), 1);

		assertTrue(b.intersects(proche));
		assertFalse(b.intersects(loin));
	}

	@Test
	void boundingPlusieursShapes_intersecteDesQuUneIntersecte() {
		// la bounding contient un cercle loin et un rect proche
		// la cible touche le rect mais pas le cercle
		Bounding b = new Bounding();
		b.add(new Circle(isu.new Coord(50, 50), 1));
		b.add(new Rect(isu.new Coord(10, 10), isu.new Dimension(4, 2), 0));

		Circle cible = new Circle(isu.new Coord(11, 10), 0.5);
		assertTrue(b.intersects(cible));
	}

	@Test
	void boundingPlusieursShapes_aucuneNIntersecte() {
		Bounding b = new Bounding();
		b.add(new Circle(isu.new Coord(50, 50), 1));
		b.add(new Rect(isu.new Coord(10, 10), isu.new Dimension(2, 2), 0));

		Circle cible = new Circle(isu.new Coord(25, 25), 0.5);
		assertFalse(b.intersects(cible));
	}

	// === BOUNDING vs BOUNDING ===

	@Test
	void deuxBoundings_unePaireIntersecte() {
		// a contient un cercle en (10,10), b contient un cercle en (10.5,10)
		Bounding a = new Bounding();
		a.add(new Circle(isu.new Coord(10, 10), 1));
		a.add(new Circle(isu.new Coord(50, 50), 1));

		Bounding b = new Bounding();
		b.add(new Circle(isu.new Coord(10.5, 10), 1));
		b.add(new Circle(isu.new Coord(70, 70), 1));

		assertTrue(a.intersects(b));
		assertTrue(b.intersects(a)); // symétrie
	}

	@Test
	void deuxBoundings_aucunePaireIntersecte() {
		Bounding a = new Bounding();
		a.add(new Circle(isu.new Coord(10, 10), 1));

		Bounding b = new Bounding();
		b.add(new Circle(isu.new Coord(50, 50), 1));

		assertFalse(a.intersects(b));
		assertFalse(b.intersects(a));
	}

	// === MIX RECT / CIRCLE dans une même Bounding ===

	@Test
	void boundingMixte_rectEtCircle() {
		Bounding b = new Bounding();
		b.add(new Rect(isu.new Coord(10, 10), isu.new Dimension(4, 2), 0));
		b.add(new Circle(isu.new Coord(20, 20), 1));

		// touche le rect
		assertTrue(b.intersects(new Circle(isu.new Coord(10, 10), 0.5)));
		// touche le circle
		assertTrue(b.intersects(new Circle(isu.new Coord(20, 20), 0.5)));
		// touche rien
		assertFalse(b.intersects(new Circle(isu.new Coord(50, 50), 0.5)));
	}

	// === BOUNDING — wrap-around ===

	@Test
	void boundingsTorique_seVoientACrossBordure() {
		Bounding a = new Bounding();
		a.add(new Circle(isu.new Coord(102, 50), 1));

		Bounding b = new Bounding();
		b.add(new Circle(isu.new Coord(0.6, 50), 1));

		assertTrue(a.intersects(b));
	}
}
