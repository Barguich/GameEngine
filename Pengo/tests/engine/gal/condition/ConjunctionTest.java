package engine.gal.condition;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Caractérisation : Conjunction (conjonction de conditions GAL) n'est pas
 * encore implémentée. Son constructeur lève systématiquement
 * UnsupportedOperationException.
 */
class ConjunctionTest {

	@Test
	void le_constructeur_n_est_pas_implemente() {
		assertThrows(UnsupportedOperationException.class, () -> new Conjunction());
	}
}
