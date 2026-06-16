package engine.gal;

import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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
    void constructeur_ne_plante_pas() {
        assertDoesNotThrow(() -> new State("Waiting", 0));
    }

    @Test
    void mode_et_id_sont_corrects() {
        State s = new State("Walking", 3);
        assertEquals(Mode.Walking, s.mode());
        assertEquals(3, s.id());
    }

    @Test
    void equals_meme_mode_meme_id() {
        State s1 = new State("Waiting", 0);
        State s2 = new State("Waiting", 0);
        assertEquals(s1, s2);
    }

    @Test
    void equals_meme_mode_id_different() {
        State s1 = new State("Waiting", 0);
        State s2 = new State("Waiting", 1);
        assertNotEquals(s1, s2);
    }

    @Test
    void equals_mode_different_meme_id() {
        State s1 = new State("Waiting", 0);
        State s2 = new State("Walking", 0);
        assertNotEquals(s1, s2);
    }

    @Test
    void equals_null_retourne_false() {
        State s = new State("Waiting", 0);
        assertNotEquals(null, s);
    }

    @Test
    void equals_autre_type_retourne_false() {
        State s = new State("Waiting", 0);
        assertNotEquals("Waiting", s);
    }

    @Test
    void hashcode_coherent_avec_equals() {
        State s1 = new State("Waiting", 0);
        State s2 = new State("Waiting", 0);
        // Si equals → même hashCode (contrat Java)
        assertEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void hashcode_different_pour_etats_differents() {
        State s1 = new State("Waiting", 0);
        State s2 = new State("Walking", 1);
        // Pas garanti par le contrat, mais hautement probable
        assertNotEquals(s1.hashCode(), s2.hashCode());
    }

    @Test
    void mode_utilise_canonical_flyweight() {
        State s1 = new State("Walking", 0);
        State s2 = new State("Walking", 1);
        // Les deux doivent référencer la MÊME instance Mode
        assertSame(s1.mode(), s2.mode());
        assertSame(Mode.Walking, s1.mode());
    }
}
