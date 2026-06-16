package engine.gal.condition;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import engine.gal.arguments.Category;
import engine.gal.arguments.Direction;

/**
 * Caractérisation : AtStep (condition GAL "à l'étape N") n'est pas encore
 * implémentée. Son constructeur lève systématiquement
 * UnsupportedOperationException.
 */
class AtStepTest {

	
    @Test
    void constructeur_trois_args_ne_plante_pas() {
        assertDoesNotThrow(() -> new AtStep(Direction.F, Category.V, 1));
    }

    @Test
    void constructeur_deux_args_ne_plante_pas() {
        // AtStep(int nbStep, Category cat) → délègue à (F, cat, nbStep)
        assertDoesNotThrow(() -> new AtStep(1, Category.V));
    }

    @Test
    void constructeur_deux_args_utilise_direction_F() {
        // toString() expose direction — vérifie que F est utilisé par défaut
        AtStep step = new AtStep(2, Category.V);
		    String s = step.toString();
        //assertTrue(step.toString().contains("F"));
		    assertTrue(s.contains("F"), "doit contenir la direction F : " + s);
    }

    // === TOSTRING ===

    @Test
    void toString_contient_direction_nbStep_category() {
        AtStep step = new AtStep(Direction.N, Category.G, 3);
       String s = step.toString();
    assertTrue(s.contains("N"), "doit contenir la direction N : " + s);
    assertTrue(s.contains("3"), "doit contenir nbStep 3 : " + s);
    assertTrue(s.contains("G"), "doit contenir la catégorie G : " + s);
    }

    // === CONJUNCTION (testable sans Entity) ===

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
    void conjunction_court_circuite_sur_premier_false() {
        // Si la première condition est fausse, la deuxième ne doit pas être évaluée
        boolean[] evaluated = {false};
        Conjunction c = new Conjunction();
        c.add(e -> false);
        c.add(e -> { evaluated[0] = true; return true; });

        c.eval(null);

        assertFalse(evaluated[0], "la deuxième condition ne doit pas être évaluée");
    }

    // === TRUE ===

    @Test
    void true_retourne_toujours_true() {
        assertTrue(new True().eval(null));
    }
}
