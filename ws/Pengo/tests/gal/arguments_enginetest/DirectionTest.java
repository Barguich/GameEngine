package gal.arguments_enginetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;


import org.junit.jupiter.api.Test;

import gal.arguments.Direction;

/**
 * Direction : prédicats absolu/relatif et conversion en angle.
 */
class DirectionTest {

	// ─── isAbsolute / isRelative ────────────────────────────────────────

	@Test
	void directions_cardinales_et_diagonales_sont_absolues() {
		Direction[] absolutes = { Direction.N, Direction.S, Direction.E, Direction.W,
				Direction.NE, Direction.NW, Direction.SE, Direction.SW };

		for (Direction d : absolutes) {
			assertTrue(d.isAbsolute(), d.name() + " devrait être absolue");
			assertFalse(d.isRelative(), d.name() + " ne devrait pas être relative");
		}
	}

	@Test
	void directions_relatives_ne_sont_pas_absolues() {
		Direction[] relatives = { Direction.F, Direction.B, Direction.L, Direction.R, Direction.H };

		for (Direction d : relatives) {
			assertTrue(d.isRelative(), d.name() + " devrait être relative");
			assertFalse(d.isAbsolute(), d.name() + " ne devrait pas être absolue");
		}
	}

	// ─── toAngle ─────────────────────────────────────────────────────────

	@Test
	void toAngle_des_directions_absolues() {
		assertEquals(0, Direction.E.toAngle());
		assertEquals(45, Direction.NE.toAngle());
		assertEquals(90, Direction.N.toAngle());
		assertEquals(135, Direction.NW.toAngle());
		assertEquals(180, Direction.W.toAngle());
		assertEquals(-135, Direction.SW.toAngle());
		assertEquals(-90, Direction.S.toAngle());
		assertEquals(-45, Direction.SE.toAngle());
	}

	@Test
	void toAngle_d_une_direction_relative_leve_IllegalStateException() {
		assertThrows(IllegalStateException.class, () -> Direction.F.toAngle());
	}

	// ─── name ────────────────────────────────────────────────────────────

	@Test
	void name_renvoie_le_nom_de_la_direction() {
		assertEquals("N", Direction.N.name());
		assertEquals("F", Direction.F.name());
	}

	// ─── canonical ───────────────────────────────────────────────────────

	@Test
	void canonical_cree_une_nouvelle_instance_distincte_de_la_constante() {
		// Caractérisation : canonical(name) construit un *nouveau* Direction
		// (new Direction(name)) au lieu de chercher dans la map `directions` ;
		// il ne renvoie donc PAS l'instance partagée Direction.N.
		Direction n2 = Direction.N.canonical("N");
		assertSame(Direction.N, n2);
	}

	@Test
	void directionNature() {
		assertTrue(Direction.N.isAbsolute());
		assertTrue(Direction.F.isRelative());
		assertFalse(Direction.F.isAbsolute());
	}
}
