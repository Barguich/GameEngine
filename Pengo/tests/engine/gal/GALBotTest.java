package engine.gal;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.gal.aut.Automaton;
import engine.model.Entity;

class GALBotTest {

	// ===== CONSTRUCTEUR =====

	@Test
	void constructeur_cree_un_galbot() {
		Entity e = new Entity("player");
		GALBot bot = new GALBot(e);
		assertNotNull(bot);
	}

	// ===== SELECTED ENTITY =====

	@Test
	void selected_est_null_par_defaut() {
		GALBot bot = new GALBot(new Entity("player"));
		assertNull(bot.selected());
	}

	@Test
	void selectedEntity_stocke_l_entite() {
		GALBot bot = new GALBot(new Entity("player"));
		Entity target = new Entity("target");
		bot.selectedEntity(target);
		assertSame(target, bot.selected());
	}

	@Test
	void selectedEntity_peut_etre_remplacee() {
		GALBot bot = new GALBot(new Entity("player"));
		Entity e1 = new Entity("enemy1");
		Entity e2 = new Entity("enemy2");
		bot.selectedEntity(e1);
		assertSame(e1, bot.selected());
		bot.selectedEntity(e2);
		assertSame(e2, bot.selected());
	}

	@Test
	void selectedEntity_peut_etre_null() {
		GALBot bot = new GALBot(new Entity("player"));
		bot.selectedEntity(null);
		assertNull(bot.selected());
	}

	// ===== AUTOMATON =====

	@Test
	void automate_peut_etre_associe() {
		GALBot bot = new GALBot(new Entity("player"));
		State idle = new State("Idle", 0);
		Automaton a = new Automaton("test", idle);
		assertDoesNotThrow(() -> bot.set(a));
	}

	@Test
	void automate_peut_etre_remplace() {
		GALBot bot = new GALBot(new Entity("player"));
		Automaton a1 = new Automaton("a1", new State("S1", 1));
		Automaton a2 = new Automaton("a2", new State("S2", 2));
		bot.set(a1);
		assertDoesNotThrow(() -> bot.set(a2));
	}

	// ===== TICK =====

	@Test
	void tick_avec_automate_ne_leve_pas_exception() {
		Entity e = new Entity("player");
		GALBot bot = new GALBot(e);
		Automaton a = new Automaton("test", new State("Idle", 0));
		bot.set(a);
		assertDoesNotThrow(() -> bot.tick(0));
	}

	@Test
	void tick_plusieurs_fois_ne_leve_pas_exception() {
		Entity e = new Entity("player");
		GALBot bot = new GALBot(e);
		Automaton a = new Automaton("test", new State("Idle", 0));
		bot.set(a);
		assertDoesNotThrow(() -> {
			bot.tick(0);
			bot.tick(10);
			bot.tick(100);
			bot.tick(1000);
		});
	}

	// ===== COLLISION =====
	@Test
	void collision_ne_leve_pas_exception() {
		Entity e = new Entity("player");
		GALBot bot = new GALBot(e);
		Automaton a = new Automaton("test", new State("Idle", 0));
		bot.set(a);
		Entity wall = new Entity("wall");
		assertDoesNotThrow(() -> bot.collision(wall, 100));
	}

	// ===== COMPLETED =====

	@Test
	void completed_ne_leve_pas_exception() {
		Entity e = new Entity("player");
		GALBot bot = new GALBot(e);
		Automaton a = new Automaton("test", new State("Idle", 0));
		bot.set(a);
		assertDoesNotThrow(bot::completed);
	}

	// ===== INTEGRATION BOT =====

	@Test
	void galbot_est_un_bot() {
		GALBot galBot = new GALBot(new Entity("player"));
		assertTrue(galBot instanceof Bot);
	}

	@Test
	void tick_sans_automate_ne_plante_pas() {
		GALBot bot = new GALBot(new Entity("player"));
		assertDoesNotThrow(() -> bot.tick(0));
	}

}