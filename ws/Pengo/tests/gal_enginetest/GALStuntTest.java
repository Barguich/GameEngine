package gal_enginetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gal.arguments.Direction;
import engine.Game;
import gal_engine.GALStunt;
import model.Entity;
import model.Model;

/**
 * GALStunt : déplacements et rotations temporisés pilotés par {@code tick}.
 * Game(20,20) -> grille 20x20, torus actif.
 */
class GALStuntTest {

	 private static final double DELTA = 1e-9;

    private Model model;
    private Entity entity;
    private GALStunt stunt;

    @BeforeEach
    void setUp() {
        new Game(20, 20);
        model = new Model(Game.grid());
        entity = new Entity("e");
        entity.setPosition(Game.grid().new Position(5, 5));
        model.add(entity);
        stunt = new GALStunt(model, entity);
        entity.setStunt(stunt);
    }

    // ─── stepLength ──────────────────────────────────────────────────────

    @Test
    void stepLength_par_defaut_est_10() {
        assertEquals(10, stunt.stepLength(), DELTA);
    }

    @Test
    void setStepLength_modifie_stepLength() {
        stunt.setStepLength(5);
        assertEquals(5, stunt.stepLength(), DELTA);
    }

    // ─── actionDuration ──────────────────────────────────────────────────

    @Test
    void actionDuration_par_defaut_est_zero() {
        assertEquals(0, stunt.actionDuration(), DELTA);
    }

    // ─── startMoving ─────────────────────────────────────────────────────

    @Test
    void startMoving_vers_l_est_definit_la_vitesse_lineaire_positive_en_x() {
        boolean ok = stunt.startMoving(Direction.E, 1.0, 100);

        assertTrue(ok);
        assertEquals(0.1, entity.linearSpeed().x(), DELTA);
        assertEquals(0,   entity.linearSpeed().y(), DELTA);
        assertEquals(100, stunt.actionDuration(),   DELTA);
    }

    @Test
    void startMoving_vers_l_ouest_definit_la_vitesse_lineaire_negative_en_x() {
        stunt.startMoving(Direction.W, 1.0, 100);

        assertEquals(-0.1, entity.linearSpeed().x(), DELTA);
        assertEquals(0,    entity.linearSpeed().y(), DELTA);
    }

    @Test
    void startMoving_vers_le_nord_definit_la_vitesse_lineaire_negative_en_y() {
        stunt.startMoving(Direction.N, 1.0, 100);

        assertEquals(0,    entity.linearSpeed().x(), DELTA);
        assertEquals(-0.1, entity.linearSpeed().y(), DELTA);
    }

    @Test
    void startMoving_vers_le_sud_definit_la_vitesse_lineaire_positive_en_y() {
        stunt.startMoving(Direction.S, 1.0, 100);

        assertEquals(0,   entity.linearSpeed().x(), DELTA);
        assertEquals(0.1, entity.linearSpeed().y(), DELTA);
    }

    @Test
    void startMoving_avec_intensite_nulle_utilise_la_vitesse_max() {
        stunt.startMoving(Direction.E, 0, 100);

        assertEquals(0.1, entity.linearSpeed().x(), DELTA);
    }

    @Test
    void startMoving_avec_intensite_negative_utilise_la_vitesse_max() {
        stunt.startMoving(Direction.E, -1.0, 100);

        assertEquals(0.1, entity.linearSpeed().x(), DELTA);
    }

    @Test
    void startMoving_avec_intensite_partielle_reduit_la_vitesse() {
        stunt.startMoving(Direction.E, 0.5, 100);

        assertEquals(0.05, entity.linearSpeed().x(), DELTA);
    }

    @Test
    void startMoving_pendant_une_action_en_cours_renvoie_faux() {
        stunt.startMoving(Direction.E, 1.0, 100);

        boolean second = stunt.startMoving(Direction.N, 1.0, 50);

        assertFalse(second);
        // la vitesse précédente n'est pas modifiée
        assertEquals(0.1, entity.linearSpeed().x(), DELTA);
        assertEquals(0,   entity.linearSpeed().y(), DELTA);
    }

    @Test
    void startMoving_avec_direction_non_cardinale_ne_definit_aucune_vitesse() {
        // Direction.F n'est pas dans le switch → vitesse reste null
        boolean ok = stunt.startMoving(Direction.F, 1.0, 100);

        assertTrue(ok);
        assertNull(entity.linearSpeed());
        assertEquals(100, stunt.actionDuration(), DELTA);
    }

    @Test
    void setMaxLinearSpeed_modifie_la_vitesse_de_deplacement() {
        stunt.setMaxLinearSpeed(0.5);
        stunt.startMoving(Direction.E, 1.0, 100);

        assertEquals(0.5, entity.linearSpeed().x(), DELTA);
    }

    // ─── startTurning ────────────────────────────────────────────────────

    @Test
    void startTurning_positif_definit_une_vitesse_angulaire_positive() {
        boolean ok = stunt.startTurning(90, 1.0);

        assertTrue(ok);
        assertEquals(0.1, entity.angularSpeed(), DELTA);
        assertEquals(900, stunt.actionDuration(), DELTA); // 90 / 0.1
    }

    @Test
    void startTurning_negatif_definit_une_vitesse_angulaire_negative() {
        stunt.startTurning(-90, 1.0);

        assertEquals(-0.1, entity.angularSpeed(), DELTA);
        assertEquals(900,  stunt.actionDuration(), DELTA);
    }

