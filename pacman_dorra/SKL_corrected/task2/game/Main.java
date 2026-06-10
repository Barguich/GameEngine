package game;

import oop.graphics.Canvas;
import oop.tasks.Runtime;
import view.View;
import oop.tasks.Runnable;

public class Main {

    public static void main(String[] args) {

        Game gameConfig = new Game(20, 15);

        Runtime.boot(new java.awt.Dimension(gameConfig.pict().width(), gameConfig.pict().height()), new Runnable() {

            @Override
            public void run() {

                Game game = gameConfig;
                Model model = new Model(game);
                model.init();

                Canvas canvas = (Canvas) Runtime.task().find("canvas");

                View view = new View(model);
                canvas.set((Canvas.PaintListener) view);
            }
        });
    }
}
