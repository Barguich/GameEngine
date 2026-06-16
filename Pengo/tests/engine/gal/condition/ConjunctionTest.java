package engine.gal.condition;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Caractérisation : Conjunction (conjonction de conditions GAL) n'est pas
 * encore implémentée. Son constructeur lève systématiquement
 * UnsupportedOperationException.
 */
class ConjunctionTest {

    @Test
    void constructeur_ne_plante_pas() {
        assertDoesNotThrow(() -> new Conjunction());
    }

    @Test
    void conjunction_vide_retourne_true() {
        Conjunction c = new Conjunction();
        assertTrue(c.eval(null));
    }

    @Test
    void conjunction_avec_un_true_retourne_true() {
        Conjunction c = new Conjunction();
        c.add(new True());
        assertTrue(c.eval(null));
    }

    @Test
    void conjunction_avec_condition_fausse_retourne_false() {
        Conjunction c = new Conjunction();
        c.add(new True());
        c.add(e -> false);
        assertFalse(c.eval(null));
    }

    @Test
    void conjunction_plusieurs_true_retourne_true() {
        Conjunction c = new Conjunction();
        c.add(new True());
        c.add(new True());
        c.add(new True());
        assertTrue(c.eval(null));
    }

    @Test
    void conjunction_court_circuite_sur_premier_false() {
        boolean[] evaluated = {false};
        Conjunction c = new Conjunction();
        c.add(e -> false);
        c.add(e -> { evaluated[0] = true; return true; });

        c.eval(null);

        assertFalse(evaluated[0], "la deuxième condition ne doit pas être évaluée");
    }

    @Test
    void conjunction_false_en_milieu_retourne_false() {
        boolean[] troisieme = {false};
        Conjunction c = new Conjunction();
        c.add(new True());
        c.add(e -> false);
        c.add(e -> { troisieme[0] = true; return true; });

        boolean result = c.eval(null);

        assertFalse(result);
        assertFalse(troisieme[0], "la troisième condition ne doit pas être évaluée");
    }
}
