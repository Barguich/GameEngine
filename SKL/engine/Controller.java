package engine;

import oop.graphics.Canvas;
import oop.graphics.VirtualKeyCodes;

public class Controller implements Canvas.KeyListener {

    private final Brain brain;
    private final Entity player;
    private BasicStunt stunt;

    public Controller(Canvas canvas, Brain brain, Entity player, BasicStunt stunt) {
        assert canvas != null;
        assert player != null;
        this.brain = brain;
        this.player = player;
        this.stunt = stunt;
        canvas.set(this);
    }

    @Override
    public void pressed(Canvas c, int keyCode, char keyChar) {
        switch (keyCode) {
            case VirtualKeyCodes.VK_LEFT  -> stunt.walk(180);  // ouest
            case VirtualKeyCodes.VK_RIGHT -> stunt.walk(0);    // est
            case VirtualKeyCodes.VK_UP    -> stunt.walk(270);  // nord
            case VirtualKeyCodes.VK_DOWN  -> stunt.walk(90);   // sud
        }
        if (brain.model().gameOver() || brain.model().win()) return;
    }

    @Override
    public void released(Canvas c, int keyCode, char keyChar) {}

    @Override
    public void typed(Canvas c, char keyChar) {}
}