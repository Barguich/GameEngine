package engine.gal;

import engine.gal.arguments.Key;

/**
 * @apiNote état du clavier, alimenté par le Controller, consulté par les
 *          conditions GAL KeyP/KeyR.
 * @implNote singleton via self(), même pattern que Game.game().
 * @implNote pas de souci de concurrence : l'EventPump est mono-thread,
 *           événements clavier et ticks sont sérialisés.
 */
public class Keyboard {

	private static Keyboard self;

	public static Keyboard self() {
		if (self == null)
			self = new Keyboard();
		return self;
	}

	// FIELDS — indexés par keyCode
	private final boolean[] down = new boolean[65536];
	private final boolean[] releasedPending = new boolean[65536];

	private Keyboard() {
	}

	// === alimenté par le Controller ===
	public void pressed(int keyCode) {
		if (keyCode >= 0 && keyCode < down.length)
			down[keyCode] = true;
	}

	public void released(int keyCode) {
		if (keyCode >= 0 && keyCode < down.length) {
			down[keyCode] = false;
			releasedPending[keyCode] = true;
		}
	}

	// === consulté par les conditions GAL ===
	/** @apiNote vrai tant que la touche est enfoncée (état). */
	public boolean isDown(Key k) {
		return down[k.keyCode()];
	}

	/**
	 * @apiNote vrai si un événement « relâchée » est en attente ;
	 *          consommé à la lecture (événement, pas état).
	 */
	public boolean consumeRelease(Key k) {
		boolean r = releasedPending[k.keyCode()];
		releasedPending[k.keyCode()] = false;
		return r;
	}
}
