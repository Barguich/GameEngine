import java.awt.Dimension;
import java.util.List;

import engine.Game;
import gal.GalBuilder;
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

public class MainGALTest {

	public static void main(String[] args) {
		try {
			List<Automaton> automates = GalBuilder.loadAutomata("Pengo/gal/demo/test/test.gal");
			Automaton pengoAuto = automates.get(0);
			System.out.println("Automate chargé : " + pengoAuto.name());

			Game game = new Game(20, 13);
			Model model = new Model(Game.grid());

			Entity player = new Entity("PengoBot");
			player.setPosition(Game.grid().new Position(10, 6)); // Au centre
			player.setSize(Game.grid().new Dimension(1, 1));

			GALStunt stunt = new GALStunt(model, player);
			player.setStunt(stunt);

			GALBot bot = new GALBot(player);
			bot.set(pengoAuto);
			bot.stunt(stunt);
			player.setBot(bot);

			model.add(player);

			double mapW = game.width_cm;
			double mapH = game.height_cm;

			ViewPort viewPort = new ViewPort(30, 20, mapW, mapH);
			View view = new View(model, viewPort);

			view.follow(player);

			ShapeAvatar playerAvatar = new ShapeAvatar(player, ShapeAvatar.Shape.OVAL, 255, 255, 200, 0);
			player.setAvatar(playerAvatar);
			playerAvatar.setView(view);

			int winW = (int) (mapW * game.pixelPerCm);
			int winH = (int) (mapH * game.pixelPerCm);

			Runtime.boot(new Dimension(winW, winH), () -> {
				Canvas canvas = (Canvas) Task.task().find("canvas");
				canvas.set(view);
				new Painter(canvas).run();
				new Ticker(model, view).run();
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
