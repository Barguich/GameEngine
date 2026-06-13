package engine.gal.condition;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Caractérisation : True (condition GAL toujours vraie) n'est pas encore
 * implémentée. Son constructeur lève systématiquement
 * UnsupportedOperationException.
 */
class TrueTest {

	@Test
	void le_constructeur_n_est_pas_implemente() {
		assertThrows(UnsupportedOperationException.class, () -> new True());
	}
}
