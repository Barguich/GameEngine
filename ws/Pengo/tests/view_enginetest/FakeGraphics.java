package view_enginetest;

import java.util.ArrayList;
import java.util.List;

import oop.graphics.BufferedImage;
import oop.graphics.Color;
import oop.graphics.Font;
import oop.graphics.Graphics;

/**
 * Graphics minimal pour les tests : enregistre les appels (fillRect,
 * setClip, setColor, transformations) au lieu de dessiner réellement.
 */
public class FakeGraphics implements Graphics {

	/** Une demande de remplissage de rectangle. */
	public static class Rect {
		public final int x, y, w, h;

		public Rect(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
	}

	public final List<Rect> filledRects = new ArrayList<>();
	public final List<Rect> clips = new ArrayList<>();
	public final List<Color> colors = new ArrayList<>();
	public final List<Object> transforms = new ArrayList<>();

	private Color currentColor;
	private Font currentFont;
	private Object currentTransform = new Object();

	@Override
	public BufferedImage load(String path) {
		return null;
	}

	@Override
	public Color getColor(int a, int r, int g, int b) {
		return new FakeColor(a, r, g, b);
	}

	@Override
	public Font getFont(String name, int styles, int size) {
		return new FakeFont(name, size);
	}

	@Override
	public Font getFont() {
		return currentFont;
	}

	@Override
	public void setFont(Font f) {
		this.currentFont = f;
	}

	@Override
	public void drawString(String str, int x, int y) {
	}

	@Override
	public Color getColor() {
		return currentColor;
	}

	@Override
	public void setColor(Color c) {
		this.currentColor = c;
		this.colors.add(c);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
	}

	@Override
	public void drawRect(int x, int y, int w, int h) {
	}

	@Override
	public void fillRect(int x, int y, int w, int h) {
		filledRects.add(new Rect(x, y, w, h));
	}

	@Override
	public void drawOval(int x, int y, int w, int h) {
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		clips.add(new Rect(x, y, width, height));
	}

	@Override
	public void drawImage(BufferedImage img, int x, int y) {
	}

	@Override
	public void drawImage(BufferedImage img, int x, int y, int width, int height) {
	}

	@Override
	public void rotate(double theta) {
	}

	@Override
	public void scale(double sx, double sy) {
	}

	@Override
	public void translate(int x, int y) {
	}

	@Override
	public void shear(double shx, double shy) {
	}

	@Override
	public Object getTransform() {
		return currentTransform;
	}

	@Override
	public void setTransform(Object o) {
		this.currentTransform = o;
		this.transforms.add(o);
	}
}
