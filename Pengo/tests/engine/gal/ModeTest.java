package engine.gal;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Caractérisation : Mode (engine.gal) n'est pas encore implémenté.
 * Son constructeur lève systématiquement UnsupportedOperationException.
 */
class ModeTest {

	@Test
	void le_constructeur_n_est_pas_implemente() {
		assertThrows(UnsupportedOperationException.class, () -> new Mode("name"));
	}
}
