package engine.gal.condition;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Caractérisation : GALCondition n'a pas de constructeur explicite, mais son
 * champ d'instance {@code TRUE = new True()} est initialisé à la
 * construction. Comme {@code True()} lève UnsupportedOperationException,
 * construire un GALCondition échoue également.
 */
class GALConditionTest {

	@Test
	void la_construction_echoue_via_l_initialisation_du_champ_TRUE() {
		assertThrows(UnsupportedOperationException.class, () -> new GALCondition());
	}
}
