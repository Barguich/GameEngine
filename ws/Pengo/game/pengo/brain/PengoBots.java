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
import pengo.model.Wall;
import pengo.view.EnemyAvatar;

import java.util.List;

import ast.AST;

/**
 * Branchement des catégories + automates GAL sur les entités Pengo.
 *
 * NE PAS appeler setMaxLinearSpeed / setMaxAngularSpeed ici.
 * La vitesse est entièrement gérée dans GALStunt via MS_PER_CELL.
 */
public final class PengoBots {

	private static final double SPEED_FACTOR = 1.0;
	
	private PengoBots() {
	}

	public static void attachEnemyAvatar(Enemy e) {
		if (e == null || e.avatar() != null)
			return;
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

		// ── Catégories ────────────────────────────────────────────────────────
		if (e instanceof PengoPlayer) {
			e.setCategory(Category.PLAYER);
		} else if (e instanceof DiamondBlock) {
			e.setCategory(Category.D);
		} else if (e instanceof GoldBlock) {
			e.setCategory(Category.G);
		} else if (e instanceof IceBlock) {
			e.setCategory(Category.K);
		} else if (e instanceof Wall) {
			e.setCategory(Category.O);
		} else if (e instanceof Enemy) {
			e.setCategory(Category.A);
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
				System.err.println("[PengoBots] Impossible de charger SnoBees.gal — "
						+ enemy + " sans comportement GAL.");
				return;
			}

			GALBot bot = new GALBot(enemy);
			bot.stunt(stunt);
			bot.set(aut);
			enemy.setBot(bot);

			System.out.println("[PengoBots] SnoBee configuré : " + enemy
					+ "  automate=" + aut.name());
		}
	}

	private static Automaton loadEnemyAutomaton() {
		String[] candidates = {
				"Pengo/gal/demo/test/SnoBees.gal", // depuis ws/
				"gal/demo/test/SnoBees.gal", // depuis ws/Pengo/
				"demo/test/SnoBees.gal", // depuis bin/
				"Automata.gal", // racine ws/
				"../Automata.gal", // depuis ws/Pengo/
		};

		for (String path : candidates) {
			try {
				AST ast = Parser.from_file(path);
				GALVisitor visitor = new GALVisitor();
				@SuppressWarnings("unchecked")
				List<Automaton> autos = (List<Automaton>) ast.accept(visitor);
				if (!autos.isEmpty()) {
					System.out.println("[PengoBots] GAL chargé : " + path
							+ " → « " + autos.get(0).name() + " »");
					return autos.get(0);
				}
			} catch (Exception ex) {
				System.out.println("[PengoBots] Non trouvé : " + path
						+ " (" + ex.getClass().getSimpleName() + ")");
			}
		}

		System.err.println("[PengoBots] Aucun SnoBees.gal trouvé.");
		return null;
	}
}