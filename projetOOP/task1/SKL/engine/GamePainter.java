package engine;

import oop.graphics.Canvas;
import oop.graphics.Graphics;

public class GamePainter implements Canvas.PaintListener {

    private View view;
    public GamePainter(View view){
        this.view = view;
    }

    @Override
    public void visible(Canvas canvas) {
    }

    @Override
    public void paint(Canvas canvas, Graphics g) {
        view.paint(canvas, g);
    }

    @Override
    public void revoked(Canvas canvas) {
    }

}
