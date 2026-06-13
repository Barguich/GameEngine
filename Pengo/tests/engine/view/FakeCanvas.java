package engine.view;

import oop.graphics.BufferedImage;
import oop.graphics.Canvas;

/**
 * Canvas minimal pour les tests : dimensions fixes en pixels, pas
 * d'interaction graphique réelle.
 */
public class FakeCanvas implements Canvas {

	private final int width;
	private final int height;

	public FakeCanvas(int width, int height) {
		this.width = width;
		this.height = height;
	}


	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void repaint() {
	}

	@Override
	public void set(PaintListener l) {
	}

	@Override
	public void set(KeyListener l) {
	}

	@Override
	public void set(MouseListener l) {
	}

	@Override
	public String[] listFontNames() {
		return new String[0];
	}

	@Override
	public void setCursor(java.awt.image.BufferedImage img) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'setCursor'");
	}
}
