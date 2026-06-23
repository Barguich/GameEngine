package gal.visitor;

import java.util.List;

import ast.AST;
import engine.Game;
import gal.arguments.Category;
import gal.aut.Automaton;
import gal_engine.State;
import model.Entity;
import model.Model;
import parser.Parser;

/**
 * Test isolé de l'interpréteur GAL — sans fenêtre graphique.
 *
 * Scénario simulé pour Patrol.gal :
 * - Une entité placée en (5,5), orientée vers l'Est (0°)
 * - Deux entités Obstacle placées en (8,5) et (7,4)
 * - Les cases (6,5) et (7,5) sont libres (Void)
 *
 * Comportement attendu :
 * steps 0-1 : Step(F,V) vrai → Move(F) déclenché, état reste Patrol
 * step 2 : Step(F,V) faux (obstacle en 8,5) → Turn(R) déclenché
 * step 3+ : la rotation change l'orientation → Step(F,V) recalculé
 *
 * Lancement :
 * java gal.visitor.TestGAL /chemin/vers/Patrol.gal
 */
public class TestGAL {

	public static void main(String[] args) throws Exception {

		// ── 1. Chemin .gal ───────────────────────────────────────────────
		String path = args.length > 0 ? args[0] : "Pengo/gal/demo/test/SnoBees.gal";

		// ── 2. Parsing ──────────────────────────────────────────────────
		System.out.println("=== Parsing : " + path + " ===");
		AST ast = Parser.from_file(path);
		System.out.println("Parsing OK\n");

		// ── 3. Visite → Automaton ────────────────────────────────────────
		GALVisitor visitor = new GALVisitor();
		@SuppressWarnings("unchecked")
		List<Automaton> automata = (List<Automaton>) ast.accept(visitor);

		Automaton patrol = automata.get(0);
		System.out.println("Automate : " + patrol.name());
		System.out.println("État initial : " + patrol.current());

		// ── 4. Grille et modèle (pas de fenêtre) ─────────────────────────
		Game game = new Game(20, 13);
		Model model = new Model(Game.grid());

		// Entité contrôlée par l'automate — orientée Est (0°)
		Entity patrol_entity = new Entity("Patroller");
		patrol_entity.setPosition(Game.grid().new Position(5, 5));
		patrol_entity.setSize(Game.grid().new Dimension(1, 1));
		patrol_entity.turnTo(0); // regarde vers l'Est = Forward = droite
		model.add(patrol_entity);

		// Obstacle devant à 3 cases (col 8, ligne 5)
		Entity obs1 = new Entity("Wall1");
		obs1.setCategory(Category.O);
		obs1.setPosition(Game.grid().new Position(8, 5));
		obs1.setSize(Game.grid().new Dimension(1, 1));
		model.add(obs1);

		// Obstacle un peu plus loin (pour bloquer après un éventuel Turn)
		Entity obs2 = new Entity("Wall2");
		obs2.setCategory(Category.O);
		obs2.setPosition(Game.grid().new Position(7, 4));
		obs2.setSize(Game.grid().new Dimension(1, 1));
		model.add(obs2);

		// ── 5. Simulation ────────────────────────────────────────────────
		System.out.println("\n=== Simulation (15 steps) ===");
		System.out.printf("%-8s %-12s %-12s %-10s %-20s%n",
				"step", "état avant", "état après", "transition", "orientation");
		System.out.println("-".repeat(65));

		for (int i = 0; i < 15; i++) {
			State before = patrol.current();
			int orientBefore = patrol_entity.orientation();

			boolean fired = patrol.step(patrol_entity);
			System.out.println("[GALBot] step → fired=" + fired);

			State after = patrol.current();
			int orientAfter = patrol_entity.orientation();

			System.out.printf("%-8d %-12s %-12s %-10s %d° → %d°%n",
					i,
					before != null ? before.toString() : "(mort)",
					after != null ? after.toString() : "(mort)",
					fired ? "OK" : "AUCUNE",
					orientBefore, orientAfter);

			// Simule un déplacement d'une case vers l'avant si Move a été déclenché
			// (dans le vrai moteur c'est le Stunt+Ticker qui le fait)
			if (fired && orientAfter == orientBefore) {
				// pas de rotation → on suppose Move → avance d'une case
				int dx = 0, dy = 0;
				switch (orientAfter) {
					case 0:
						dx = 1;
						break; // Est
					case 90:
						dy = 1;
						break; // Sud
					case 180:
						dx = -1;
						break; // Ouest
					case 270:
						dy = -1;
						break; // Nord
				}
				if (dx != 0 || dy != 0) {
					patrol_entity.translate(Game.grid().new Vector(dx, dy));
					System.out.printf("         → position maintenant : %s%n",
							patrol_entity.position());
				}
			}
		}

		System.out.println("\nTest terminé.");
	}
}
