package Tests;

import engine.Circle;
import engine.Game;
import engine.ISU;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CircleCollisionTest {

	private ISU isu;

	@BeforeEach
	void setUp() {
		new Game(28, 31);
		this.isu = Game.isu();
	}

	// === CIRCLE / CIRCLE ===

	@Test
	void cerclesQuiSeChevauchent() {
		Circle a = new Circle(isu.new Coord(10, 10), 2);
		Circle b = new Circle(isu.new Coord(11, 10), 2);
		assertTrue(a.intersects(b));
	}

	@Test
	void cerclesLoin() {
		Circle a = new Circle(isu.new Coord(10, 10), 1);
		Circle b = new Circle(isu.new Coord(50, 50), 1);
		assertFalse(a.intersects(b));
	}

	@Test
	void cerclesConcentriques_petitDansGrand() {
		Circle grand = new Circle(isu.new Coord(10, 10), 5);
		Circle petit = new Circle(isu.new Coord(10, 10), 1);
		assertTrue(grand.intersects(petit));
		assertTrue(petit.intersects(grand));
	}

	@Test
	void cerclesTangentsExt_neSeTouchentPas() {
		// distance entre centres = 4, somme des rayons = 4 (tangence exacte)
		// le code utilise '<' strict, donc tangence == pas d'intersection
		Circle a = new Circle(isu.new Coord(10, 10), 2);
		Circle b = new Circle(isu.new Coord(14, 10), 2);
		assertFalse(a.intersects(b));
	}

	@Test
	void cerclesQuiSeChevauchentTrèsLégèrement() {
		// distance 3.9, somme rayons 4
		Circle a = new Circle(isu.new Coord(10, 10), 2);
		Circle b = new Circle(isu.new Coord(13.9, 10), 2);
		assertTrue(a.intersects(b));
	}

	// === CIRCLE via interface iShape ===

	@Test
	void intersectsViaiShape_doubleDispatch() {
		// vérifie que le double dispatch fonctionne : Circle.intersects(iShape)
		// délègue à shape.intersects(Circle)
		Circle a = new Circle(isu.new Coord(10, 10), 2);
		Circle b = new Circle(isu.new Coord(11, 10), 2);
		assertTrue(a.intersects((engine.iShape) b));
	}

	// === CIRCLE / CIRCLE — wrap-around du tore ===
	// width_cm = 28 * 3.7 = 103.6 ; height_cm = 31 * 3.7 = 114.7

	@Test
	void cerclesTorique_seVoientACrossBordureDroite() {
		// un cercle proche du bord droit, l'autre proche du bord gauche
		// distance euclidienne = 102, distance torique ≈ 1.6
		Circle a = new Circle(isu.new Coord(103, 50), 1.2);
		Circle b = new Circle(isu.new Coord(0.5, 50), 1.2);
		assertTrue(a.intersects(b));
	}

	@Test
	void cerclesTorique_seVoientACrossBordureBas() {
		// pareil mais sur l'axe Y (height_cm = 114.7)
		Circle a = new Circle(isu.new Coord(50, 113.5), 1.2);
		Circle b = new Circle(isu.new Coord(50, 0.5), 1.2);
		assertTrue(a.intersects(b));
	}

	@Test
	void cerclesTorique_seVoientACrossCoinDiagonal() {
		// un cercle en coin haut-gauche, l'autre en coin bas-droit
		Circle a = new Circle(isu.new Coord(0.5, 0.5), 1);
		Circle b = new Circle(isu.new Coord(103, 114), 1);
		// distance torique : dx ≈ 1.1, dy ≈ 1.2, total ≈ 1.6 ; somme rayons = 2
		assertTrue(a.intersects(b));
	}

	@Test
	void cerclesTorique_pasAssezProchesMemeAvecRaccourci() {
		// proches du bord mais pas assez gros pour se rejoindre
		Circle a = new Circle(isu.new Coord(100, 50), 0.5);
		Circle b = new Circle(isu.new Coord(2, 50), 0.5);
		// distance torique ≈ 5.6, somme rayons = 1
		assertFalse(a.intersects(b));
	}
}
