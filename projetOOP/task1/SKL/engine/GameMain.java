package engine;

import game.PacMan;
import oop.graphics.Canvas;
import oop.graphics.Graphics;
import oop.graphics.VirtualKeyCodes;
import oop.tasks.Runtime;
import oop.tasks.Runnable;

import java.awt.*;

public class GameMain implements Runnable {

    private Model model;
    private View view;

    public static void main(String[] args) {
        Dimension d = new Dimension(800, 600);
        Runnable r = new GameMain();
        Runtime.boot(d, r, true);
    }

    @Override
    public void run() {
        Canvas canvas = (Canvas) Runtime.task().find("canvas");
        new Game(100, 100);
        model = new Model();
        PacMan pacman = new PacMan();
        BasicStunt stunt = new BasicStunt(model, pacman);
        pacman.stunt(stunt);

        KeyboardBot bot = new KeyboardBot(pacman);
        pacman.bot(bot);

        pacman.avatar(new PacmanAvatar(pacman));
        model.add(pacman);

        view = new View(model);

        GamePainter painter = new GamePainter(view);
        canvas.set(painter);


        canvas.set(new Canvas.KeyListener() {

            @Override
            public void pressed(Canvas canvas, int keyCode, char keyChar) {
                switch (keyCode) {
                    case VirtualKeyCodes.VK_LEFT:
                        bot.direction(180);
                        break;

                    case VirtualKeyCodes.VK_RIGHT:
                        bot.direction(0);
                        break;

                    case VirtualKeyCodes.VK_UP:
                        bot.direction(270);
                        break;

                    case VirtualKeyCodes.VK_DOWN:
                        bot.direction(90);
                        break;
                }
            }

            @Override
            public void released(Canvas canvas, int keyCode, char keyChar) {
            }

            @Override
            public void typed(Canvas canvas, char keyChar) {
            }
        });

        new Painter(canvas).start();
        new Ticker(model).start();
    }


}
