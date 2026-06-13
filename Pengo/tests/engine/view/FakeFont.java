package engine.view;

import oop.graphics.Font;

/** Police minimale pour les tests. */
public class FakeFont implements Font {

	private final String name;
	private final int size;

	public FakeFont(String name, int size) {
		this.name = name;
		this.size = size;
	}

	@Override
	public int getSize() {
		return size;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getHeight() {
		return size;
	}

	@Override
	public int getLeading() {
		return 0;
	}

	@Override
	public int getAscent() {
		return size;
	}

	@Override
	public int getDescent() {
		return 0;
	}

	@Override
	public int getWidth(char c) {
		return size;
	}

	@Override
	public int getWidth(String s) {
		return s == null ? 0 : s.length() * size;
	}
}
