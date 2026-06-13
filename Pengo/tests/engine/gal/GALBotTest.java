package engine.gal;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import engine.model.Entity;

/**
 * Caractérisation : GALBot (engine.gal) n'est pas encore implémenté.
 * Son constructeur lève systématiquement UnsupportedOperationException.
 */
class GALBotTest {

	@Test
	void le_constructeur_n_est_pas_implemente() {
		Entity e = new Entity("e");

		assertThrows(UnsupportedOperationException.class, () -> new GALBot(e));
	}
}
