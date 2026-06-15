package engine;

import oop.graphics.Canvas;
import oop.graphics.Graphics;

public class View {
    private Model model;

    public View(Model model) {
        this.model = model;
    }

    public void paint(Canvas canvas, Graphics g) {
        System.out.println(
                "Entities : " +
                        model.entities().size()
        );
        g.setColor(Graphics.Colors.black);
        g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        for (Entity e : model.entities()){
            if (e.avatar() != null){
                e.avatar().paint(canvas, g);
            }
        }
    }
}
