package engine.gal.aut;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Caractérisation : Automaton n'est pas encore implémenté.
 * Le constructeur lève systématiquement UnsupportedOperationException.
 * Comme {@code engine.gal.State} a, lui aussi, un constructeur non implémenté,
 * il est impossible de construire le moindre argument {@code initial} : on se
 * contente donc de vérifier que l'appel échoue avec {@code null}.
 */
class AutomatonTest {

	@Test
	void le_constructeur_n_est_pas_implemente() {
		assertThrows(UnsupportedOperationException.class, () -> new Automaton("name", null));
	}
}
