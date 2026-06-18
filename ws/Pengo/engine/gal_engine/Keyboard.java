package gal_engine;

import gal.arguments.Key;

public class Keyboard {

	private static Keyboard self;

	public static Keyboard self() {
		if (self == null)
			self = new Keyboard();
		return self;
	}

	// FIELDS
	private final boolean[] down = new boolean[65536];
	private final boolean[] releasedPending = new boolean[65536];

	private Keyboard() {
	}

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

	public boolean isDown(Key k) {
		return down[k.keyCode()];
	}

	public boolean consumeRelease(Key k) {
		boolean r = releasedPending[k.keyCode()];
		releasedPending[k.keyCode()] = false;
		return r;
	}
}
