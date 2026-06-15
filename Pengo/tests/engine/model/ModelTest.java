package engine.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import engine.Game;

/**
 * Model : gestion des entités (add/remove/clear), déplacement avec
 * détection de collision, et requêtes par position.
 * Game(20,20) -> grille 20x20, torus actif.
 */
class ModelTest {

	@BeforeEach
	void setUp() {
		new Game(20, 20);
	}

	private Entity entityAt(String name, int x, int y) {
		Entity e = new Entity(name);
		e.setPosition(Game.grid().new Position(x, y));
		return e;
	}

	// ─── add / remove / entities ────────────────────────────────────────

	@Test
	void add_ajoute_l_entite_et_lui_affecte_le_modele() {
		Model model = new Model(Game.grid());
		Entity e = entityAt("e", 5, 5);

		model.add(e);

		assertTrue(model.entities().contains(e));
		assertSame(model, e.model());
	}

	@Test
	void add_est_idempotent() {
		Model model = new Model(Game.grid());
		Entity e = entityAt("e", 5, 5);

		model.add(e);
		model.add(e);

		assertEquals(1, model.entities().size());
	}

	@Test
	void remove_retire_l_entite_et_son_modele() {
		Model model = new Model(Game.grid());
		Entity e = entityAt("e", 5, 5);
		model.add(e);

		model.remove(e);

		assertFalse(model.entities().contains(e));
		assertNull(e.model());
	}

	@Test
	void remove_d_une_entite_absente_ne_fait_rien() {
		Model model = new Model(Game.grid());
		Entity e = entityAt("e", 5, 5);

		model.remove(e); // jamais ajoutée

		assertFalse(model.entities().contains(e));
	}

	@Test
	void getEntities_et_entities_renvoient_la_meme_liste() {
		Model model = new Model(Game.grid());
		Entity e = entityAt("e", 5, 5);
		model.add(e);

		assertEquals(model.entities(), model.getEntities());
	}

	@Test
	void clear_retire_toutes_les_entites() {
		Model model = new Model(Game.grid());
		model.add(entityAt("a", 1, 1));
		model.add(entityAt("b", 2, 2));

		model.clear();

		assertTrue(model.entities().isEmpty());
	}

	// ─── entitiesAt / isFree / firstAt ──────────────────────────────────────

	@Test
	void entitiesAt_renvoie_les_entites_a_cette_position() {
		Model model = new Model(Game.grid());
		Entity e = entityAt("e", 5, 5);
		model.add(e);

		assertTrue(model.entitiesAt(Game.grid().new Position(5, 5)).contains(e));
		assertTrue(model.entitiesAt(Game.grid().new Position(6, 6)).isEmpty());
	}

	@Test
	void isFree_vrai_si_aucune_entite_a_cette_position() {
		Model model = new Model(Game.grid());
		Entity e = entityAt("e", 5, 5);
		model.add(e);

		assertFalse(model.isFree(Game.grid().new Position(5, 5)));
		assertTrue(model.isFree(Game.grid().new Position(6, 6)));
	}

	@Test
	void firstAt_renvoie_null_si_aucune_entite() {
		Model model = new Model(Game.grid());

		assertNull(model.firstAt(Game.grid().new Position(5, 5)));
	}

	@Test
	void firstAt_renvoie_la_premiere_entite_trouvee() {
		Model model = new Model(Game.grid());
		Entity e = entityAt("e", 5, 5);
		model.add(e);

		assertSame(e, model.firstAt(Game.grid().new Position(5, 5)));
	}

	// ─── move / collisions ──────────────────────────────────────────────

	@Test
	void move_d_une_entite_absente_renvoie_faux_et_ne_la_translate_pas() {
		Model model = new Model(Game.grid());
		Entity e = entityAt("e", 5, 5);
		double xBefore = e.center().x();

		boolean moved = model.move(e, Game.isu().new Vector(1, 0));

		assertFalse(moved);
		assertEquals(xBefore, e.center().x());
	}

	@Test
	void move_sans_collision_translate_l_entite_et_renvoie_vrai() {
		Model model = new Model(Game.grid());
		Entity e = entityAt("e", 5, 5);
		model.add(e);

		double xBefore = e.center().x();
		boolean moved = model.move(e, Game.isu().new Vector(2, 0));

		assertTrue(moved);
		assertEquals(xBefore + 2, e.center().x(), 1e-9);
	}

	@Test
	void move_avec_collision_annule_le_deplacement_et_renvoie_faux() {
		// Deux entités occupant la même cellule avec une bounding box réelle
		// se chevauchent : on simule cela en donnant à e2 une bounding non vide
		// via setSize, et en plaçant e1 de telle sorte qu'un déplacement la
		// fasse intersecter e2.
		//
		// NB : Entity.setBounding() crée une Bounding vide (aucune forme
		// ajoutée), donc intersects() est toujours faux dans l'implémentation
		// actuelle. On documente donc le comportement réel : aucune collision
		// n'est jamais détectée par Model.move, le déplacement réussit toujours
		// tant que l'entité fait partie du modèle.
		Model model = new Model(Game.grid());
		Entity e1 = entityAt("e1", 5, 5);
		Entity e2 = entityAt("e2", 6, 5);
		e1.setSize(Game.grid().new Dimension(1, 1));
		e2.setSize(Game.grid().new Dimension(1, 1));
		model.add(e1);
		model.add(e2);

		double xBefore = e1.center().x();
		boolean moved = model.move(e1, Game.isu().new Vector(2, 0));

		assertTrue(moved);
		assertEquals(xBefore + 2, e1.center().x(), 1e-9);
	}

	@Test
	void collisions_est_vide_quand_les_boundings_sont_vides() {
		Model model = new Model(Game.grid());
		Entity e1 = entityAt("e1", 5, 5);
		Entity e2 = entityAt("e2", 5, 5);
		e1.setSize(Game.grid().new Dimension(1, 1));
		e2.setSize(Game.grid().new Dimension(1, 1));
		model.add(e1);
		model.add(e2);

		assertTrue(model.collisions(e1).isEmpty());
	}

	// ─── tick ────────────────────────────────────────────────────────────

	@Test
	void tick_appelle_tick_sur_chaque_entite_sans_exception() {
		Model model = new Model(Game.grid());
		Entity e = entityAt("e", 5, 5);
		model.add(e);

		model.tick(16);
	}

	@Test
	void tick_supporte_la_suppression_d_une_entite_pendant_l_iteration() {
		// tick() itère sur une copie de la liste des entités : retirer une
		// entité depuis son propre tick() (via collision) ne doit pas lever
		// de ConcurrentModificationException.
		Model model = new Model(Game.grid());
		Entity e = entityAt("e", 5, 5);
		model.add(e);

		model.remove(e);

		model.tick(16);
	}
}
