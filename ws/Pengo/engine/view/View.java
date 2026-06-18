package view;

import java.util.ArrayList;

import engine.Game;
import model.Entity;
import model.Model;
import oop.graphics.Canvas;
import oop.graphics.Graphics;
import pengo.model.PengoModel;

public class View implements Canvas.PaintListener {

	private final Model model;
	private final ViewPort viewPort;
	private int h_cm;
	private int w_cm;

	private Entity followed;
	private final DebugOverlay debug = new DebugOverlay();

	public View(Model model, ViewPort viewPort) {
		this.model = model;
		this.viewPort = viewPort;
	}

	public void follow(Entity e) {
		this.followed = e;
	}

	public ViewPort viewPort() {
		return this.viewPort;
	}

	@Override
	public void visible(Canvas canvas) {
	}

	public void update() {
		if (followed == null || followed.center() == null) {
			return;
		}

		double vpW = viewPort.getWidth_cm();
		double vpH = viewPort.getHeight_cm();
		double mapW = Game.game().width_cm;
		double mapH = Game.game().height_cm;

		double x = followed.center().x() - vpW / 2.0;
		double y = followed.center().y() - vpH / 2.0;

		// Clamping Non-Tore
		x = Math.max(0, Math.min(x, mapW - vpW));
		y = Math.max(0, Math.min(y, mapH - vpH));

		viewPort.MoveTo(x, y);
	}

	@Override
	public void paint(Canvas canvas, Graphics g) {

		debug.begin();

		// On calcule l'origine idéale (centrée sur followed),
		// puis on la clamp pour que le viewport reste dans la map

		g.setColor(Graphics.Colors.darkGray);
		g.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		g.setColor(Graphics.Colors.black);
		viewPort.fill(canvas, g);
		viewPort.clip(canvas, g);
		double scale = viewPort.scale(canvas);
		ArrayList<Entity> entities = new ArrayList<>(model.getEntities());
		for (Entity e : entities) {
			if (e.center() == null || e.avatar() == null) {
				continue;
			}
			if (!viewPort.contains(e.center())) {
				continue;
			}
			int px = viewPort.toPixelX(canvas, e.center().x());
			int py = viewPort.toPixelY(canvas, e.center().y());

			// Vibration si PengoModel l'indique
			if (model instanceof PengoModel pm && pm.isVibrating(e)) {
				int shake = (int) (Math.random() * 5) - 2;
				px += shake;
				py += shake;
			}
			e.avatar().paint(g, px, py, scale);
		}
		debug.paintBoundingBoxes(canvas, g, viewPort, entities);

		// On retire le clip avant le HUD pour qu'il s'affiche plein écran.
		g.setClip(0, 0, canvas.getWidth(), canvas.getHeight());
		debug.paintPanel(canvas, g, followed);

	}

	public DebugOverlay debug() {
		return this.debug;
	}

	@Override
	public void revoked(Canvas canvas) {
	}

}
