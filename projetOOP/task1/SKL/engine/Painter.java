package engine;

import oop.graphics.Canvas;
import oop.tasks.Task;
import oop.tasks.Runtime;

public class Painter {

    private Canvas canvas;

    public Painter(Canvas canvas) {
        this.canvas = canvas;
    }

    public void start() {
        Task painter = Runtime.newTask("Painter");
        painter.post(new oop.tasks.Runnable() {
            @Override
            public void run() {
                canvas.repaint();
                Task.task().post(this,40);
            }
        });
    }
}
