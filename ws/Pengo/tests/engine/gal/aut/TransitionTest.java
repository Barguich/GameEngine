package engine.gal.aut;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import engine.gal.State;
import engine.gal.condition.True;

/**
 * Caractérisation : Transition n'est pas encore implémentée.
 * Le constructeur lève systématiquement UnsupportedOperationException, quels
 * que soient ses arguments (tous {@code null} ici, car {@code State} et
 * {@code GALAction} n'ont eux-mêmes pas d'instance constructible facilement).
 */
class TransitionTest {

	
    private State s0;
    private State s1;

    @BeforeEach
    void setUp() {
        s0 = new State("Waiting", 0);
        s1 = new State("Walking", 1);
    }

    @Test
    void source_et_target_sont_corrects() {
        Transition t = new Transition(s0, new True(), null, s1);
        assertEquals(s0, t.source());
        assertEquals(s1, t.target());
    }

    @Test
    void exec_avec_condition_vraie_retourne_true() {
        Transition t = new Transition(s0, new True(), null, s1);
        assertTrue(t.exec(null));
    }

    @Test
    void exec_avec_condition_fausse_retourne_false() {
        Transition t = new Transition(s0, e -> false, null, s1);
        assertFalse(t.exec(null));
    }

    @Test
    void exec_avec_action_null_ne_plante_pas() {
        // action == null est autorisé — exec doit simplement ne pas appeler action
        Transition t = new Transition(s0, new True(), null, s1);
        assertDoesNotThrow(() -> t.exec(null));
    }
	
}
