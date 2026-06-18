package model_enginetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import engine.Game;
import geometry.Grid;
import geometry.ISU;
import model.Entity;

/**
 * Entity : positionnement, déplacement, orientation, occupation de cellules
 * et collision. Game(20,20) -> grille 20x20, torus actif.
 */
class EntityTest {

	private static final double DELTA = 1e-9;

	@BeforeEach
	void setUp() {
		new Game(20, 20);
	}

	// ─── setPosition / setCoord ────────────────────────────────────────────

	@Test
	void setPosition_initialise_position_et_centre() {
		Entity e = new Entity("e");
		Grid.Position p = Game.grid().new Position(5, 5);

		e.setPosition(p);

		assertEquals(5, e.position().x());
		assertEquals(5, e.position().y());
		assertNotNull(e.center());
	}

	@Test
	void setPosition_centre_l_entite_au_milieu_de_la_cellule() {
		Entity e = new Entity("e");
		Grid.Position p = Game.grid().new Position(2, 3);

		e.setPosition(p);

		double cm = Game.game().cmPerCell;
		assertEquals((2 + .5) * cm, e.center().x(), DELTA);
		assertEquals((3 + .5) * cm, e.center().y(), DELTA);
	}

	@Test
	void setCoord_initialise_centre_et_deduit_la_position() {
		Entity e = new Entity("e");
		ISU.Coord c = Game.isu().new Coord(10, 10);

		e.setCoord(c);

		assertEquals(10, e.center().x(), DELTA);
		assertEquals(10, e.center().y(), DELTA);
		assertEquals(c.toGridPosition(), e.position());
	}

	@Test
	void setPosition_deploie_l_entite_dans_la_cellule() {
		Entity e = new Entity("e");
		Grid.Position p = Game.grid().new Position(5, 5);

		e.setPosition(p);

		Grid.Cell cell = Game.grid().cellAt(p);
		assertTrue(cell.contains(e));
	}

	// ─── setSize / bounding ─────────────────────────────────────────────────

	@Test
	void setSize_avant_position_ne_cree_pas_de_bounding() {
		Entity e = new Entity("e");
		e.setSize(Game.grid().new Dimension(1, 1));

		assertNull(e.bounding());
	}

	@Test
	void setSize_apres_position_cree_un_bounding() {
		Entity e = new Entity("e");
		e.setPosition(Game.grid().new Position(5, 5));
		e.setSize(Game.grid().new Dimension(1, 1));

		assertNotNull(e.bounding());
	}

	@Test
	void setPosition_apres_setSize_cree_aussi_un_bounding() {
		Entity e = new Entity("e");
		e.setSize(Game.grid().new Dimension(1, 1));
		e.setPosition(Game.grid().new Position(5, 5));

		assertNotNull(e.bounding());
	}

	@Test
	void box_sans_bounding_est_null() {
		Entity e = new Entity("e");
		assertNull(e.box());
	}

	@Test
	void box_avec_bounding_vide_est_null() {
		// setBounding() crée une Bounding vide : aucune forme, donc box() == null.
		Entity e = new Entity("e");
		e.setPosition(Game.grid().new Position(5, 5));
		e.setSize(Game.grid().new Dimension(1, 1));

		assertNotNull(e.bounding());
		assertNull(e.box());
	}

	// ─── orientation ─────────────────────────────────────────────────────

	@Test
	void orientation_par_defaut_est_zero() {
		Entity e = new Entity("e");
		assertEquals(0, e.orientation());
	}

	@Test
	void turnTo_normalise_l_angle_dans_0_360() {
		Entity e = new Entity("e");

		e.turnTo(450);
		assertEquals(90, e.orientation());

		e.turnTo(-90);
		assertEquals(270, e.orientation());
	}

	@Test
	void turn_ajoute_a_l_orientation_courante() {
		Entity e = new Entity("e");
		e.turnTo(10);

		e.turn(20);

		assertEquals(30, e.orientation());
	}

	// ─── step / moveNorth / moveSouth ───────────────────────────────────────

	@Test
	void moveNorth_sans_step_leve_IllegalStateException() {
		Entity e = new Entity("e");
		assertThrows(IllegalStateException.class, () -> e.moveNorth(1));
	}

