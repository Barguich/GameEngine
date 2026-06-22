package pengo.brain;

import gal.GalBuilder;
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

import java.util.List;

import ast.AST;

/** Branchement des catégories + automates GAL sur les entités Pengo. */
public final class PengoBots {
	private PengoBots() {
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

		if (e instanceof PengoPlayer) {
			e.setCategory(Category.PLAYER);
		} else if (e instanceof Enemy) {
			e.setCategory(Category.M);
		} else if (e instanceof GoldBlock) {
			e.setCategory(Category.G);
		} else if (e instanceof DiamondBlock) {
			e.setCategory(Category.O);
		} else if (e instanceof IceBlock) {
			e.setCategory(Category.O);
		} else if (e instanceof FishBonus) {
			e.setCategory(Category.G);
		} else {
			e.setCategory(Category.O);
		}

		if (e instanceof Enemy enemy) {
			enemy.turnTo(0);

			GALStunt stunt = new GALStunt(model, enemy);
			enemy.setStunt(stunt);

			Automaton aut = loadEnemyAutomaton();

			if (aut == null) {
				System.err.println("[PengoBots] Impossible de charger SnoBee.gal");
				return;
			}

			GALBot bot = new GALBot(enemy);

			bot.stunt(stunt); // IMPORTANT
			bot.set(aut);

			enemy.setBot(bot);

			System.out.println(
					"[PengoBots] SnoBee configuré : "
							+ enemy
							+ " automate="
							+ aut.name());
		}
	}

	private static Automaton loadEnemyAutomaton() {

		String path = "/home/barguich/AgileLearning/ple/ws/Pengo/gal/demo/test/SnoBees.gal";
		try {

			AST ast = Parser.from_file(path);

			GALVisitor visitor = new GALVisitor();

			@SuppressWarnings("unchecked")
			List<Automaton> autos = (List<Automaton>) ast.accept(visitor);

			if (!autos.isEmpty()) {
				System.out.println(
						"[PengoBots] GAL loaded : "
								+ autos.get(0).name());

				return autos.get(0);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
