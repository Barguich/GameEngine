package brain_enginetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import brain.AttackBot;
import engine.Game;
import model.BasicStunt;
import model.Entity;
import model.Model;

/**
 * AttackBot : se dirige vers sa cible en se déplaçant d'abord en x, puis en y.
 * Game(20,20) -> grille 20x20, torus actif.
 */
class AttackBotTest {

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
	void target_a_l_est_fait_marcher_vers_l_est() {
		Entity me = entityAt("me", 5, 5);
		Entity target = entityAt("target", 8, 5);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		AttackBot bot = new AttackBot(stunt, target);
		bot.think();

		assertEquals(0, me.orientation());
		assertEquals(entityStepX(me), me.linearSpeed().x(), DELTA);
		assertEquals(0, me.linearSpeed().y(), DELTA);
	}

	@Test
	void target_a_l_ouest_fait_marcher_vers_l_ouest() {
		Entity me = entityAt("me", 8, 5);
		Entity target = entityAt("target", 5, 5);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		AttackBot bot = new AttackBot(stunt, target);
		bot.think();

		assertEquals(180, me.orientation());
		assertEquals(-entityStepX(me), me.linearSpeed().x(), DELTA);
	}

	@Test
	void target_au_nord_avec_meme_x_fait_marcher_vers_le_nord() {
		Entity me = entityAt("me", 5, 5);
		Entity target = entityAt("target", 5, 8);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		AttackBot bot = new AttackBot(stunt, target);
		bot.think();

		assertEquals(90, me.orientation());
		assertEquals(-entityStepX(me), me.linearSpeed().y(), DELTA);
	}

	@Test
	void target_au_sud_avec_meme_x_fait_marcher_vers_le_sud() {
		Entity me = entityAt("me", 5, 8);
		Entity target = entityAt("target", 5, 5);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		AttackBot bot = new AttackBot(stunt, target);
		bot.think();

		assertEquals(270, me.orientation());
		assertEquals(entityStepX(me), me.linearSpeed().y(), DELTA);
	}

	@Test
	void target_a_la_meme_position_ne_declenche_aucun_mouvement() {
		Entity me = entityAt("me", 5, 5);
		Entity target = entityAt("target", 5, 5);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		AttackBot bot = new AttackBot(stunt, target);
		bot.think();

		assertNull(me.linearSpeed());
	}

	@Test
	void think_ne_fait_rien_si_deja_en_mouvement() {
		Entity me = entityAt("me", 5, 5);
		Entity target = entityAt("target", 8, 5);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		AttackBot bot = new AttackBot(stunt, target);
		bot.think(); // moving devient true, marche vers l'est

		me.setLinearSpeed(Game.isu().new Vector(0, 0));
		bot.think(); // ne doit rien changer car moving == true

		assertEquals(0, me.linearSpeed().x(), DELTA);
		assertEquals(0, me.linearSpeed().y(), DELTA);
	}

	@Test
	void done_reactive_think() {
		Entity me = entityAt("me", 5, 5);
		Entity target = entityAt("target", 8, 5);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		AttackBot bot = new AttackBot(stunt, target);
		bot.think();

		bot.done(); // moving = false

		me.setLinearSpeed(Game.isu().new Vector(0, 0));
		bot.think(); // think doit a nouveau agir

		assertEquals(entityStepX(me), me.linearSpeed().x(), DELTA);
	}

	@Test
	void collision_reactive_think() {
		Entity me = entityAt("me", 5, 5);
		Entity target = entityAt("target", 8, 5);
		BasicStunt stunt = new BasicStunt(model, me);
		me.setStunt(stunt);

		AttackBot bot = new AttackBot(stunt, target);
		bot.think();

		bot.collision(null); // moving = false

		me.setLinearSpeed(Game.isu().new Vector(0, 0));
		bot.think();

		assertEquals(entityStepX(me), me.linearSpeed().x(), DELTA);
	}

	private double entityStepX(Entity e) {
		return e.step().x();
	}
}
