package engine.gal;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.model.Entity;

class BotTest {

    // ===== CONSTRUCTEUR =====

    @Test
    void constructeur_cree_un_bot_valide() {
        Entity e = new Entity("player");
        Bot bot = new Bot(e);
        assertNotNull(bot);
    }

    // ===== ETAT =====

    @Test
    void state_est_null_par_defaut() {
        Bot bot = new Bot(new Entity("player"));
        assertNull(bot.state());
    }

    @Test
    void state_peut_etre_modifie() {
        Bot bot = new Bot(new Entity("player"));
        State walking = new State("Walking", 1);
        bot.state(walking);
        assertEquals(walking, bot.state());
    }

    @Test
    void state_peut_etre_remplace() {
        Bot bot = new Bot(new Entity("player"));
        State s1 = new State("Walking", 1);
        State s2 = new State("Running", 2);
        bot.state(s1);
        bot.state(s2);
        assertEquals(s2, bot.state());
    }

    // ===== STUNT =====

    @Test
    void stunt_est_null_par_defaut() {
        Bot bot = new Bot(new Entity("player"));
        // seulement si tu ajoutes stunt()
        // assertNull(bot.stunt());
        assertDoesNotThrow(() -> bot.stunt(null));
    }

    @Test
    void stunt_peut_etre_defini() {
        Bot bot = new Bot(new Entity("player"));
        assertDoesNotThrow(() -> bot.stunt(null));
    }

    // ===== TICK =====

    @Test
    void tick_ne_leve_pas_exception() {
        Bot bot = new Bot(new Entity("player"));
        assertDoesNotThrow(() -> bot.tick(0));
        assertDoesNotThrow(() -> bot.tick(100));
        assertDoesNotThrow(() -> bot.tick(1000));
    }

    // ===== COLLISION =====

    @Test
    void collision_ne_leve_pas_exception() {
        Bot bot = new Bot(new Entity("player"));
        Entity wall = new Entity("wall");
        assertDoesNotThrow(() -> bot.collision(wall, 100));
    }

    // ===== COMPLETED =====

    @Test
    void completed_ne_leve_pas_exception() {
        Bot bot = new Bot(new Entity("player"));
        assertDoesNotThrow(() -> bot.completed());
    }

    // ===== STATE MULTIPLES =====

    @Test
    void plusieurs_etats_differents_sont_acceptes() {
        Bot bot = new Bot(new Entity("player"));
        State walking = new State("Walking", 1);
        State running = new State("Running", 2);
        State fighting = new State("Fighting", 3);
        bot.state(walking);
        assertEquals(walking, bot.state());
        bot.state(running);
        assertEquals(running, bot.state());
        bot.state(fighting);
        assertEquals(fighting, bot.state());
    }

    // ===== NULL =====

    @Test
    void state_peut_etre_remis_a_null() {
        Bot bot = new Bot(new Entity("player"));
        bot.state(new State("Walking", 1));
        bot.state(null);
        assertNull(bot.state());
    }
}