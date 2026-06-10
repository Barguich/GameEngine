package Tests;

import engine.*;
import game.Ghost;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StuntTest {

	// sous-classe concrète minimale pour tester Stunt qui est abstract
	static class TestStunt extends Stunt {
		TestStunt(Model model, Entity e) {
			super(model,e);
		}
	}

	private Grid grid;
	private Model model;
	@BeforeEach
	void setUp() {
		new Game(10, 10);
		this.grid = Game.grid();
		this.model=new Model();
	}

	// === Création ===
	@Test
	void constructeur_entiteNull_throws() {
		assertThrows(AssertionError.class, () -> new TestStunt(model,null));
	}

	@Test
	void constructeur_entiteValide_ok() {
		Ghost g = new Ghost();
		TestStunt s = new TestStunt(model,g);
		assertSame(g, s.entity());
	}

	// === Hooks par défaut : ne plantent pas ===
	@Test
	void set_cell_parDefaut_neJettePas() {
		Ghost g = new Ghost();
		g.setSize(grid.new Dimension(1,1));

		g.setPosition(grid.new Position(2, 2));
		TestStunt s = new TestStunt(model,g);
		// appel direct du hook, doit juste rien faire
		assertDoesNotThrow(()->
				s.set(grid.cellAt(grid.new Position(2, 2)))

				);
	}

	@Test
	void collision_entite_parDefaut_neJettePas() {
		Ghost g1 = new Ghost();
		Ghost g2 = new Ghost();
		TestStunt s = new TestStunt(model,g1);
		s.collision(g2);
	}

	@Test
	void collision_liste_parDefaut_neJettePas() {
		Ghost g = new Ghost();
		TestStunt s = new TestStunt(model,g);
		s.collision(java.util.List.of(new Ghost(), new Ghost()));
	}
}

class EntityStuntLinkTest {
	private Model model;
	@org.junit.jupiter.api.BeforeEach
	void setUp() {
		new Game(10, 10);
	}

	@Test
	void entity_stuntParDefaut_null() {
		Ghost g = new Ghost();
		assertNull(g.getStunt());
	}

	@Test
	void setStunt_attache() {
		Ghost g = new Ghost();
		StuntTest.TestStunt s = new StuntTest.TestStunt(model,g);
		g.setStunt(s);
		assertSame(s, g.getStunt());
	}

	@Test
	void setStunt_remplaceLAncien() {
		Ghost g = new Ghost();
		StuntTest.TestStunt s1 = new StuntTest.TestStunt(model,g);
		StuntTest.TestStunt s2 = new StuntTest.TestStunt(model,g);
		g.setStunt(s1);
		g.setStunt(s2);
		assertSame(s2, g.getStunt());
	}
}
