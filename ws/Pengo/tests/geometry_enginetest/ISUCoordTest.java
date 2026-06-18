package geometry_enginetest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import engine.Game;
import geometry.Grid;
import geometry.ISU;

/**
 * ISU.Coord en cm, sur un plateau toroïdal (torus activé par défaut dans Game).
 * Game 20×20 → width_cm = height_cm = 74. On reste sur des coordonnées centrales
 * pour éviter tout repli de tore et garder des assertions déterministes.
 */
class ISUCoordTest {

	private static final double DELTA = 1e-9;

	@BeforeEach
	void setUp() {
		new Game(20, 20);
	}

	@Test
	void distanceTo_pythagore_sans_repli() {
		ISU isu = Game.isu();
		ISU.Coord a = isu.new Coord(10, 10);
		ISU.Coord b = isu.new Coord(13, 14);
		assertEquals(5.0, a.distanceTo(b), DELTA); // 3-4-5
	}

	@Test
	void mkVectorToward_donne_le_vecteur_entre_deux_coords() {
		ISU isu = Game.isu();
		ISU.Coord a = isu.new Coord(10, 10);
		ISU.Coord b = isu.new Coord(13, 14);
		ISU.Vector v = a.mkVectorToward(b);
		assertEquals(3, v.x(), DELTA);
		assertEquals(4, v.y(), DELTA);
	}

	@Test
	void translate_decale_la_coordonnee() {
		ISU isu = Game.isu();
		ISU.Coord c = isu.new Coord(10, 10);
		c.translate(isu.new Vector(5, 5));
		assertEquals(15, c.x(), DELTA);
		assertEquals(15, c.y(), DELTA);
	}

	@Test
	void mkCopy_est_independante_de_l_originale() {
		ISU isu = Game.isu();
		ISU.Coord original = isu.new Coord(10, 10);
		ISU.Coord copy = original.mkCopy();
		copy.translate(isu.new Vector(5, 0));
		assertEquals(15, copy.x(), DELTA);
		assertEquals(10, original.x(), DELTA); // l'originale n'a pas bougé
	}

	@Test
	void toGridPosition_convertit_en_cellule_par_floor() {
		ISU isu = Game.isu();
		ISU.Coord c = isu.new Coord(10, 10);
		// floor(10 / 3.7) = floor(2.70...) = 2
		Grid.Position p = c.toGridPosition();
		assertEquals(2, p.x());
		assertEquals(2, p.y());
	}
}
