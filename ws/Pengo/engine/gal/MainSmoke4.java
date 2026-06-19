package gal;

import java.awt.Dimension;
import java.util.List;

import engine.Game;
import gal.arguments.Category;
import gal.aut.Automaton;
import gal_engine.GALBot;
import gal_engine.GALStunt;
import model.Entity;
import model.Model;
import model.Ticker;
import oop.graphics.Canvas;
import oop.tasks.Runtime;
import oop.tasks.Task;
import view.Painter;
import view.ShapeAvatar;
import view.View;
import view.ViewPort;

public class MainSmoke4 {

	public static void main(String[] args) {
		try {
			List<Automaton> automates = GalBuilder.loadAutomata("ws/Pengo/gal/demo/test/test.gal");
			Automaton chaserAuto = automates.get(0);
			System.out.println("Automate charge : " + chaserAuto.name());

			Game game = new Game(20, 13);
			Model model = new Model(Game.grid());

			// --- Cible "P", statique ---
			Entity target = new Entity("Target");
			target.setCategory(Category.PLAYER);
			target.setPosition(Game.grid().new Position(15, 6));
			target.setSize(Game.grid().new Dimension(1, 1));
			model.add(target);

			// --- Chasseur, pilote par le GAL ---
			Entity hunter = new Entity("Hunter");
			hunter.setCategory(Category.A);
			hunter.setPosition(Game.grid().new Position(5, 6));
			hunter.setSize(Game.grid().new Dimension(1, 1));

			GALStunt stunt = new GALStunt(model, hunter);
			hunter.setStunt(stunt);

			GALBot bot = new GALBot(hunter);
			bot.set(chaserAuto);
			bot.stunt(stunt);
			hunter.setBot(bot);

			model.add(hunter);

			double mapW = game.width_cm;
			double mapH = game.height_cm;
			ViewPort viewPort = new ViewPort(mapW, mapH, mapW, mapH);
			View view = new View(model, viewPort);

			ShapeAvatar targetAvatar = new ShapeAvatar(target, ShapeAvatar.Shape.OVAL, 255, 255, 220, 0);
			target.setAvatar(targetAvatar);
			targetAvatar.setView(view);

			ShapeAvatar hunterAvatar = new ShapeAvatar(hunter, ShapeAvatar.Shape.OVAL, 255, 255, 0, 0);
			hunter.setAvatar(hunterAvatar);
			hunterAvatar.setView(view);

			int winW = (int) (mapW * game.pixelPerCm);
			int winH = (int) (mapH * game.pixelPerCm);

			Runtime.boot(new Dimension(winW, winH), () -> {
				Canvas canvas = (Canvas) Task.task().find("canvas");
				canvas.set(view);
				new Painter(canvas).run();
				new Ticker(model, view).run();
				final double SPEED_CM_S = 10.0;
				final geometry.ISU isu = engine.Game.isu();

				canvas.set(new Canvas.KeyListener() {
					@Override
					public void pressed(Canvas canvas, int keyCode, char keyChar) {
						switch (keyCode) {
							case oop.graphics.VirtualKeyCodes.VK_UP:
							case oop.graphics.VirtualKeyCodes.VK_Z:
								target.setLinearSpeed(isu.new Vector(0, -SPEED_CM_S));
								target.turnTo(270);
								break;
							case oop.graphics.VirtualKeyCodes.VK_DOWN:
							case oop.graphics.VirtualKeyCodes.VK_S:
								target.setLinearSpeed(isu.new Vector(0, SPEED_CM_S));
								target.turnTo(90);
								break;
							case oop.graphics.VirtualKeyCodes.VK_LEFT:
							case oop.graphics.VirtualKeyCodes.VK_Q:
								target.setLinearSpeed(isu.new Vector(-SPEED_CM_S, 0));
								target.turnTo(180);
								break;
							case oop.graphics.VirtualKeyCodes.VK_RIGHT:
							case oop.graphics.VirtualKeyCodes.VK_D:
								target.setLinearSpeed(isu.new Vector(SPEED_CM_S, 0));
								target.turnTo(0);
								break;
							default:
								break;
						}
					}

					@Override
					public void released(Canvas canvas, int keyCode, char keyChar) {
						switch (keyCode) {
							case oop.graphics.VirtualKeyCodes.VK_UP:
							case oop.graphics.VirtualKeyCodes.VK_DOWN:
							case oop.graphics.VirtualKeyCodes.VK_LEFT:
							case oop.graphics.VirtualKeyCodes.VK_RIGHT:
							case oop.graphics.VirtualKeyCodes.VK_Z:
							case oop.graphics.VirtualKeyCodes.VK_S:
							case oop.graphics.VirtualKeyCodes.VK_Q:
							case oop.graphics.VirtualKeyCodes.VK_D:
								target.stop();
								break;
							default:
								break;
						}
					}

					@Override
					public void typed(Canvas canvas, char keyChar) {
					}
				});
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
