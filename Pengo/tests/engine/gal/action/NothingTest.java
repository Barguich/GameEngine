package engine.gal.action;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import engine.gal.action.GALAction;
import engine.gal.action.Nothing;

/**
 * Nothing (engine.gal.action) : action GAL neutre, toujours réussie.
 */
class NothingTest {

	@Test
	void exec_renvoie_toujours_vrai() {
		Nothing nothing = new Nothing();

		assertTrue(nothing.exec(null));
	}

	@Test
	void galaction_NOTHING_est_une_constante_partagee() {
		assertNotNull(GALAction.NOTHING);
		assertTrue(GALAction.NOTHING.exec(null));
	}
}
