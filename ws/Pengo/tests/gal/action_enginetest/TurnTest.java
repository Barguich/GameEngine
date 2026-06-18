package gal.action_enginetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import action.Turn;
import arguments.Direction;
import engine.Game;
import model.Entity;

/**
 * Turn (engine.gal.action) : action GAL qui fait tourner une entité d'un
 * angle donné, construit soit directement, soit via une Direction.
 */
class TurnTest {

	@BeforeEach
	void setUp() {
		new Game(20, 20);
	}

	// ─── construction directe ────────────────────────────────────────────

	@Test
	void exec_tourne_l_entite_de_l_angle_donne() {
		Entity e = new Entity("e");
		e.turnTo(10);

		Turn turn = new Turn(30, 1.0);
		boolean ok = turn.exec(e);

		assertTrue(ok);
		assertEquals(40, e.orientation());
	}

	@Test
	void exec_avec_entite_null_renvoie_faux() {
		Turn turn = new Turn(30);

		assertFalse(turn.exec(null));
	}

	@Test
	void angle_negatif_tourne_dans_l_autre_sens() {
		Entity e = new Entity("e");
		e.turnTo(40);

		Turn turn = new Turn(-30);
		turn.exec(e);

		assertEquals(10, e.orientation());
	}

	// ─── constructeur via Direction relative ────────────────────────────

	@Test
	void direction_R_correspond_a_un_angle_de_90() {
		Entity e = new Entity("e");
		e.turnTo(0);

		new Turn(Direction.R).exec(e);

		assertEquals(90, e.orientation());
	}

	@Test
	void direction_L_correspond_a_un_angle_de_moins_90() {
		Entity e = new Entity("e");
		e.turnTo(0);

		new Turn(Direction.L).exec(e);

		assertEquals(270, e.orientation()); // -90 normalisé
	}

	@Test
	void direction_B_correspond_a_un_angle_de_180() {
		Entity e = new Entity("e");
		e.turnTo(0);

		new Turn(Direction.B).exec(e);

		assertEquals(180, e.orientation());
	}

	@Test
	void direction_H_relative_inconnue_utilise_90_par_defaut() {
		Entity e = new Entity("e");
		e.turnTo(0);

		new Turn(Direction.H).exec(e);

		assertEquals(90, e.orientation());
	}

	// ─── constructeur via Direction absolue ─────────────────────────────

	@Test
	void direction_absolue_utilise_son_angle() {
		Entity e = new Entity("e");
		e.turnTo(0);

		new Turn(Direction.N).exec(e); // toAngle() == 90

		assertEquals(90, e.orientation());
	}

	// ─── constructeur intensite seule ───────────────────────────────────

	@Test
	void constructeur_avec_seulement_intensite_tourne_de_90_degres() {
		Entity e = new Entity("e");
		e.turnTo(0);

		new Turn(0.5).exec(e);

		assertEquals(90, e.orientation());
	}
	@Test
void exec_avec_angle_zero_ne_change_pas_l_orientation() {
    Entity e = new Entity("e");
    e.turnTo(45);
    new Turn(0).exec(e);
    assertEquals(45, e.orientation());
}
}
