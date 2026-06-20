package pengo.brain;

import gal.GalBuilder;
import gal.aut.Automaton;
import gal.arguments.Category;
import gal_engine.GALBot;
import gal_engine.GALStunt;
import model.Entity;
import pengo.model.Enemy;
import pengo.model.FishBonus;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;

import java.util.List;

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
		if (e instanceof PengoPlayer)
			e.setCategory(Category.PLAYER);
		else if (e instanceof Enemy)
			e.setCategory(Category.M);
		else if (e instanceof IceBlock)
			e.setCategory(Category.O);
		else if (e instanceof FishBonus)
			e.setCategory(Category.G);
		else
			e.setCategory(Category.O);

		if (e instanceof Enemy) {
			e.setStunt(new GALStunt(model, e));
			GALBot bot = new GALBot(e);
			Automaton aut = loadEnemyAutomaton();
			if (aut != null)
				bot.set(aut);
			e.setBot(bot);
			e.turnTo(0);
		}
	}

	private static Automaton loadEnemyAutomaton() {

	    String[] paths = {
	        "../Pengo/gal/demo.test/SnoBees.gal",
	        "gal/demo.test/SnoBees.gal",
	        "demo.test/SnoBees.gal"
	    };

	    for (String p : paths) {
	        try {
	            System.out.println("Trying GAL file : " + p);

	            List<Automaton> autos = GalBuilder.loadAutomata(p);

	            if (!autos.isEmpty()) {
	                System.out.println("GAL loaded : " + p);
	                return autos.get(0);
	            }

	        } catch (Throwable ex) {
	            System.out.println("Failed : " + p);
	        }
	    }

	    System.out.println("NO GAL FILE FOUND");
	    return null;
	}
}
