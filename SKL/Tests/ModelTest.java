package Tests;

import engine.Game;
import engine.Grid;
import engine.Model;
import game.Ghost;
import game.Obstacle;
import game.PacMan;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

	static class TestModel extends Model {
		TestModel() {
			super();
		}
	}

	private Model model;
	private Grid grid;

	@BeforeEach
	void setUp() {
		new Game(10, 10);
		this.model = new TestModel();
		this.grid = Game.grid();
	}

	@Test
	void modelVide_grilleNonNull() {
		assertNotNull(model.grid());
	}

	@Test
	void modelVide_listeEntitesVide() {
		assertTrue(model.entities().isEmpty());
	}

	@Test
	void modelGrid_estLaMemeQueGameGrid() {
		assertSame(Game.grid(), model.grid());
	}

	@Test
	void add_ajouteALaListe() {
		Ghost g = new Ghost();
		model.add(g);
		assertEquals(1, model.entities().size());
		assertTrue(model.entities().contains(g));
	}

	@Test
	void add_dejadedans() {
		Ghost g = new Ghost();
		model.add(g);
		model.add(g);
		assertEquals(1, model.entities().size());
	}

	@Test
	void add_pbsinull() {
		assertThrows(IllegalArgumentException.class, () -> model.add(null));
	}

	@Test
	void add_plusieursEntitesDeTypesDifferents() {
		model.add(new Ghost());
		model.add(new PacMan());
		model.add(new Obstacle(3, 3));
		assertEquals(3, model.entities().size());
	}

	// === remove ===
	@Test
	void remove_retireDeLaListe() {
		Ghost g = new Ghost();
		model.add(g);
		model.remove(g);
		assertTrue(model.entities().isEmpty());
	}

	@Test
	void remove_entitePositionnee_videLaCellule() {
		Ghost g = new Ghost();
		g.setPosition(grid.new Position(4, 4));
		model.add(g);

		Grid.Cell cell = grid.cellAt(grid.new Position(4, 4));
		assertTrue(cell.contains(g));

		model.remove(g);
		assertFalse(cell.contains(g));
	}

	@Test
	void remove_entiteNonAjoutee_neJettePas() {
		Ghost g = new Ghost();
		model.remove(g);
	}

	@Test
	void remove_null_neJettePas() {
		model.remove(null);
	}

	@Test
	void entities_listeNonModifiable() {
		Ghost g = new Ghost();
		model.add(g);
		assertThrows(UnsupportedOperationException.class,
				() -> model.entities().add(new Ghost()));
	}

	@Test
	void scenario_placementMultipleEntites() {
		PacMan p = new PacMan();
		p.setPosition(grid.new Position(5, 5));
		model.add(p);

		for (int i = 0; i < 3; i++) {
			Ghost g = new Ghost();
			g.setPosition(grid.new Position(i, 0));
			model.add(g);
		}

		Obstacle o = new Obstacle(8, 8);
		model.add(o);

		assertEquals(5, model.entities().size());

		assertTrue(grid.cellAt(grid.new Position(5, 5)).contains(p));
		assertTrue(grid.cellAt(grid.new Position(8, 8)).contains(o));
	}

}
