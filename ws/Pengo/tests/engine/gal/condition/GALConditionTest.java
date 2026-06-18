package engine.gal.condition;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import gal.condition.Conjunction;
import gal.condition.GALCondition;
import gal.condition.True;

/**
 * Caractérisation : GALCondition n'a pas de constructeur explicite, mais son
 * champ d'instance {@code TRUE = new True()} est initialisé à la
 * construction. Comme {@code True()} lève UnsupportedOperationException,
 * construire un GALCondition échoue également.
 */
class GALConditionTest {

	
    @Test
    void TRUE_est_non_null() {
        assertNotNull(GALCondition.TRUE);
    }

    @Test
    void TRUE_est_une_instance_de_True() {
        assertInstanceOf(True.class, GALCondition.TRUE);
    }

    @Test
    void TRUE_eval_retourne_toujours_true() {
        assertTrue(GALCondition.TRUE.eval(null));
    }

    @Test
    void TRUE_est_partage_meme_instance() {
        // Flyweight : TRUE est une constante statique partagée
        assertSame(GALCondition.TRUE, GALCondition.TRUE);
    }

    @Test
    void une_sous_classe_concrete_est_instanciable() {
        // GALCondition est abstraite — on passe par une sous-classe concrète
        assertDoesNotThrow(() -> new True());
        assertDoesNotThrow(() -> new Conjunction());
    }
}
