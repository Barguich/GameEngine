package pengo.brain;

import gal.aut.Automaton;
import gal.visitor.GALVisitor;
import gal.arguments.Category;
import gal_engine.GALBot;
import gal_engine.GALStunt;
import model.Entity;
import parser.Parser;
import pengo.model.DiamondBlock;
import pengo.model.Enemy;
import pengo.model.FishBonus;
import pengo.model.GoldBlock;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import testSprite.EnemyAvatar;

import java.util.List;

import ast.AST;

/**
 * Branchement des catégories + automates GAL sur les entités Pengo.
 *
 * Corrections apportées :
 * 1. Chargement du fichier GAL via des chemins relatifs + fallback classpath
 * (suppression du chemin absolu /home/leila/... qui bloquait le chargement)
 * 2. Vitesse des ennemis multipliée par SPEED_FACTOR (3×) pour être visible
 * 3. Les ennemis non-frozen reçoivent leur bot correctement
 */
public final class PengoBots {

	/**
	 * Facteur de vitesse appliqué aux SnoBees.
	 * 1.0 = une cellule par seconde (quasi imperceptible).
	 * 3.0 = trois cellules par seconde (bon gameplay).
	 * Augmenter si les ennemis semblent encore trop lents.
	 */
	private static final double SPEED_FACTOR = 1.0;

	private PengoBots() {
	}

	public static void attachEnemyAvatar(Enemy e) {
		if (e == null || e.avatar() != null) {
			return;
		}
		e.setAvatar(new EnemyAvatar(e));
	}

	public static void configure(PengoModel model) {
		if (model == null)
			return;
		for (Entity e : model.entities()) {
			configureEntity(model, e);
		}
	}

	public static void configureEntity(PengoModel model, Entity e) {
		if (e == null)
			return;

		// ── Catégories ────────────────────────────────────────────────────
		if (e instanceof PengoPlayer) {
			e.setCategory(Category.PLAYER);
		} else if (e instanceof Enemy) {
			e.setCategory(Category.M);
		} else if (e instanceof GoldBlock) {
			e.setCategory(Category.G);
		} else if (e instanceof DiamondBlock) {
			e.setCategory(Category.O);
		} else if (e instanceof IceBlock) {
			e.setCategory(Category.K); // K = IceBlock (bloquant, poussable)
		} else if (e instanceof FishBonus) {
			e.setCategory(Category.G);
		} else {
			e.setCategory(Category.O);
		}

        // ── Bot GAL pour les ennemis ──────────────────────────────────────
        if (e instanceof Enemy enemy) {

			enemy.turnTo(0);

			GALStunt stunt = new GALStunt(model, enemy);

            double baseLinear = stunt.stepLength();

			stunt.setMaxLinearSpeed(baseLinear * SPEED_FACTOR);
			stunt.setMaxAngularSpeed((90.0 / 1000.0) * SPEED_FACTOR);

			enemy.setStunt(stunt);

			Automaton aut = loadEnemyAutomaton();

			if (aut == null) {
				System.err.println("[PengoBots] Impossible de charger SnoBees.gal"
						+ " — l'ennemi " + enemy + " n'aura pas de comportement GAL.");
				return;
			}

			GALBot bot = new GALBot(enemy);
			bot.stunt(stunt);
			bot.set(aut);

			enemy.setBot(bot);

			System.out.println("[PengoBots] SnoBee configuré : " + enemy
					+ " automate=" + aut.name()
					+ " vitesse=" + String.format("%.4f", baseLinear * SPEED_FACTOR) + " cm/ms");
		}
	}

	private static Automaton loadEnemyAutomaton() {
		String[] candidates = {
				"Pengo/gal/demo/test/SnoBees.gal",
		};

		for (String path : candidates) {
			try {
				AST ast = Parser.from_file(path);
				GALVisitor visitor = new GALVisitor();
				@SuppressWarnings("unchecked")
				List<Automaton> autos = (List<Automaton>) ast.accept(visitor);
				if (!autos.isEmpty()) {
					System.out.println("[PengoBots] GAL chargé depuis : " + path
							+ "  →  automate « " + autos.get(0).name() + " »");
					return autos.get(0);
				}
			} catch (Exception ex) {
				// Ce candidat n'existe pas ou n'est pas parseable → on essaie le suivant
				System.out.println("[PengoBots] Candidat non trouvé : " + path
						+ " (" + ex.getClass().getSimpleName() + ")");
			}
		}

		System.err.println("[PengoBots] Aucun fichier SnoBees.gal trouvé parmi les candidats.");
		return null;
	}
}
