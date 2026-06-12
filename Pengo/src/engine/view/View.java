package engine.view;

import java.util.ArrayList;

import engine.model.Entity;
import engine.model.Model;
import oop.graphics.Canvas;
import oop.graphics.Graphics;

public class View implements Canvas.PaintListener {

	private final Model model;
	private final ViewPort viewPort;
	private int h_cm;
	private int w_cm;

	public View(Model model, ViewPort viewPort) {
		this.model = model;
		this.viewPort = viewPort;
	}

	public ViewPort viewPort() {
		return this.viewPort;
	}

	@Override
	public void visible(Canvas canvas) {
		this.h_cm = canvas.getHeight();
		this.w_cm = canvas.getWidth();
	}

	@Override
	public void paint(Canvas canvas, Graphics g) {
		g.setColor(Graphics.Colors.darkGray);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		g.setColor(Graphics.Colors.black);
		viewPort.fill(canvas, g);
		double scale = viewPort.scale(canvas);
		for (Entity e : new ArrayList<>(model.getEntities())) {
			if (e.center() == null || e.avatar() == null) {
				continue;
			}
			int px = viewPort.toPixelX(canvas, e.center().x());
			int py = viewPort.toPixelY(canvas, e.center().y());
			e.avatar().paint(g, px, py, scale);
		}
	}

	@Override
	public void revoked(Canvas canvas) {
		w_cm = 0;
		h_cm = 0;
	}

}
