package model;

import oop.tasks.Task;
import view.View;

public class Ticker implements oop.tasks.Runnable {

	// Période d'exécution du moteur (~60 FPS)
	private static final int PERIOD_MS = 16;

	private final View view;

	private Model model;

	// Sert à calculer le temps écoulé entre deux ticks
	private long lastTime;

	private boolean firstRun = true;

	public Ticker(Model model, View view) {
		this.model = model;
		this.lastTime = System.currentTimeMillis();
		this.view = view;
	}

	@Override
	public void run() {

		// Ignore le premier tick pour initialiser correctement le timer
		if (firstRun) {
			firstRun = false;
		}

		long currentTime = System.currentTimeMillis();

		// Temps écoulé depuis le tick précédent
		long elapsed_ms = currentTime - lastTime;

		this.lastTime = currentTime;

		// Mise à jour du modèle de jeu
		model.tick(elapsed_ms);

		// Mise à jour des statistiques d'affichage
		if (view != null) {
			view.debug().recordTick(elapsed_ms);
		}

		// Reprogramme le prochain tick
		Task.task().post(this, PERIOD_MS);
	}
}