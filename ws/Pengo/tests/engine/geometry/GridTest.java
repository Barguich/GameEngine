package engine.geometry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import engine.Game;
import geometry.Grid;
import geometry.ISU;
import model.Entity;

/**
 * Grid : dimensions, normalisation toroïdale des Position/Dimension/Vector,
 * et gestion des entités par Cell.
 * Game(20,20) -> grille 20x20, torus actif sur les deux axes.
 */
class GridTest {

	private static final double DELTA = 1e-9;

	@BeforeEach
	void setUp() {
		new Game(20, 20);
	}

	// ─── width / height ──────────────────────────────────────────────────

	@Test
	void width_et_height_correspondent_aux_dimensions_du_jeu() {
		Grid grid = Game.grid();
		assertEquals(20, grid.width());
		assertEquals(20, grid.height());
	}

	// ─── Position : normalisation toroïdale ─────────────────────────────

	@Test
	void position_normalise_les_coordonnees_hors_grille_sur_le_tore() {
		Grid grid = Game.grid();
		Grid.Position p = grid.new Position(22, -1);
		assertEquals(2, p.x());
		assertEquals(19, p.y());
	}

	@Test
	void position_copy_est_independante_de_l_originale() {
		Grid grid = Game.grid();
		Grid.Position original = grid.new Position(5, 5);
		Grid.Position copy = original.copy();

		copy.translate(grid.new Vector(1, 0));

		assertEquals(6, copy.x());
		assertEquals(5, original.x());
	}

	@Test
	void position_equals_compare_x_et_y() {
		Grid grid = Game.grid();
		Grid.Position a = grid.new Position(3, 4);
		Grid.Position b = grid.new Position(3, 4);
		Grid.Position c = grid.new Position(3, 5);

		assertEquals(a, b);
		assertNotEquals(a, c);
	}

	@Test
	void position_equiv_compare_apres_normalisation_toroïdale() {
		Grid grid = Game.grid();
		Grid.Position a = grid.new Position(0, 0);
		Grid.Position b = grid.new Position(20, 0); // 20 normalisé -> 0

		assertTrue(a.equiv(b));
	}

	@Test
	void position_equiv_avec_null_est_faux() {
		Grid grid = Game.grid();
		Grid.Position a = grid.new Position(0, 0);
		assertFalse(a.equiv(null));
	}

	@Test
	void translate_deplace_et_normalise_sur_le_tore() {
		Grid grid = Game.grid();
		Grid.Position p = grid.new Position(19, 19);
		p.translate(grid.new Vector(2, 2));

		assertEquals(1, p.x()); // (19+2) % 20 = 1
		assertEquals(1, p.y());
	}

	@Test
	void moveNorth_decremente_y_et_normalise() {
		Grid grid = Game.grid();
		Grid.Position p = grid.new Position(5, 0);
		p.moveNorth(1);

		assertEquals(5, p.x());
		assertEquals(19, p.y()); // (0 - 1) mod 20 = 19
	}

	@Test
	void rotateAround_90_degres_autour_de_l_origine() {
		Grid grid = Game.grid();
		Grid.Position p = grid.new Position(1, 0);
		Grid.Position center = grid.new Position(0, 0);

		p.rotateAround(center, 90);

		assertEquals(0, p.x());
		assertEquals(1, p.y());
	}

	@Test
	void distanceTo_sans_repli_de_tore() {
		Grid grid = Game.grid();
		Grid.Position a = grid.new Position(2, 2);
		Grid.Position b = grid.new Position(5, 6);

		assertEquals(5.0, a.distanceTo(b), DELTA); // 3-4-5
	}

	@Test
	void distanceTo_prend_le_plus_court_chemin_sur_le_tore() {
		Grid grid = Game.grid();
		Grid.Position a = grid.new Position(0, 5);
		Grid.Position b = grid.new Position(19, 5);

		// |0-19| = 19 >= 10 -> 20-19 = 1
		assertEquals(1.0, a.distanceTo(b), DELTA);
	}

	@Test
	void toISUCoord_convertit_en_coin_de_cellule() {
		Grid grid = Game.grid();
		Grid.Position p = grid.new Position(2, 3);
		ISU.Coord c = p.toISUCoord();

		assertEquals(2 * Game.game().cmPerCell, c.x(), DELTA);
		assertEquals(3 * Game.game().cmPerCell, c.y(), DELTA);
	}

