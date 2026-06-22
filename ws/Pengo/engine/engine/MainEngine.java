package engine;

import java.util.List;

import ast.AST;
import gal.arguments.Category;
import gal.aut.Automaton;
import gal.visitor.GALVisitor;
import gal_engine.GALBot;
import gal_engine.GALStunt;
import geometry.ISU;
import model.Entity;
import model.Model;
import model.Ticker;
import oop.graphics.Canvas;
import oop.graphics.VirtualKeyCodes;
import oop.tasks.Task;
import parser.Parser;
import pengo.model.IceBlock;
import pengo.model.PengoPlayer;
import view.Painter;
import view.ShapeAvatar;
import view.View;
import view.ViewPort;
import oop.tasks.Runtime;

public class MainEngine {

	private static final double SPEED_CM_S = 10.0;

	public static void main(String[] args) {
		Game game = new Game(20, 13);
		Model model = new Model(Game.grid());

		double mapW = game.width_cm;
		double mapH = game.height_cm;

		ViewPort viewPort = new ViewPort(mapW, mapH, mapW, mapH);
		View view = new View(model, viewPort);
		view.debug().setEnabled(true);

		// ── Joueur contrôlé au clavier ────────────────────────────────────────
		PengoPlayer player = new PengoPlayer();
		player.setPosition(Game.grid().new Position(5, 5));
		player.setSize(Game.grid().new Dimension(1, 1));
		model.add(player);

		ShapeAvatar playerAvatar = new ShapeAvatar(player, ShapeAvatar.Shape.OVAL, 255, 220, 220, 0);
		player.setAvatar(playerAvatar);
		playerAvatar.setView(view);

		// ── Bloc de glace ─────────────────────────────────────────────────────
		IceBlock ice = new IceBlock();
		ice.setPosition(Game.grid().new Position(8, 5));
		ice.setSize(Game.grid().new Dimension(1, 1));
		model.add(ice);

		ShapeAvatar iceAvatar = new ShapeAvatar(ice, ShapeAvatar.Shape.RECT, 255, 120, 180, 255);
		ice.setAvatar(iceAvatar);
		iceAvatar.setView(view);

		// ── Mur fixe ─────────────────────────────────────────────────────────
		Entity wall = new Entity("Wall");
		wall.setCategory(Category.O);
		wall.setPosition(Game.grid().new Position(12, 5));
		wall.setSize(Game.grid().new Dimension(1, 1));
		model.add(wall);

		ShapeAvatar wallAvatar = new ShapeAvatar(wall, ShapeAvatar.Shape.RECT, 255, 120, 120, 120);
		wall.setAvatar(wallAvatar);
		wallAvatar.setView(view);

		// ── Bot GAL : entité autonome pilotée par Patrol.gal ─────────────────
		Entity patroller = new Entity("Patroller");
		patroller.setCategory(Category.A);  // catégorie Adversaire (rouge)
		patroller.setPosition(Game.grid().new Position(2, 3));
		patroller.setSize(Game.grid().new Dimension(2, 1));
		patroller.turnTo(0); // orienté vers l'Est au départ
		model.add(patroller);

		ShapeAvatar patrollerAvatar = new ShapeAvatar(patroller, ShapeAvatar.Shape.RECT, 255, 255, 80, 80);
		patroller.setAvatar(patrollerAvatar);
		patrollerAvatar.setView(view);

		// Charger et construire l'automate GAL
		try {
			// ⚠ Adapte ce chemin vers ton fichier .gal
			AST ast = Parser.from_file("/home/barguich/AgileLearning/ple/ws/Pengo/gal/demo/test/SnoBees.gal");

			GALVisitor visitor = new GALVisitor();
			@SuppressWarnings("unchecked")
			List<Automaton> automata = (List<Automaton>) ast.accept(visitor);
			Automaton patrol = automata.get(0);

			// Connecter bot + stunt à l'entité
			GALStunt stunt = new GALStunt(model, patroller);
			patroller.setStunt(stunt);

			GALBot bot = new GALBot(patroller);
			bot.stunt(stunt);
			bot.set(patrol);
			patroller.setBot(bot);

			System.out.println("Automate GAL chargé : " + patrol.name());

		} catch (Exception ex) {
			System.err.println("Impossible de charger l'automate GAL : " + ex.getMessage());
			ex.printStackTrace();
		}

		// ── Vue ───────────────────────────────────────────────────────────────
		view.follow(player);

		int winW = (int) (mapW * game.pixelPerCm);
		int winH = (int) (mapH * game.pixelPerCm);

		Runtime.boot(
			new java.awt.Dimension(winW, winH),
			(oop.tasks.Runnable) () -> {
				Canvas canvas = (Canvas) Task.task().find("canvas");
				canvas.set(view);
				new Painter(canvas).run();
				new Ticker(model, view).run();

				canvas.set(new Canvas.KeyListener() {
					@Override
					public void pressed(Canvas canvas, int keyCode, char keyChar) {
						ISU isu = Game.isu();
						double s = SPEED_CM_S;
						switch (keyCode) {
							case VirtualKeyCodes.VK_UP:
							case VirtualKeyCodes.VK_Z:
								player.setLinearSpeed(isu.new Vector(0, -s));
								player.turnTo(270);
								break;
							case VirtualKeyCodes.VK_DOWN:
							case VirtualKeyCodes.VK_S:
								player.setLinearSpeed(isu.new Vector(0, s));
								player.turnTo(90);
								break;
							case VirtualKeyCodes.VK_LEFT:
							case VirtualKeyCodes.VK_Q:
								player.setLinearSpeed(isu.new Vector(-s, 0));
								player.turnTo(180);
								break;
							case VirtualKeyCodes.VK_RIGHT:
							case VirtualKeyCodes.VK_D:
								player.setLinearSpeed(isu.new Vector(s, 0));
								player.turnTo(0);
								break;
						}
					}

					@Override
					public void released(Canvas canvas, int keyCode, char keyChar) {
						switch (keyCode) {
							case VirtualKeyCodes.VK_UP:
							case VirtualKeyCodes.VK_DOWN:
							case VirtualKeyCodes.VK_LEFT:
							case VirtualKeyCodes.VK_RIGHT:
							case VirtualKeyCodes.VK_Z:
							case VirtualKeyCodes.VK_S:
							case VirtualKeyCodes.VK_Q:
							case VirtualKeyCodes.VK_D:
								player.stop();
								break;
						}
					}

					@Override
					public void typed(Canvas canvas, char keyChar) {}
				});
			});
	}
}