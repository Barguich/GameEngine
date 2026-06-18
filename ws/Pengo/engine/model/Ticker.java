package model;

import oop.graphics.Canvas;
import oop.tasks.Task;
import view.View;

public class Ticker implements oop.tasks.Runnable {

	private static final int PERIOD_MS = 16;

	private final View view;

	private Model model;
	private Canvas canvas;
	private long lastTime;
	private boolean firstRun = true;

	public Ticker(Model model, Canvas canvas,View view) {
		this.model = model;
		this.canvas = canvas;
		this.lastTime = System.currentTimeMillis();
		this.view = view;
	}

	@Override
	public void run() {
		if (firstRun) {
			firstRun = false;
		}

		long currentTime = System.currentTimeMillis();
		long elapsed_ms = currentTime - lastTime;
		this.lastTime = currentTime;

		model.tick(elapsed_ms);

		if (view != null) {
			view.debug().recordTick(elapsed_ms);
		}

		Task.task().post(this, PERIOD_MS);
	}
}
