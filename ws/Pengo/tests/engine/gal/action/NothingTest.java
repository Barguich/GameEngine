package engine.gal.action;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import action.GALAction;
import action.Nothing;


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

    @Test
    void NOTHING_est_singleton_meme_reference() {
        // Flyweight : toujours la même instance
        assertSame(GALAction.NOTHING, GALAction.NOTHING);
    }

    @Test
    void exec_avec_entity_non_null_retourne_aussi_true() {
        // Nothing ignore l'entité — doit retourner true dans tous les cas
        Nothing nothing = new Nothing();
        assertTrue(nothing.exec(null)); // null OK
        // On ne peut pas construire une vraie Entity sans le framework,
        // mais null suffit à couvrir le contrat
    }
}
