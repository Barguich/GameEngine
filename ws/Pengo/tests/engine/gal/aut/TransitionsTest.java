package engine.gal.aut;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import engine.gal.State;
import engine.gal.condition.True;

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

	private Transitions transitions;
    private State s0;
    private State s1;

    @BeforeEach
    void setUp() {
        transitions = new Transitions();
        s0 = new State("Waiting", 0);
        s1 = new State("Walking", 1);
    }

    @Test
    void get_etat_sans_transition_retourne_liste_vide() {
        List<Transition> result = transitions.get(s0);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void add_puis_get_retrouve_la_transition() {
        Transition t = new Transition(s0, new True(), null, s1);
        transitions.add(t);

        List<Transition> result = transitions.get(s0);
        assertEquals(1, result.size());
        assertEquals(t, result.get(0));
    }

    @Test
    void get_ne_melange_pas_les_etats() {
        Transition t0 = new Transition(s0, new True(), null, s1);
        Transition t1 = new Transition(s1, new True(), null, s0);
        transitions.add(t0);
        transitions.add(t1);

        List<Transition> fromS0 = transitions.get(s0);
        List<Transition> fromS1 = transitions.get(s1);

        assertEquals(1, fromS0.size());
        assertEquals(t0, fromS0.get(0));
        assertEquals(1, fromS1.size());
        assertEquals(t1, fromS1.get(0));
    }

    @Test
    void plusieurs_transitions_depuis_meme_etat_sont_ordonnees() {
        Transition t1 = new Transition(s0, new True(), null, s1);
        Transition t2 = new Transition(s0, new True(), null, s0);
        transitions.add(t1);
        transitions.add(t2);

        List<Transition> result = transitions.get(s0);
        assertEquals(2, result.size());
        assertEquals(t1, result.get(0));  // ordre d'insertion préservé
        assertEquals(t2, result.get(1));
    }
}