    @Test
    void startTurning_avec_intensite_nulle_utilise_la_vitesse_max() {
        stunt.startTurning(90, 0);

        assertEquals(0.1, entity.angularSpeed(), DELTA);
    }

    @Test
    void startTurning_pendant_une_action_en_cours_renvoie_faux() {
        stunt.startTurning(90, 1.0);

        boolean second = stunt.startTurning(180, 1.0);

        assertFalse(second);
        assertEquals(0.1, entity.angularSpeed(), DELTA);
    }

    @Test
    void setMaxAngularSpeed_modifie_la_vitesse_de_rotation() {
        stunt.setMaxAngularSpeed(0.5);
        stunt.startTurning(90, 1.0);

        assertEquals(0.5, entity.angularSpeed(), DELTA);
        assertEquals(180, stunt.actionDuration(), DELTA); // 90 / 0.5
    }

    // ─── tick ────────────────────────────────────────────────────────────

    @Test
    void tick_sans_action_en_cours_ne_fait_rien() {
        stunt.tick(16);

        assertEquals(0, stunt.actionDuration(), DELTA);
    }

    @Test
    void tick_decremente_la_duree_de_l_action_en_cours() {
        stunt.startMoving(Direction.E, 1.0, 100);

        stunt.tick(40);

        assertEquals(60, stunt.actionDuration(), DELTA);
    }

    @Test
void tick_qui_epuise_l_action_arrete_l_entite() {
    stunt.startMoving(Direction.E, 1.0, 100);

    stunt.tick(100);

    assertEquals(0, stunt.actionDuration(), DELTA);
    // stop() met la vitesse à (0,0), pas à null
    assertEquals(0, entity.linearSpeed().x(), DELTA);
    assertEquals(0, entity.linearSpeed().y(), DELTA);
}

@Test
void tick_qui_depasse_la_duree_clamp_a_zero() {
    stunt.startMoving(Direction.E, 1.0, 100);

    stunt.tick(150);

    assertEquals(0, stunt.actionDuration(), DELTA);
    assertEquals(0, entity.linearSpeed().x(), DELTA);
    assertEquals(0, entity.linearSpeed().y(), DELTA);
}


    @Test
    void tick_termine_permet_de_demarrer_une_nouvelle_action() {
        stunt.startMoving(Direction.E, 1.0, 100);
        stunt.tick(100);

        boolean ok = stunt.startTurning(90, 1.0);

        assertTrue(ok);
    }

    // ─── set ─────────────────────────────────────────────────────────────

    @Test
    void set_xy_deplace_le_centre() {
        stunt.set(10.0, 20.0);

        assertEquals(10, entity.center().x(), DELTA);
        assertEquals(20, entity.center().y(), DELTA);
    }

    @Test
    void set_orientation_pointe_vers_l_angle_cible() {
        stunt.set(90);

        assertEquals(90, entity.orientation());
    }

    @Test
    void set_cell_deplace_l_entite_sur_la_cellule() {
        var cell = Game.grid().cellAt(Game.grid().new Position(3, 4));

        stunt.set(cell);

        assertEquals(3, entity.position().x());
        assertEquals(4, entity.position().y());
    }

    // ─── collision / done ────────────────────────────────────────────────

   @Test
void collision_entity_remet_action_a_zero_et_arrete_l_entite() {
    stunt.startMoving(Direction.E, 1.0, 100);

    stunt.collision((Entity) null);

    assertEquals(0, stunt.actionDuration(), DELTA);
    assertEquals(0, entity.linearSpeed().x(), DELTA);
    assertEquals(0, entity.linearSpeed().y(), DELTA);
}

   @Test
void done_remet_action_a_zero_et_arrete_l_entite() {
    stunt.startMoving(Direction.E, 1.0, 100);

    stunt.done();

    assertEquals(0, stunt.actionDuration(), DELTA);
    assertEquals(0, entity.linearSpeed().x(), DELTA);
    assertEquals(0, entity.linearSpeed().y(), DELTA);
}

    @Test
    void collision_liste_remet_action_a_zero_sans_arreter_l_entite() {
        // collision(List) remet action_ms=0 mais NE STOPPE PAS l'entité
        stunt.startMoving(Direction.E, 1.0, 100);

        stunt.collision(Arrays.asList((Entity) null));

        assertEquals(0, stunt.actionDuration(), DELTA);
        // vitesse inchangée — entity.stop() n'est PAS appelé ici
        assertEquals(0.1, entity.linearSpeed().x(), DELTA);
    }

    // ─── update ──────────────────────────────────────────────────────────

    @Test
    void update_decremente_la_duree_de_l_action_en_cours() {
        stunt.startMoving(Direction.E, 1.0, 100);

        stunt.update(16);

        assertEquals(84, stunt.actionDuration(), DELTA);
    }
    @Test
    void update_termine_l_action_si_le_temps_est_suffisant() {
        stunt.startMoving(Direction.E, 1.0, 100);

        stunt.update(100);

        assertEquals(0, stunt.actionDuration(), DELTA);
        assertEquals(0, entity.linearSpeed().x(), DELTA);
        assertEquals(0, entity.linearSpeed().y(), DELTA);
    }
}
