package view_enginetest;

import oop.graphics.Color;

/** Couleur minimale pour les tests. */
public class FakeColor implements Color {

	private final int a;
	private final int r;
	private final int g;
	private final int b;

	public FakeColor(int a, int r, int g, int b) {
		this.a = a;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	@Override
	public int alpha() {
		return a;
	}

	@Override
	public int red() {
		return r;
	}

	@Override
	public int green() {
		return g;
	}

	@Override
	public int blue() {
		return b;
	}
}
