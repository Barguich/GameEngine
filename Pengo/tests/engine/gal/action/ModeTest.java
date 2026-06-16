package engine.gal.action;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Caractérisation : Mode (engine.gal.action) n'est pas encore implémenté.
 * Son constructeur lève systématiquement UnsupportedOperationException.
 */
class ModeTest {

	  @Test
    void canonical_retourne_instance_existante() {
        assertSame(Mode.Walking, Mode.canonical("Walking"));
        assertSame(Mode.Waiting, Mode.canonical("Waiting"));
    }

    @Test
    void canonical_instances_sont_identiques_par_reference() {
        // Flyweight : == doit fonctionner
        assertSame(Mode.canonical("Running"), Mode.canonical("Running"));
    }

    @Test
    void name_retourne_le_nom() {
        assertEquals("Walking", Mode.Walking.name());
        assertEquals("Waiting", Mode.Waiting.name());
        assertEquals("Dying",   Mode.Dying.name());
    }

    @Test
    void toutes_les_constantes_sont_non_null() {
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
    void canonical_nom_inconnu_retourne_null() {
        assertNull(Mode.canonical("Inexistant"));
    }
}
