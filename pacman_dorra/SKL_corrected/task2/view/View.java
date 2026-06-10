package view;

import engine.Entity;
import engine.Grid;
import entities.PacMan;
import game.Model;
import oop.graphics.Canvas;
import oop.graphics.Font;
import oop.graphics.Graphics;

public class View implements Canvas.PaintListener, Canvas.KeyListener {

    private Model model;
    private Canvas canvas;
    private boolean alive;
    private PacMan player;

    public View(Model model) {
        assert model != null;
        this.model = model;
        this.player = model.pacman();
    }

    @Override
    public void visible(Canvas canvas) {
        this.canvas = canvas;
        this.alive = true;
        canvas.set((Canvas.KeyListener) this);
        canvas.repaint();
        scheduleRepaint();
    }

    private void scheduleRepaint() {
        oop.tasks.Runtime.post(new oop.tasks.Runnable() {
            @Override
            public void run() {
                if (!alive || canvas == null) {
                    return;
                }

                model.tick(30);
                canvas.repaint();
                scheduleRepaint();
            }
        }, 30);
    }

    @Override
    public void paint(Canvas canvas, Graphics g) {
        Avatar.configure(canvas, model);

        g.setColor(g.getColor(255, 0, 0, 0));
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (Entity e : model.entities()) {
            if (e.avatar() != null && !(e instanceof PacMan)) {
                e.avatar().paint(g);
            }
        }

        if (player != null && player.avatar() != null) {
            player.avatar().paint(g);
        }

        drawHUD(canvas, g);
    }

    private void drawHUD(Canvas canvas, Graphics g) {
        Font old = g.getFont();
        Font font = g.getFont("Arial", Font.BOLD, 16);
        g.setFont(font);
        g.setColor(g.getColor(255, 255, 255, 255));
        g.drawString("Score: " + model.score(), 12, 20);
        g.drawString("Lives: " + model.lives(), 12, 42);

        if (model.gameOver()) {
            drawMessage(canvas, g, "GAME OVER");
        } else if (model.win()) {
            drawMessage(canvas, g, "YOU WIN");
        }

        g.setFont(old);
    }

    private void drawMessage(Canvas canvas, Graphics g, String text) {
        Font old = g.getFont();
        Font font = g.getFont("Arial", Font.BOLD, 32);
        g.setFont(font);

        int w = Math.max(260, font.getWidth(text) + 50);
        int h = 90;
        int x = (canvas.getWidth() - w) / 2;
        int y = (canvas.getHeight() - h) / 2;

        g.setColor(g.getColor(220, 0, 0, 0));
        g.fillRect(x, y, w, h);
        g.setColor(g.getColor(255, 255, 255, 0));
        g.drawRect(x, y, w, h);
        g.setColor(g.getColor(255, 255, 255, 255));
        g.drawString(text, x + 25, y + 55);

        g.setFont(old);
    }

    @Override
    public void revoked(Canvas canvas) {
        alive = false;
        this.canvas = null;
    }

    @Override
    public void pressed(Canvas canvas, int keyCode, char keyChar) {
        if (player == null || model.gameOver() || model.win() || player.isDying()) {
            return;
        }

        int dx = 0;
        int dy = 0;
        int orientation = player.orientation();

        if (keyCode == 37) {
            dx = -1;
            orientation = 180;
        } else if (keyCode == 39) {
            dx = 1;
            orientation = 0;
        } else if (keyCode == 38) {
            dy = -1;
            orientation = 90;
        } else if (keyCode == 40) {
            dy = 1;
            orientation = 270;
        } else {
            return;
        }

        player.turnTo(orientation);

        if (model.moveEntityByCell(player, dx, dy)) {
            player.markMoving();
        }

        model.tick(30);
        canvas.repaint();
    }

    @Override
    public void released(Canvas canvas, int keyCode, char keyChar) {
    }

    @Override
    public void typed(Canvas canvas, char keyChar) {
    }
}
