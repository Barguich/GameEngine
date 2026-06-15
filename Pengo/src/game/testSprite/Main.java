package game.testSprite;

import java.awt.Dimension;

import oop.tasks.Runtime;
import oop.tasks.Runnable;
import oop.tasks.Task;
import oop.graphics.Canvas;

import engine.Game;
import engine.model.Model;
import engine.view.Painter;
import engine.view.View;
import engine.view.ViewPort;

public class Main {
	public static void main(String[] args) {
		Runtime.boot(new Dimension(800, 600), new Runnable() {
			@Override
			public void run() {
				new Game(FixedMap.width(), FixedMap.height());

				Model model = new Model(Game.grid());

				FixedMap.build(model);

				Canvas canvas = (Canvas) Task.task().find("canvas");

				ViewPort viewPort = new ViewPort(
						Game.game().width_cm,
						Game.game().height_cm);
				View view = new View(model, viewPort);
				canvas.set((Canvas.PaintListener) view);

				Painter painter = new Painter(canvas);
				Task.task().post(painter, 0);
			}
		}, true);
	}
}
