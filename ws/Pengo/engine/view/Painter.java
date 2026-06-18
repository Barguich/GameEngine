package view;

import oop.graphics.Canvas;
import oop.tasks.Runnable;
import oop.tasks.Task;

public class Painter implements Runnable {

	private static final int period_ms = 33;
	private final Canvas canvas;

	public Painter(Canvas canvas){
		this.canvas = canvas;
	}
	@Override
	public void run() throws Exception {
		canvas.repaint();
		Task.task().post(this,period_ms);
	}

}
