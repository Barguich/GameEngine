package engine.gal;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Caractérisation : Mode (engine.gal) n'est pas encore implémenté.
 * Son constructeur lève systématiquement UnsupportedOperationException.
 */
class ModeTest {

	@Test
    void constantes_sont_non_null() {
        assertNotNull(Mode.Blocking);
        assertNotNull(Mode.Dying);
        assertNotNull(Mode.Escaping);
        assertNotNull(Mode.Fighting);
        assertNotNull(Mode.Resting);
        assertNotNull(Mode.Running);
        assertNotNull(Mode.Searching);
        assertNotNull(Mode.Sleeping);
        assertNotNull(Mode.Waiting);
        assertNotNull(Mode.Walking);
    }

    @Test
    void canonical_retourne_la_meme_instance_que_la_constante() {
        assertSame(Mode.Walking, Mode.canonical("Walking"));
        assertSame(Mode.Waiting, Mode.canonical("Waiting"));
        assertSame(Mode.Dying,   Mode.canonical("Dying"));
    }

    @Test
    void canonical_est_idempotent() {
        // Deux appels avec le même nom → même instance (==)
        assertSame(Mode.canonical("Running"), Mode.canonical("Running"));
    }

    @Test
    void name_retourne_le_nom() {
        assertEquals("Walking", Mode.Walking.name());
        assertEquals("Waiting", Mode.Waiting.name());
    }

    @Test
    void toString_retourne_le_nom() {
        assertEquals("Blocking", Mode.Blocking.toString());
        assertEquals("Sleeping", Mode.Sleeping.toString());
    }

    @Test
    void equals_compare_par_nom() {
        Mode m1 = Mode.canonical("Walking");
        Mode m2 = Mode.canonical("Walking");
        assertEquals(m1, m2);
    }

    @Test
    void canonical_nom_inconnu_cree_un_nouveau_mode() {
        // canonical crée un nouveau Mode si le nom n'existe pas encore
        Mode m = Mode.canonical("Custom");
        assertNotNull(m);
        assertEquals("Custom", m.name());
        // et le retrouve ensuite
        assertSame(m, Mode.canonical("Custom"));
    }
}
