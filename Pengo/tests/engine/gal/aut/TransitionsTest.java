package engine.gal.aut;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Caractérisation : Transitions (implémentation par défaut de iTransitions)
 * n'est qu'un squelette.
 * <UL>
 * <LI>{@code add} n'est pas implémenté et lève toujours
 * UnsupportedOperationException.</LI>
 * <LI>{@code get} est implémenté mais renvoie toujours {@code null}, quelle
 * que soit la clé.</LI>
 * </UL>
 */
class TransitionsTest {

	@Test
	void add_n_est_pas_implemente() {
		Transitions transitions = new Transitions();

		assertThrows(UnsupportedOperationException.class, () -> transitions.add(null));
	}

	@Test
	void get_renvoie_toujours_null() {
		Transitions transitions = new Transitions();

		assertNull(transitions.get(null));
	}
}
