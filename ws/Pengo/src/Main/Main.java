package Main;

import java.awt.Dimension;

import oop.graphics.Canvas;
import oop.tasks.Runnable;
import oop.tasks.Runtime;
import oop.tasks.Task;

import engine.Game;
import model.Model;
import testSprite.FixedMap;
import view.Painter;
import view.View;
import view.ViewPort;

public class Main {

	public static void main(String[] args) {

		Runtime.boot(new Dimension(800, 600), new Runnable() {

			@Override
			public void run() {

				new Game(FixedMap.width(), FixedMap.height());

				Model model = new Model(Game.grid());

				FixedMap.build(model);

				Canvas canvas = (Canvas) Task.task().find("canvas");

				if (canvas == null) {
					System.err.println("Canvas introuvable.");
					return;
				}

				ViewPort viewPort = new ViewPort(
						Game.game().width_cm,
						Game.game().height_cm);

				View view = new View(model, viewPort);

				canvas.set((Canvas.PaintListener) view);

				Painter painter = new Painter(canvas);

				Task.task().post(painter, 0);
			}

		}, true);

		System.exit(0);
	}
}
