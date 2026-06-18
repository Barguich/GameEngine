package model;

import oop.tasks.Task;
import view.View;

public class Ticker implements oop.tasks.Runnable {

	private static final int PERIOD_MS = 16;

	private final View view;

	private Model model;
	private long lastTime;
	private boolean firstRun = true;

	public Ticker(Model model, View view) {
		this.model = model;
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
