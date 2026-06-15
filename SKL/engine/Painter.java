package engine;

import oop.graphics.Canvas;
import oop.graphics.Graphics;
import oop.tasks.Task;

public class Painter implements Canvas.PaintListener {

    private final View view;
    private Canvas canvas;

    public Painter(View view, Canvas canvas) {
        assert view != null;
        assert canvas != null;
        this.view = view;
        this.canvas = canvas;
        canvas.set(this);  // une seule fois ici
    }

    @Override
    public void visible(Canvas c) {
        this.canvas = c;
        scheduleRepaint();  // démarrer la boucle de repaint
    }

    @Override
    public void paint(Canvas c, Graphics g) {
        // fond bleu très foncé
        g.setColor(g.getColor(255, 0, 0, 20));
        g.fillRect(0, 0, c.getWidth(), c.getHeight());

        view.paint(g);

        // score en blanc
        g.setColor(g.getColor(255, 255, 255, 255));
        g.drawString("Score: " + view.model().score(), 10, 20);

        if (view.model().gameOver()) {
            g.setColor(g.getColor(255, 255, 0, 0));
            g.drawString("GAME OVER", c.getWidth()/2 - 40, c.getHeight()/2);
        }
        if (view.model().win()) {
            g.setColor(g.getColor(255, 0, 255, 0));
            g.drawString("VOUS AVEZ GAGNE !", c.getWidth()/2 - 60, c.getHeight()/2);
        }
        // afficher vies
        g.setColor(g.getColor(255, 255, 255, 0)); // jaune
        for (int i = 0; i < view.model().lives(); i++) {
            g.fillOval(10 + i * 25, c.getHeight() - 30, 18, 18);
        }
    }

    @Override
    public void revoked(Canvas c) {}

    private void scheduleRepaint() {
        Task.task().post(new oop.tasks.Runnable() {
            @Override
            public void run() {
                canvas.repaint();
                Task.task().post(this, 33);  // re-poster le même runnable
            }
        }, 33);
    }

    private void paintGrid(Graphics g, Canvas c) {
        g.setColor(g.getColor(255, 0, 0, 0));        int pixPerCell = (int)(Game.game().cmPerCell * Game.PixelPerCm());
        for (int x = 0; x < c.getWidth(); x += pixPerCell)
            g.drawLine(x, 0, x, c.getHeight());
        for (int y = 0; y < c.getHeight(); y += pixPerCell)
            g.drawLine(0, y, c.getWidth(), y);
    }
}