package engine.gal.condition;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import engine.model.Entity;

/**
 * Caractérisation : Conjunction (conjonction de conditions GAL) n'est pas
 * encore implémentée. Son constructeur lève systématiquement
 * UnsupportedOperationException.
 */
class ConjunctionTest {

	@Test
	void conjunctionOfTrueShouldBeTrue() {
		Conjunction c = new Conjunction();
		c.add(new True());
		c.add(new True());
		Entity e = new Entity("player");
		assertTrue(c.eval(e));
	}
}
