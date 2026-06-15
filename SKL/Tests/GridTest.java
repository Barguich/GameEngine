package Tests;

import engine.Game;
import engine.Grid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GridTest {

	private Grid grid;

	@BeforeEach
	void setUp() {
		new Game(10, 10);
		this.grid = Game.grid();
	}

	// === DIMENSION : normalisation au constructeur ===
	@Test
	void dimension_dansLaCarte() {
		Grid.Dimension d = grid.new Dimension(3, 7);
		assertEquals(3, d.x());
		assertEquals(7, d.y());
	}

	@Test
	void dimension_negatif_normalise() {
		Grid.Dimension d = grid.new Dimension(-1, -1);
		assertEquals(9, d.x());
		assertEquals(9, d.y());
	}

	// === POSITION : normalisation au constructeur ===
	@Test
	void position_negatif_normalise() {
		Grid.Position p = grid.new Position(-1, -1);
		assertEquals(9, p.x());
		assertEquals(9, p.y());
	}

	@Test
	void position_audessusGrille_normalise() {
		Grid.Position p = grid.new Position(12, 15);
		assertEquals(2, p.x());
		assertEquals(5, p.y());
	}

	// === VECTOR : pas de normalisation ===
	@Test
	void vector_negatif_preserve() {
		Grid.Vector v = grid.new Vector(-3, -7);
		assertEquals(-3, v.x());
		assertEquals(-7, v.y());
	}

	// === DISTANCE ===
	@Test
	void position_distance_traverseBordure() {
		Grid.Position a = grid.new Position(1, 5);
		Grid.Position b = grid.new Position(9, 5);
		assertEquals(2, a.distanceTo(b), 1e-9);
	}

	// === EQUALS ===
	@Test
	void position_equals_memesCoord() {
		Grid.Position a = grid.new Position(3, 4);
		Grid.Position b = grid.new Position(3, 4);
		assertEquals(a, b);
	}

	@Test
	void position_equals_apresNormalisation() {
		// (-1,-1) doit être normalisé en (9,9), donc égal à un (9,9) explicite
		Grid.Position a = grid.new Position(-1, -1);
		Grid.Position b = grid.new Position(9, 9);
		assertEquals(a, b);
	}

	@Test
	void position_pasEgale_dimension() {
		// après le découplage Position/Dimension, ces deux types ne sont plus égaux
		Grid.Position p = grid.new Position(3, 4);
		Grid.Dimension d = grid.new Dimension(3, 4);
		assertNotEquals(p, d);
		assertNotEquals(d, p);
	}
}
