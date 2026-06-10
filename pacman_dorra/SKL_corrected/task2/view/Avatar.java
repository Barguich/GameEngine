package view;

import engine.Entity;
import game.Game;
import game.Model;
import oop.graphics.Canvas;
import oop.graphics.Graphics;

public abstract class Avatar {

    protected Entity entity;

    private static Canvas canvas;
    private static Model model;
    private static int cellPixel = 24;
    private static int offsetX = 0;
    private static int offsetY = 0;

    public Avatar(Entity entity) {
        this.entity = entity;
    }

    public static void configure(Canvas c, Model m) {
        canvas = c;
        model = m;

        if (canvas != null && model != null) {
            int cw = Math.max(1, canvas.getWidth() / model.grid().width());
            int ch = Math.max(1, canvas.getHeight() / model.grid().height());
            cellPixel = Math.max(8, Math.min(cw, ch));
            offsetX = (canvas.getWidth() - model.grid().width() * cellPixel) / 2;
            offsetY = (canvas.getHeight() - model.grid().height() * cellPixel) / 2;
        }
    }

    protected int cellPixel() {
        return cellPixel;
    }

    protected int xPixel(Entity e) {
        double xCell = e.center().x() / Game.getCmpercell();
        return offsetX + (int) Math.round(xCell * cellPixel);
    }

    protected int yPixel(Entity e) {
        double yCell = e.center().y() / Game.getCmpercell();
        return offsetY + (int) Math.round(yCell * cellPixel);
    }

    protected int xCellPixel(int xCell) {
        return offsetX + xCell * cellPixel;
    }

    protected int yCellPixel(int yCell) {
        return offsetY + yCell * cellPixel;
    }

    public abstract void paint(Graphics g);
}