	@Test
	void toISUCoordCentered_convertit_en_centre_de_cellule() {
		Grid grid = Game.grid();
		Grid.Position p = grid.new Position(2, 3);
		ISU.Coord c = p.toISUCoordCentered();

		double cm = Game.game().cmPerCell;
		assertEquals((2 + .5) * cm, c.x(), DELTA);
		assertEquals((3 + .5) * cm, c.y(), DELTA);
	}

	@Test
	void grid_renvoie_la_grille_englobante() {
		Grid grid = Game.grid();
		Grid.Position p = grid.new Position(2, 3);

		assertSame(grid, p.grid());
	}

	// ─── Dimension ───────────────────────────────────────────────────────

	@Test
	void dimension_normalise_x_et_y_sur_le_tore() {
		Grid grid = Game.grid();
		Grid.Dimension d = grid.new Dimension(22, -1);

		assertEquals(2, d.x());
		assertEquals(19, d.y());
	}

	@Test
	void dimension_equals_compare_apres_normalisation() {
		Grid grid = Game.grid();
		Grid.Dimension a = grid.new Dimension(3, 4);
		Grid.Dimension b = grid.new Dimension(3, 4);
		Grid.Dimension c = grid.new Dimension(3, 5);

		assertTrue(a.equals(b));
		assertFalse(a.equals(c));
	}

	@Test
	void dimension_equiv_compare_apres_normalisation_toroïdale() {
		Grid grid = Game.grid();
		Grid.Dimension a = grid.new Dimension(1, 1);
		Grid.Dimension b = grid.new Dimension(21, 1); // 21 normalisé -> 1

		assertTrue(a.equiv(b));
	}

	@Test
	void dimension_equiv_avec_null_est_faux() {
		Grid grid = Game.grid();
		Grid.Dimension a = grid.new Dimension(1, 1);

		assertFalse(a.equiv(null));
	}

	@Test
	void dimension_toISUDimension_multiplie_par_cmPerCell() {
		Grid grid = Game.grid();
		Grid.Dimension d = grid.new Dimension(2, 3);
		ISU.Dimension isuD = d.toISUDimension();

		double cm = Game.game().cmPerCell;
		assertEquals(2 * cm, isuD.x(), DELTA);
		assertEquals(3 * cm, isuD.y(), DELTA);
	}

	@Test
	void dimension_normalize_renormalise_apres_mutation() {
		Grid grid = Game.grid();
		Grid.Dimension d = grid.new Dimension(5, 5);

		// pas de mutation directe possible depuis l'extérieur du package,
		// on vérifie juste que normalize() est sans effet sur une valeur déjà
		// normalisée.
		d.normalize();

		assertEquals(5, d.x());
		assertEquals(5, d.y());
	}

	// ─── Vector ──────────────────────────────────────────────────────────

	@Test
	void vector_add_mute_le_vecteur() {
		Grid grid = Game.grid();
		Grid.Vector v = grid.new Vector(1, 2);
		v.add(grid.new Vector(3, 4));

		assertEquals(4, v.x());
		assertEquals(6, v.y());
	}

	@Test
	void vector_length_pythagore() {
		Grid grid = Game.grid();
		Grid.Vector v = grid.new Vector(3, 4);

		assertEquals(5.0, v.length(), DELTA);
	}

	// ─── Cell : entités ──────────────────────────────────────────────────

	@Test
	void cell_at_renvoie_la_cellule_a_la_position_normalisee() {
		Grid grid = Game.grid();
		Grid.Position p = grid.new Position(22, 1); // normalisé -> (2,1)
		Grid.Cell cell = grid.cellAt(p);

		assertEquals(2, cell.position().x());
		assertEquals(1, cell.position().y());
	}

	@Test
	void cell_add_contains_et_remove_une_entite() {
		Grid grid = Game.grid();
		Grid.Position p = grid.new Position(5, 5);
		Grid.Cell cell = grid.cellAt(p);
		Entity e = new Entity("e");

		assertFalse(cell.contains(e));

		cell.add(e);
		assertTrue(cell.contains(e));
		assertTrue(cell.entities().contains(e));

		cell.remove(e);
		assertFalse(cell.contains(e));
	}

	@Test
	void cell_add_est_idempotent() {
		Grid grid = Game.grid();
		Grid.Position p = grid.new Position(6, 6);
		Grid.Cell cell = grid.cellAt(p);
		Entity e = new Entity("e");

		cell.add(e);
		cell.add(e);

		assertEquals(1, cell.entities().size());
	}
}
