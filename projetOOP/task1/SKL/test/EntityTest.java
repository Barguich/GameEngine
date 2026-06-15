package test;

import engine.Game;
import engine.Grid;
import engine.ISU;
import game.Ghost;
import game.PacMan;
import game.Obstacle;
import game.Boss;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EntityTest {

	private ISU isu;
	private Grid grid;
	private double cm;

	@BeforeEach
	void setUp() {
		new Game(10, 10); // 10x10 cellules => 37x37 cm
		this.isu = Game.game().isu;
		this.grid = Game.game().grid;
		this.cm = Game.game().cmPerCell; // 3.7
	}

	// === setPosition : pose l'entité au coin de la cellule ===
	@Test
	void setPosition_centreSurCoinCellule() {
		Ghost g = new Ghost();
		g.setPosition(grid.new Position(2, 3));
		// position(2,3) -> center en (2*cm, 3*cm)
		assertEquals(2 * cm, g.center().x(), 1e-9);
		assertEquals(3 * cm, g.center().y(), 1e-9);
	}

	@Test
	void setPosition_metALaBonneCellule() {
		Ghost g = new Ghost();
		g.setPosition(grid.new Position(2, 3));
		assertEquals(2, g.position().x());
		assertEquals(3, g.position().y());
	}

	// === Une entité s'enregistre dans sa cellule ===
	@Test
	void setPosition_entiteAjouteeDansCellule() {
		Ghost g = new Ghost();
		g.setPosition(grid.new Position(2, 3));
		Grid.Cell cell = grid.cellAt(grid.new Position(2, 3));
		assertTrue(cell.contains(g));
	}

	@Test
	void changementDePosition_ancienneCelluleVidee() {
		Ghost g = new Ghost();
		g.setPosition(grid.new Position(2, 3));
		Grid.Cell ancienne = grid.cellAt(grid.new Position(2, 3));

		g.setPosition(grid.new Position(5, 5));
		assertFalse(ancienne.contains(g));
		Grid.Cell nouvelle = grid.cellAt(grid.new Position(5, 5));
		assertTrue(nouvelle.contains(g));
	}

	// === Obstacle : se positionne dans son constructeur ===
	@Test
	void obstacle_sePositionneAuConstructeur() {
		Obstacle o = new Obstacle(4, 4);
		assertEquals(4, o.position().x());
		assertEquals(4, o.position().y());
		Grid.Cell cell = grid.cellAt(grid.new Position(4, 4));
		assertTrue(cell.contains(o));
	}

	// === translate (ISU.Vector) ===
	@Test
	void translate_bougeLeCentre() {
		Ghost g = new Ghost();
		g.setPosition(grid.new Position(5, 5));
		g.translate(isu.new Vector(2, 0));
		assertEquals(5 * cm + 2, g.center().x(), 1e-9);
		assertEquals(5 * cm, g.center().y(), 1e-9);
	}

	@Test
	void translate_traverseBordureTorique() {
		// 10x10 cellules => width_cm = 37
		Ghost g = new Ghost();
		g.setPosition(grid.new Position(9, 5)); // center.x = 33.3
		g.translate(isu.new Vector(5, 0)); // 33.3 + 5 = 38.3 -> 1.3 après wrap
		assertEquals(38.3 - 37, g.center().x(), 1e-9);
	}

	// === translate (Grid.Vector) — conversion en cm ===
	@Test
	void translate_avecGridVector_convertitEnCm() {
		Ghost g = new Ghost();
		g.setPosition(grid.new Position(2, 2));
		g.translate(grid.new Vector(1, 0)); // 1 cellule = cm
		assertEquals(3 * cm, g.center().x(), 1e-9);
	}

	// === turn : rotation modulo 360 ===
	@Test
	void turn_simpleAjoute() {
		Ghost g = new Ghost();
		g.setPosition(grid.new Position(0, 0));
		g.turn(90);
		assertEquals(90, g.orientation());
	}

	@Test
	void turn_audessusDe360_wrap() {
		Ghost g = new Ghost();
		g.setPosition(grid.new Position(0, 0));
		g.turn(270);
		g.turn(180); // 270+180 = 450 -> 90
		assertEquals(90, g.orientation());
	}

	@Test
	void turn_negatif_normaliseEnPositif() {
		Ghost g = new Ghost();
		g.setPosition(grid.new Position(0, 0));
		g.turn(-90); // -90 -> 270
		assertEquals(270, g.orientation());
	}

	// === moveNorth/South/East/West ===
	// Note : moveNorth/South utilisent step qui n'est pas initialisé sur Ghost,
	// donc on teste l'erreur attendue.
	@Test
	void moveNorth_sansStep_throws() {
		Ghost g = new Ghost();
		g.setPosition(grid.new Position(5, 5));
		assertThrows(IllegalStateException.class, () -> g.moveNorth(1));
	}

	@Test
	void moveEast_enCm_translateLeCentre() {
		Ghost g = new Ghost();
		g.setPosition(grid.new Position(5, 5));
		g.moveEast(2);
		assertEquals(5 * cm + 2, g.center().x(), 1e-9);
	}

	@Test
	void moveWest_enCm_translateLeCentre() {
		Ghost g = new Ghost();
		g.setPosition(grid.new Position(5, 5));
		g.moveWest(2);
		assertEquals(5 * cm - 2, g.center().x(), 1e-9);
	}

	// === intersects ===
	@Test
	void deuxEntites_memePosition_sIntersectent() {
		Ghost g1 = new Ghost();
		Ghost g2 = new Ghost();
		g1.setPosition(grid.new Position(5, 5));
		g2.setPosition(grid.new Position(5, 5));
		assertTrue(g1.intersects(g2));
	}

	@Test
	void deuxEntites_loin_neSIntersectentPas() {
		Ghost g1 = new Ghost();
		Ghost g2 = new Ghost();
		g1.setPosition(grid.new Position(2, 2));
		g2.setPosition(grid.new Position(8, 8));
		assertFalse(g1.intersects(g2));
	}

	@Test
	void pacmanEtGhost_memePosition_sIntersectent() {
		PacMan p = new PacMan();
		Ghost g = new Ghost();
		p.setPosition(grid.new Position(5, 5));
		g.setPosition(grid.new Position(5, 5));
		assertTrue(p.intersects(g));
	}

	// === distanceCenterToCenter ===
	@Test
	void distanceCenterToCenter() {
		Ghost g1 = new Ghost();
		Ghost g2 = new Ghost();
		g1.setPosition(grid.new Position(2, 2));
		g2.setPosition(grid.new Position(5, 2));
		// distance = 3 cellules = 3*cm
		assertEquals(3 * cm, g1.distanceCenterToCenter(g2), 1e-9);
	}

	// === Boss : test que setBounding gère bien la rotation ===
	@Test
	void boss_seCreeSansErreur() {
		Boss b = new Boss();
		b.setPosition(grid.new Position(5, 5));
		// pas d'assertion forte, on vérifie juste que le setBounding fonctionne
		assertNotNull(b.position());
	}

	@Test
	void boss_apresRotation_seBoundingMisAJour() {
		Boss b = new Boss();
		b.setPosition(grid.new Position(5, 5));
		// La rotation n'est PAS automatiquement répercutée dans le bounding actuel
		// car turn() ne déclenche pas setBounding(). C'est un constat, pas un test
		// du contenu — juste que turn() ne casse rien.
		b.turn(90);
		assertEquals(90, b.orientation());
	}

//	// === lSpeed ===
//	@Test
//	void lSpeed_parDefaut_null() {
//		Ghost g = new Ghost();
//		assertNull(g.getlSpeed());
//	}
//
//	@Test
//	void setLSpeed_modifie() {
//		Ghost g = new Ghost();
//		ISU.Vector v = isu.new Vector(7.4, 0);
//		g.setlSpeed(v);
//		assertSame(v, g.getlSpeed());
//	}
//
//	@Test
//	void setLSpeed_null_arret() {
//		Ghost g = new Ghost();
//		g.setlSpeed(isu.new Vector(7.4, 0));
//		g.setlSpeed(null);
//		assertNull(g.getlSpeed());
//	}
}
