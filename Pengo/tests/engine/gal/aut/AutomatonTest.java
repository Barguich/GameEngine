package engine.gal.aut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import engine.gal.State;

/**
 * Caractérisation : Automaton n'est pas encore implémenté.
 * Le constructeur lève systématiquement UnsupportedOperationException.
 * Comme {@code engine.gal.State} a, lui aussi, un constructeur non implémenté,
 * il est impossible de construire le moindre argument {@code initial} : on se
 * contente donc de vérifier que l'appel échoue avec {@code null}.
 */
class AutomatonTest {
	private State s0;
	private State s1;
	private Automaton automaton;

	@BeforeEach
    void setUp() {
        s0 = new State("Waiting",0);
        s1 = new State("Walking",1);
        automaton = new Automaton("test", s0);
    }

    @Test
    void etat_initial_est_current() {
        assertEquals(s0, automaton.current());
        assertEquals(s0, automaton.initial());
    }

    @Test
    void name_est_correct() {
        assertEquals("test", automaton.name());
    }

    @Test
    void step_sans_transition_retourne_false() {
        // Aucune transition ajoutée → step doit retourner false
        assertFalse(automaton.step(null));
    }

    @Test
    void step_avec_condition_vraie_change_etat() {
        // Transition de s0 vers s1 avec condition True
        Transition t = new Transition(s0, e -> true, null, s1);
        automaton.add(t);

        boolean result = automaton.step(null);

        assertTrue(result);
        assertEquals(s1, automaton.current());
    }

    @Test
    void step_avec_condition_fausse_reste_dans_etat_courant() {
        Transition t = new Transition(s0, e -> false, null, s1);
        automaton.add(t);

        boolean result = automaton.step(null);

        assertFalse(result);
        assertEquals(s0, automaton.current());
    }
}
