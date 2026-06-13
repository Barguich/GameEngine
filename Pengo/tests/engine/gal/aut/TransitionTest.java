package engine.gal.aut;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Caractérisation : Transition n'est pas encore implémentée.
 * Le constructeur lève systématiquement UnsupportedOperationException, quels
 * que soient ses arguments (tous {@code null} ici, car {@code State} et
 * {@code GALAction} n'ont eux-mêmes pas d'instance constructible facilement).
 */
class TransitionTest {

	@Test
	void le_constructeur_n_est_pas_implemente() {
		assertThrows(UnsupportedOperationException.class, () -> new Transition(null, null, null, null));
	}
}
