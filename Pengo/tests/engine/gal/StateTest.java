package engine.gal;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Caractérisation : State (engine.gal) n'est pas encore implémenté.
 * Son constructeur lève systématiquement UnsupportedOperationException, ce
 * qui rend equals/hashCode impossibles à tester (aucune instance ne peut être
 * construite).
 */
class StateTest {

	@Test
	void le_constructeur_n_est_pas_implemente() {
		assertThrows(UnsupportedOperationException.class, () -> new State("mode", 0));
	}
}