	@Test
	void moveNorth_translate_vers_le_haut_y_decroissant() {
		Entity e = new Entity("e");
		e.setPosition(Game.grid().new Position(5, 5));

		double yBefore = e.center().y();
		e.moveNorth(1);

		double cm = Game.game().cmPerCell;
		assertEquals(yBefore - cm, e.center().y(), DELTA);
	}

	@Test
	void moveSouth_translate_vers_le_bas_y_croissant() {
		Entity e = new Entity("e");
		e.setPosition(Game.grid().new Position(5, 5));

		double yBefore = e.center().y();
		e.moveSouth(1);

		double cm = Game.game().cmPerCell;
		assertEquals(yBefore + cm, e.center().y(), DELTA);
	}

	@Test
	void moveEast_et_moveWest_translatent_en_x() {
		Entity e = new Entity("e");
		e.setPosition(Game.grid().new Position(5, 5));

		double xBefore = e.center().x();
		e.moveEast(2);
		assertEquals(xBefore + 2, e.center().x(), DELTA);

		e.moveWest(2);
		assertEquals(xBefore, e.center().x(), DELTA);
	}

	// ─── translate(Grid.Vector) / translate(ISU.Vector) ─────────────────────

	@Test
	void translate_grid_vector_sans_position_ne_fait_rien() {
		Entity e = new Entity("e");

		// pas de position définie : aucune exception, aucun effet.
		e.translate(Game.grid().new Vector(1, 0));

		assertNull(e.position());
	}

	@Test
	void translate_grid_vector_deplace_la_position() {
		Entity e = new Entity("e");
		e.setPosition(Game.grid().new Position(5, 5));

		e.translate(Game.grid().new Vector(1, 0));

		assertEquals(6, e.position().x());
		assertEquals(5, e.position().y());
	}

	@Test
	void translate_isu_vector_sans_centre_ne_fait_rien() {
		Entity e = new Entity("e");

		e.translate(Game.isu().new Vector(1, 0));

		assertNull(e.center());
	}

	@Test
	void translate_isu_vector_deplace_le_centre() {
		Entity e = new Entity("e");
		e.setPosition(Game.grid().new Position(5, 5));

		double xBefore = e.center().x();
		e.translate(Game.isu().new Vector(2, 0));

		assertEquals(xBefore + 2, e.center().x(), DELTA);
	}

	// ─── linear / angular speed ──────────────────────────────────────────

	@Test
	void setLinearSpeed_et_linearSpeed() {
		Entity e = new Entity("e");
		ISU.Vector v = Game.isu().new Vector(1, 2);

		e.setLinearSpeed(v);

		assertEquals(v, e.linearSpeed());
	}

	@Test
	void setAngularSpeed_et_angularSpeed() {
		Entity e = new Entity("e");

		e.setAngularSpeed(5.0);

		assertEquals(5.0, e.angularSpeed(), DELTA);
	}

	@Test
	void stop_sans_isu_remet_lSpeed_a_null_et_aSpeed_a_zero() {
		Entity e = new Entity("e");
		e.setAngularSpeed(5.0);

		e.stop();

		assertNull(e.linearSpeed());
		assertEquals(0, e.angularSpeed(), DELTA);
	}

	@Test
	void stop_avec_isu_remet_lSpeed_a_un_vecteur_nul() {
		Entity e = new Entity("e");
		e.setPosition(Game.grid().new Position(5, 5));
		e.setLinearSpeed(Game.isu().new Vector(3, 4));
		e.setAngularSpeed(5.0);

		e.stop();

		assertNotNull(e.linearSpeed());
		assertEquals(0, e.linearSpeed().x(), DELTA);
		assertEquals(0, e.linearSpeed().y(), DELTA);
		assertEquals(0, e.angularSpeed(), DELTA);
	}

	// ─── intersects / distanceCenterToCenter ────────────────────────────────

	@Test
	void intersects_avec_null_est_faux() {
		Entity e = new Entity("e");
		assertFalse(e.intersects(null));
	}

