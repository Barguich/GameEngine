package view;

/**
 * Horloge de rendu : mesure le FPS réel observé entre deux frames.
 *
 * <p>C'est le seul endroit du package view qui accumule de l'état temporel
 * mutable. On l'isole ici plutôt que dans View pour que View.paint() reste
 * structurellement un orchestrateur de rendu sans logique d'état.
 *
 */
final class FrameClock {

	/** Facteur de lissage EMA : 0.1 = réactif mais stable à l'œil. */
	private static final double SMOOTHING = 0.1;

	private long lastFrameNanos = 0;
	private double smoothedFps = 0.0;
	private double lastFrameMs = 0.0;

	/**
	 * À appeler une seule fois par frame, au tout début du rendu.
	 * Calcule le delta depuis la frame précédente et met à jour le FPS lissé.
	 */
	void onFrame() {
		long now = System.nanoTime();

		if (lastFrameNanos == 0) {
			lastFrameNanos = now;
			return; // première frame : pas de delta exploitable
		}

		long deltaNanos = now - lastFrameNanos;
		lastFrameNanos = now;

		if (deltaNanos <= 0) {
			return; // garde-fou : horloge non monotone ou frame instantanée
		}

		lastFrameMs = deltaNanos / 1_000_000.0;
		double instantFps = 1_000_000_000.0 / deltaNanos;

		// Amorçage : la première vraie valeur initialise directement la moyenne.
		smoothedFps = (smoothedFps == 0.0)
				? instantFps
				: smoothedFps + SMOOTHING * (instantFps - smoothedFps);
	}

	double fps() {
		return smoothedFps;
	}

	double lastFrameMs() {
		return lastFrameMs;
	}
}