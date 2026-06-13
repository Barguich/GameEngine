package engine.brain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import engine.Game;
import engine.brain.FleeBot;
import engine.model.BasicStunt;
import engine.model.Entity;
import engine.model.Model;

/**
 * FleeBot : s'éloigne de l'ennemi, en se déplaçant en direction opposée à
 * AttackBot. Game(20,20) -> grille 20x20, torus actif.
 */
class FleeBotTest {

	private static final double DELTA = 1e-9;

	private Model model;

	@BeforeEach
	void setUp() {
		new Game(20, 20);
		model = new Model(Game.grid());
	}

	private Entity entityAt(String name, int x, int y) {
		Entity e = new Entity(name);
		e.setPosition(Game.grid().new Position(x, y));
		e.setStep(Game.grid().new Dimension(1, 1));
		model.add(e);
		return e;
	}

	@Test
	void ennemi_a_l_est_fait_fuir_vers_l_ouest() {
		Entity me = entityAt("me", 5, 5);
		Entity enemy = entityAt("enemy", 8, 5);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		FleeBot bot = new FleeBot(stunt, enemy);
		bot.think();

		assertEquals(180, me.orientation());
		assertEquals(-entityStepX(me), me.linearSpeed().x(), DELTA);
	}

	@Test
	void ennemi_a_l_ouest_fait_fuir_vers_l_est() {
		Entity me = entityAt("me", 8, 5);
		Entity enemy = entityAt("enemy", 5, 5);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		FleeBot bot = new FleeBot(stunt, enemy);
		bot.think();

		assertEquals(0, me.orientation());
		assertEquals(entityStepX(me), me.linearSpeed().x(), DELTA);
	}

	@Test
	void ennemi_au_nord_avec_meme_x_fait_fuir_vers_le_sud() {
		Entity me = entityAt("me", 5, 5);
		Entity enemy = entityAt("enemy", 5, 8);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		FleeBot bot = new FleeBot(stunt, enemy);
		bot.think();

		assertEquals(270, me.orientation());
		assertEquals(entityStepX(me), me.linearSpeed().y(), DELTA);
	}

	@Test
	void ennemi_au_sud_avec_meme_x_fait_fuir_vers_le_nord() {
		Entity me = entityAt("me", 5, 8);
		Entity enemy = entityAt("enemy", 5, 5);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		FleeBot bot = new FleeBot(stunt, enemy);
		bot.think();

		assertEquals(90, me.orientation());
		assertEquals(-entityStepX(me), me.linearSpeed().y(), DELTA);
	}

	@Test
	void ennemi_a_la_meme_position_ne_declenche_aucun_mouvement() {
		Entity me = entityAt("me", 5, 5);
		Entity enemy = entityAt("enemy", 5, 5);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		FleeBot bot = new FleeBot(stunt, enemy);
		bot.think();

		assertNull(me.linearSpeed());
	}

	@Test
	void think_ne_fait_rien_si_deja_en_mouvement() {
		Entity me = entityAt("me", 5, 5);
		Entity enemy = entityAt("enemy", 8, 5);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		FleeBot bot = new FleeBot(stunt, enemy);
		bot.think(); // moving devient true, fuit vers l'ouest

		me.setLinearSpeed(Game.isu().new Vector(0, 0));
		bot.think(); // ne doit rien changer car moving == true

		assertEquals(0, me.linearSpeed().x(), DELTA);
		assertEquals(0, me.linearSpeed().y(), DELTA);
	}

	@Test
	void done_reactive_think() {
		Entity me = entityAt("me", 5, 5);
		Entity enemy = entityAt("enemy", 8, 5);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		FleeBot bot = new FleeBot(stunt, enemy);
		bot.think();

		bot.done(); // moving = false

		me.setLinearSpeed(Game.isu().new Vector(0, 0));
		bot.think();

		assertEquals(-entityStepX(me), me.linearSpeed().x(), DELTA);
	}

	@Test
	void collision_reactive_think() {
		Entity me = entityAt("me", 5, 5);
		Entity enemy = entityAt("enemy", 8, 5);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		FleeBot bot = new FleeBot(stunt, enemy);
		bot.think();

		bot.collision(null); // moving = false

		me.setLinearSpeed(Game.isu().new Vector(0, 0));
		bot.think();

		assertEquals(-entityStepX(me), me.linearSpeed().x(), DELTA);
	}

	private double entityStepX(Entity e) {
		return e.step().x();
	}
}