	@Test
	void intersects_sans_bounding_est_faux() {
		Entity a = new Entity("a");
		Entity b = new Entity("b");
		a.setPosition(Game.grid().new Position(5, 5));
		b.setPosition(Game.grid().new Position(5, 5));

		// aucune des deux entités n'a de bounding (setSize jamais appelé)
		assertFalse(a.intersects(b));
	}

	@Test
	void intersects_avec_bounding_vide_reste_faux() {
		// setBounding() crée une Bounding sans forme : intersects() est donc
		// toujours faux, même pour deux entités superposées.
		Entity a = new Entity("a");
		Entity b = new Entity("b");
		a.setPosition(Game.grid().new Position(5, 5));
		a.setSize(Game.grid().new Dimension(1, 1));
		b.setPosition(Game.grid().new Position(5, 5));
		b.setSize(Game.grid().new Dimension(1, 1));

		assertFalse(a.intersects(b));
	}

	@Test
	void distanceCenterToCenter_avec_null_est_infini() {
		Entity e = new Entity("e");
		e.setPosition(Game.grid().new Position(5, 5));

		assertEquals(Double.POSITIVE_INFINITY, e.distanceCenterToCenter(null));
	}

	@Test
	void distanceCenterToCenter_sans_centre_est_infini() {
		Entity a = new Entity("a");
		Entity b = new Entity("b");
		b.setPosition(Game.grid().new Position(5, 5));

		assertEquals(Double.POSITIVE_INFINITY, a.distanceCenterToCenter(b));
	}

	@Test
	void distanceCenterToCenter_calcule_la_distance_entre_centres() {
		Entity a = new Entity("a");
		Entity b = new Entity("b");
		a.setPosition(Game.grid().new Position(0, 0));
		b.setPosition(Game.grid().new Position(0, 0));

		assertEquals(0, a.distanceCenterToCenter(b), DELTA);
	}

	// ─── collision / done ────────────────────────────────────────────────

	@Test
	void collision_sans_stunt_ni_bot_arrete_l_entite() {
		Entity e = new Entity("e");
		e.setPosition(Game.grid().new Position(5, 5));
		e.setLinearSpeed(Game.isu().new Vector(3, 4));

		e.collision(null);

		assertEquals(0, e.linearSpeed().x(), DELTA);
		assertEquals(0, e.linearSpeed().y(), DELTA);
	}

	@Test
	void done_sans_stunt_ni_bot_ne_leve_pas_d_exception() {
		Entity e = new Entity("e");
		e.done(); // doit simplement ne rien faire
	}

	// ─── deploy / occupy / retract ──────────────────────────────────────────

	@Test
	void retract_libere_la_cellule_occupee() {
		Entity e = new Entity("e");
		Grid.Position p = Game.grid().new Position(5, 5);
		e.setPosition(p);

		Grid.Cell cell = Game.grid().cellAt(p);
		assertTrue(cell.contains(e));

		e.retract();

		assertFalse(cell.contains(e));
	}

	@Test
	void setPosition_deux_fois_retire_l_entite_de_l_ancienne_cellule() {
		Entity e = new Entity("e");
		Grid.Position p1 = Game.grid().new Position(5, 5);
		Grid.Position p2 = Game.grid().new Position(6, 6);

		e.setPosition(p1);
		Grid.Cell cell1 = Game.grid().cellAt(p1);
		assertTrue(cell1.contains(e));

		e.setPosition(p2);
		Grid.Cell cell2 = Game.grid().cellAt(p2);

		assertFalse(cell1.contains(e));
		assertTrue(cell2.contains(e));
	}

	// ─── tick ────────────────────────────────────────────────────────────

	@Test
	void tick_sans_modele_ni_vitesse_ne_fait_rien() {
		Entity e = new Entity("e");
		e.setPosition(Game.grid().new Position(5, 5));

		// pas de model, pas de lSpeed : ne doit pas lever d'exception.
		e.tick(16);
	}

	@Test
	void tick_avec_vitesse_nulle_ne_fait_rien() {
		Entity e = new Entity("e");
		e.setPosition(Game.grid().new Position(5, 5));
		e.setLinearSpeed(Game.isu().new Vector(0, 0));

		double xBefore = e.center().x();
		e.tick(16);

		assertEquals(xBefore, e.center().x(), DELTA);
	}
}
