package engine.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import engine.Game;
import engine.model.Entity;

/**
 * Avatar : constructeur, setView, et helpers protégés saveTransform /
 * restoreTransform (testés via une sous-classe concrète FakeAvatar).
 */
class AvatarTest {

	@BeforeEach
	void setUp() {
		new Game(20, 20);
	}

	@Test
	void le_constructeur_memorise_l_entite() {
		Entity e = new Entity("e");

		FakeAvatar avatar = new FakeAvatar(e);

		assertSame(e, avatar.entity());
	}

	@Test
	void view_est_null_avant_setView() {
		Entity e = new Entity("e");
		FakeAvatar avatar = new FakeAvatar(e);

		assertNull(avatar.view());
	}

	@Test
	void setView_memorise_la_vue() {
		Entity e = new Entity("e");
		FakeAvatar avatar = new FakeAvatar(e);
		View view = new View(new engine.model.Model(Game.grid()), new ViewPort(74, 74));

		avatar.setView(view);

		assertSame(view, avatar.view());
	}

	@Test
	void paint_est_delegue_a_la_sous_classe() {
		Entity e = new Entity("e");
		FakeAvatar avatar = new FakeAvatar(e);
		FakeGraphics g = new FakeGraphics();

		avatar.paint(g, 10, 20, 2.5);

		assertEquals(1, avatar.paintCount);
		assertEquals(10, avatar.lastXPix);
		assertEquals(20, avatar.lastYPix);
		assertEquals(2.5, avatar.lastScale, 1e-9);
	}

	@Test
	void saveTransform_renvoie_la_transformation_courante_du_graphics() {
		Entity e = new Entity("e");
		FakeAvatar avatar = new FakeAvatar(e);
		FakeGraphics g = new FakeGraphics();

		Object saved = avatar.callSaveTransform(g);

		assertSame(g.getTransform(), saved);
	}

	@Test
	void restoreTransform_reapplique_la_transformation_sauvegardee() {
		Entity e = new Entity("e");
		FakeAvatar avatar = new FakeAvatar(e);
		FakeGraphics g = new FakeGraphics();
		Object saved = avatar.callSaveTransform(g);

		Object other = new Object();
		g.setTransform(other);
		assertSame(other, g.getTransform());

		avatar.callRestoreTransform(g, saved);

		assertSame(saved, g.getTransform());
	}
}
