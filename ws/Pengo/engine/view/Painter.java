package view;

import oop.graphics.Canvas;
import oop.tasks.Runnable;
import oop.tasks.Task;

/**
 * Boucle de rendu : redemande un repaint du canvas à intervalle fixe, puis se
 * replanifie. C'est l'horloge des frames, distincte de l'horloge logique
 * ({@code Ticker}).
 *
 */

public class Painter implements Runnable {

	/** Période de repaint en ms (~30 fps à 33 ms). */
	private static final int period_ms = 33;
	private final Canvas canvas;

	public Painter(Canvas canvas) {
		this.canvas = canvas;
	}

	@Override
	public void run() throws Exception {
		canvas.repaint();
		Task.task().post(this, period_ms);
	}

}
