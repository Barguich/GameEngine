package model_enginetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import engine.Game;
import model.BasicStunt;
import model.Entity;
import model.Model;

/**
 * BasicStunt : exécution des mouvements et changements d'orientation d'une
 * entité. Game(20,20) -> grille 20x20, torus actif, cmPerCell = 3.7.
 */
class BasicStuntTest {

	private static final double DELTA = 1e-9;

	private Model model;
	private Entity entity;
	private BasicStunt stunt;

	@BeforeEach
	void setUp() {
		new Game(20, 20);
		model = new Model(Game.grid());
		entity = new Entity("e");
		entity.setPosition(Game.grid().new Position(5, 5));
		entity.setStep(Game.grid().new Dimension(1, 1));
		model.add(entity);
		stunt = new BasicStunt(model, entity);
		entity.setStunt(stunt);
	}

	// ─── set(int orientation) ────────────────────────────────────────────

	@Test
	void set_orientation_tourne_l_entite_vers_l_angle_cible() {
		entity.turnTo(10);

		stunt.set(100);

		assertEquals(100, entity.orientation());
	}

	// ─── set(Cell) ────────────────────────────────────────────────────────

	@Test
	void set_cell_deplace_l_entite_sur_la_position_de_la_cellule() {
		var cell = Game.grid().cellAt(Game.grid().new Position(8, 9));

		stunt.set(cell);

		assertEquals(8, entity.position().x());
		assertEquals(9, entity.position().y());
	}

	@Test
	void set_cell_null_ne_fait_rien() {
	    stunt.set((geometry.Grid.Cell) null);

	    assertEquals(5, entity.position().x());
	    assertEquals(5, entity.position().y());
	}

	// ─── set(double x_cm, double y_cm) ───────────────────────────────────

	@Test
	void set_xy_deplace_le_centre_de_l_entite() {
		stunt.set(10.0, 20.0);

		assertEquals(10, entity.center().x(), DELTA);
		assertEquals(20, entity.center().y(), DELTA);
	}

	// ─── walk ────────────────────────────────────────────────────────────

	@Test
	void walk_0_oriente_vers_l_est_et_definit_la_vitesse_lineaire_positive_en_x() {
		stunt.walk(0);

		assertEquals(0, entity.orientation());
		double speed = entity.step().x();
		assertEquals(speed, entity.linearSpeed().x(), DELTA);
		assertEquals(0, entity.linearSpeed().y(), DELTA);
	}

	@Test
	void walk_90_oriente_vers_le_nord_et_definit_la_vitesse_lineaire_negative_en_y() {
		stunt.walk(90);

		assertEquals(90, entity.orientation());
		double speed = entity.step().x();
		assertEquals(0, entity.linearSpeed().x(), DELTA);
		assertEquals(-speed, entity.linearSpeed().y(), DELTA);
	}

	@Test
	void walk_180_oriente_vers_l_ouest_et_definit_la_vitesse_lineaire_negative_en_x() {
		stunt.walk(180);

		assertEquals(180, entity.orientation());
		double speed = entity.step().x();
		assertEquals(-speed, entity.linearSpeed().x(), DELTA);
		assertEquals(0, entity.linearSpeed().y(), DELTA);
	}

	@Test
	void walk_270_oriente_vers_le_sud_et_definit_la_vitesse_lineaire_positive_en_y() {
		stunt.walk(270);

		assertEquals(270, entity.orientation());
		double speed = entity.step().x();
		assertEquals(0, entity.linearSpeed().x(), DELTA);
		assertEquals(speed, entity.linearSpeed().y(), DELTA);
	}

	@Test
	void walk_avec_un_angle_non_cardinal_ne_fait_rien() {
		entity.turnTo(10);
		entity.setLinearSpeed(null);

		stunt.walk(45);

		assertEquals(10, entity.orientation());
		assertNull(entity.linearSpeed());
	}

	// ─── collision(Entity) / collision(List) ────────────────────────────

	@Test
	void collision_simple_ne_leve_pas_d_exception() {
		stunt.collision((Entity) null);
	}

	@Test
	void collision_liste_appelle_collision_sur_chaque_entite() {
		stunt.collision(Arrays.asList((Entity) null, (Entity) null));
	}

	// ─── done ────────────────────────────────────────────────────────────

	@Test
	void done_arrete_l_entite() {
		stunt.walk(0); // donne une vitesse non nulle

		stunt.done();

		assertEquals(0, entity.linearSpeed().x(), DELTA);
		assertEquals(0, entity.linearSpeed().y(), DELTA);
		assertEquals(0, entity.angularSpeed(), DELTA);
	}

	// ─── update ──────────────────────────────────────────────────────────

	@Test
	void update_ne_fait_rien_par_lui_meme() {
		stunt.walk(0);
		double xSpeedBefore = entity.linearSpeed().x();

		stunt.update(16);

		assertEquals(xSpeedBefore, entity.linearSpeed().x(), DELTA);
	}
}
