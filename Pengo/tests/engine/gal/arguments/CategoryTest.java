package engine.gal.arguments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import engine.gal.arguments.Category;

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
	void canonical_d_une_categorie_predefinie_renvoie_null() {
		// Caractérisation : la map statique `categories` n'est jamais peuplée
		// (le bloc static crée les constantes mais ne les enregistre pas dans
		// la map), donc canonical(...) renvoie toujours null.
		assertNull(Category.A.canonical("A"));
	}

	@Test
	void canonical_d_un_nom_inconnu_renvoie_null() {
		assertNull(Category.A.canonical("inconnu"));
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
}
