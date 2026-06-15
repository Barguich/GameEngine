package engine;

import oop.tasks.Task;
import oop.tasks.Runtime;

public class Ticker {

    private Model model;

    public Ticker(Model model) {
        this.model = model;
    }

    public void start() {
        Task ticker = Runtime.newTask("Ticker");
        ticker.post(new oop.tasks.Runnable() {
            long last = System.currentTimeMillis();

            @Override
            public void run() {
                long now = System.currentTimeMillis();
                double elapsed = now - last;
                last = now;
                model.tick(elapsed);
                Task.task().post(this,(int) 1000/120);
            }
        });
    }
}
