package engine.gal.condition;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import engine.model.Entity;

/**
 * Caractérisation : True (condition GAL toujours vraie) n'est pas encore
 * implémentée. Son constructeur lève systématiquement
 * UnsupportedOperationException.
 */
class TrueTest {

	@Test
	void trueConditionShouldAlwaysBeTrue() {
		True t = new True();
		Entity e = new Entity("player");
		assertTrue(t.eval(e));
	}
}
