package gal_enginetest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import engine.Game;
import gal.arguments.Direction;
import gal_engine.GALStunt;
import model.Entity;
import model.Model;

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
        entity.setSize(Game.grid().new Dimension(1, 1));
        entity.setPosition(Game.grid().new Position(5, 5));

        model.add(entity);

        stunt = new GALStunt(model, entity);
        entity.setStunt(stunt);
    }

    private void ready() {
        stunt.done();
    }

    @Test
    void stepLength_est_initialise_depuis_entity_step() {
        assertEquals(entity.step().x(), stunt.stepLength(), DELTA);
    }

    @Test
    void setStepLength_modifie_stepLength() {
        stunt.setStepLength(5);
        assertEquals(5, stunt.stepLength(), DELTA);
    }

    @Test
    void actionDuration_initiale_est_nulle() {
        assertEquals(0, stunt.actionDuration(), DELTA);
    }

    @Test
    void startMoving_est() {
        ready();

        assertTrue(stunt.startMoving(Direction.E, 1.0, 100));

        assertEquals(Game.game().cmPerCell / 1000.0, entity.linearSpeed().x(), DELTA);
        assertEquals(0, entity.linearSpeed().y(), DELTA);
        assertEquals(100, stunt.actionDuration(), DELTA);
    }

    @Test
    void startMoving_ouest() {
        ready();

        assertTrue(stunt.startMoving(Direction.W, 1.0, 100));

        assertEquals(-Game.game().cmPerCell / 1000.0, entity.linearSpeed().x(), DELTA);
        assertEquals(0, entity.linearSpeed().y(), DELTA);
    }

    @Test
    void startMoving_nord() {
        ready();

        assertTrue(stunt.startMoving(Direction.N, 1.0, 100));

        assertEquals(0, entity.linearSpeed().x(), DELTA);
        assertEquals(-Game.game().cmPerCell / 1000.0, entity.linearSpeed().y(), DELTA);
    }

    @Test
    void startMoving_sud() {
        ready();

        assertTrue(stunt.startMoving(Direction.S, 1.0, 100));

        assertEquals(0, entity.linearSpeed().x(), DELTA);
        assertEquals(Game.game().cmPerCell / 1000.0, entity.linearSpeed().y(), DELTA);
    }

    @Test
    void startMoving_intensite_partielle() {
        ready();

        assertTrue(stunt.startMoving(Direction.E, 0.5, 100));

        assertEquals((Game.game().cmPerCell / 1000.0) * 0.5, entity.linearSpeed().x(), DELTA);
    }

    @Test
    void startMoving_intensite_nulle_utilise_vitesse_max() {
        ready();

        assertTrue(stunt.startMoving(Direction.E, 0.0, 100));

        assertEquals(Game.game().cmPerCell / 1000.0, entity.linearSpeed().x(), DELTA);
    }

    @Test
    void startMoving_intensite_negative_utilise_vitesse_max() {
        ready();

        assertTrue(stunt.startMoving(Direction.E, -1.0, 100));

        assertEquals(Game.game().cmPerCell / 1000.0, entity.linearSpeed().x(), DELTA);
    }

    @Test
    void startMoving_refuse_si_action_en_cours() {
        ready();

        assertTrue(stunt.startMoving(Direction.E, 1.0, 100));

        assertFalse(stunt.startMoving(Direction.N, 1.0, 50));

        assertEquals(Game.game().cmPerCell / 1000.0, entity.linearSpeed().x(), DELTA);
        assertEquals(0, entity.linearSpeed().y(), DELTA);
    }

    @Test
    void startTurning_positif() {
        ready();

        assertTrue(stunt.startTurning(90, 1.0));

        assertEquals(90, entity.orientation());
        assertEquals(90.0 / (90.0 / 1000.0), stunt.actionDuration(), DELTA);
    }

    @Test
    void startTurning_negatif() {
        ready();

        assertTrue(stunt.startTurning(-90, 1.0));

        assertEquals(270, entity.orientation());
        assertEquals(90.0 / (90.0 / 1000.0), stunt.actionDuration(), DELTA);
    }

    @Test
    void startTurning_intensite_nulle_utilise_vitesse_max() {
        ready();

        assertTrue(stunt.startTurning(90, 0));

        assertEquals(90, entity.orientation());
        assertEquals(90.0 / (90.0 / 1000.0), stunt.actionDuration(), DELTA);
    }

    @Test
    void startTurning_refuse_si_action_en_cours() {
        ready();

        assertTrue(stunt.startTurning(90, 1.0));

        assertFalse(stunt.startTurning(180, 1.0));
    }

    @Test
    void tick_sans_action_reste_a_zero() {
        stunt.tick(10);

        assertEquals(0, stunt.actionDuration(), DELTA);
    }

    @Test
    void tick_decremente_action_en_cours() {
        ready();

        stunt.startMoving(Direction.E, 1.0, 100);

        stunt.tick(10);

        assertEquals(90, stunt.actionDuration(), DELTA);
    }

    @Test
    void tick_termine_action_et_stop() {
        ready();

        stunt.startMoving(Direction.E, 1.0, 100);

        stunt.tick(100);

        assertEquals(0, stunt.actionDuration(), DELTA);
        assertEquals(0, entity.linearSpeed().x(), DELTA);
        assertEquals(0, entity.linearSpeed().y(), DELTA);
    }

    @Test
    void tick_depasse_duree_clamp_zero() {
        ready();

        stunt.startMoving(Direction.E, 1.0, 100);

        stunt.tick(150);

        assertEquals(0, stunt.actionDuration(), DELTA);
        assertEquals(0, entity.linearSpeed().x(), DELTA);
        assertEquals(0, entity.linearSpeed().y(), DELTA);
    }

    @Test
    void update_appelle_tick() {
        ready();

        stunt.startMoving(Direction.E, 1.0, 100);

        stunt.update(16);

        assertEquals(84, stunt.actionDuration(), DELTA);
    }

    @Test
    void set_xy_deplace_le_centre() {
        stunt.set(10.0, 20.0);

        assertEquals(10, entity.center().x(), DELTA);
        assertEquals(20, entity.center().y(), DELTA);
    }

    @Test
    void set_orientation_change_orientation() {
        stunt.set(90);

        assertEquals(90, entity.orientation());
    }

    @Test
    void set_cell_deplace_entite() {
        var cell = Game.grid().cellAt(Game.grid().new Position(3, 4));

        stunt.set(cell);

        assertEquals(3, entity.position().x());
        assertEquals(4, entity.position().y());
    }

    @Test
    void collision_entity_stoppe_action() {
        ready();

        stunt.startMoving(Direction.E, 1.0, 100);

        stunt.collision((Entity) null);

        assertEquals(0, stunt.actionDuration(), DELTA);
        assertEquals(0, entity.linearSpeed().x(), DELTA);
        assertEquals(0, entity.linearSpeed().y(), DELTA);
    }

    @Test
    void done_stoppe_action() {
        ready();

        stunt.startMoving(Direction.E, 1.0, 100);

        stunt.done();

        assertEquals(0, stunt.actionDuration(), DELTA);
        assertEquals(0, entity.linearSpeed().x(), DELTA);
        assertEquals(0, entity.linearSpeed().y(), DELTA);
    }

    @Test
    void collision_liste_remet_action_a_zero() {
        ready();

        stunt.startMoving(Direction.E, 1.0, 100);

        stunt.collision(Arrays.asList((Entity) null));

        assertEquals(0, stunt.actionDuration(), DELTA);
    }

    @Test
    void walk_est() {
        ready();

        stunt.walk(0);

        assertEquals(0, entity.orientation());
        assertEquals(Game.game().cmPerCell / 1000.0, entity.linearSpeed().x(), DELTA);
        assertEquals(0, entity.linearSpeed().y(), DELTA);
    }

    @Test
    void walk_sud() {
        ready();

        stunt.walk(90);

        assertEquals(90, entity.orientation());
        assertEquals(0, entity.linearSpeed().x(), DELTA);
        assertEquals(Game.game().cmPerCell / 1000.0, entity.linearSpeed().y(), DELTA);
    }

    @Test
    void walk_ouest() {
        ready();

        stunt.walk(180);

        assertEquals(180, entity.orientation());
        assertEquals(-Game.game().cmPerCell / 1000.0, entity.linearSpeed().x(), DELTA);
        assertEquals(0, entity.linearSpeed().y(), DELTA);
    }

    @Test
    void walk_nord() {
        ready();

        stunt.walk(270);

        assertEquals(270, entity.orientation());
        assertEquals(0, entity.linearSpeed().x(), DELTA);
        assertEquals(-Game.game().cmPerCell / 1000.0, entity.linearSpeed().y(), DELTA);
    }
}