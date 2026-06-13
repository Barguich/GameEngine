package engine.gal.condition;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Caractérisation : AtStep (condition GAL "à l'étape N") n'est pas encore
 * implémentée. Son constructeur lève systématiquement
 * UnsupportedOperationException.
 */
class AtStepTest {

	@Test
	void le_constructeur_n_est_pas_implemente() {
		assertThrows(UnsupportedOperationException.class, () -> new AtStep(null, null, 0));
	}
}
