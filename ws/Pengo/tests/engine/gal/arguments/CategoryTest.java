package engine.gal.arguments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Category : table d'interaction partagée (statique) entre catégories et
 * recherche canonique par nom.
 * Les tests qui modifient la table d'interaction restaurent son état initial
 * (false) car {@code interaction} est un champ statique partagé entre toutes
 * les instances.
 */
class CategoryTest {

	@AfterEach
	void resetInteractions() {
		Category.A.setInteraction(Category.A, Category.C, false);
		Category.PLAYER.setInteraction(Category.PLAYER, Category.BOSS, false);
		Category.BOSS.setInteraction(Category.BOSS, Category.PLAYER, false);
	}

	// ─── interaction table ───────────────────────────────────────────────

	@Test
	void deux_categories_n_interagissent_pas_par_defaut() {
		assertFalse(Category.A.interactsWith(Category.C));
	}

	@Test
	void setInteraction_active_l_interaction_dans_un_seul_sens() {
		Category.A.setInteraction(Category.A, Category.C, true);

		assertTrue(Category.A.interactsWith(Category.C));
		assertFalse(Category.C.interactsWith(Category.A));
	}

	@Test
	void setInteraction_peut_etre_invoque_sur_n_importe_quelle_instance() {
		// setInteraction n'est pas statique mais modifie une table statique
		// partagée : l'appeler depuis PLAYER ou BOSS a le même effet global.
		Category.PLAYER.setInteraction(Category.PLAYER, Category.BOSS, true);

		assertTrue(Category.BOSS.interactsWith(Category.PLAYER) == false);
		assertTrue(Category.PLAYER.interactsWith(Category.BOSS));
	}

	// ─── canonical ───────────────────────────────────────────────────────

	@Test
	void canonical_d_un_nom_inconnu_renvoie_null() {
		assertEquals(Category.A, Category.canonical("A"));
	}

	// ─── constantes ──────────────────────────────────────────────────────

	@Test
	void les_constantes_predefinies_sont_non_nulles_et_distinctes() {
		Category[] all = { Category.A, Category.C, Category.D, Category.G, Category.I, Category.J, Category.K,
				Category.M, Category.O, Category.P, Category.T, Category.U, Category.V, Category.PLAYER,
				Category.BOSS, Category.ANY, Category.SELECTED, Category.Q, Category.X, Category.Y, Category.Z };

		for (Category c : all) {
			assertEquals(false, c == null);
		}
	}

	@Test
	void interaction_est_desactivee_apres_reset() {
		Category.A.setInteraction(Category.A, Category.C, true);
		assertTrue(Category.A.interactsWith(Category.C));
		Category.A.setInteraction(Category.A, Category.C, false);
		assertFalse(Category.A.interactsWith(Category.C));
	}

	@Test
	void categories_speciales_existent() {
		assertEquals("@", Category.PLAYER.name());
		assertEquals("#", Category.BOSS.name());
		assertEquals("_", Category.ANY.name());
		assertEquals("$", Category.SELECTED.name());
	}

	@Test
	void interaction_n_est_pas_symetrique() {
		Category.A.setInteraction(Category.A, Category.C, true);
		assertTrue(Category.A.interactsWith(Category.C));
		assertFalse(Category.C.interactsWith(Category.A));
	}

	@Test
	void plusieurs_interactions_peuvent_coexister() {
		Category.A.setInteraction(Category.A, Category.C, true);
		Category.A.setInteraction(Category.A, Category.D, true);
		assertTrue(Category.A.interactsWith(Category.C));
		assertTrue(Category.A.interactsWith(Category.D));
	}

	@Test
	void les_categories_sont_uniques() {
		assertTrue(Category.A == Category.A);
		assertFalse(Category.A == Category.C);
	}
}
