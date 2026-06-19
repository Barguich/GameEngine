package view;

import java.util.ArrayList;

import engine.Game;
import model.Entity;
import model.Model;
import oop.graphics.Canvas;
import oop.graphics.Graphics;
import pengo.model.PengoModel;
import oop.graphics.Font;

/**
 * Couche de rendu du moteur : observe le Model et le projette à l'écran
 * à travers un ViewPort.
 */
public class View implements Canvas.PaintListener {

	private final Model model;
	private final ViewPort viewPort;

	private Entity followed;
	private final DebugOverlay debug = new DebugOverlay();

	public View(Model model, ViewPort viewPort) {
		this.model = model;
		this.viewPort = viewPort;
	}

	/** Désigne l'entité que la caméra recentre à chaque update(). */
	public void follow(Entity e) {
		this.followed = e;
	}

	public ViewPort viewPort() {
		return this.viewPort;
	}

	@Override
	public void visible(Canvas canvas) {
	}

	/**
	 * Recentre le viewport sur l'entité suivie, puis clamp pour rester dans la map.
	 */
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

		x = Math.max(0, Math.min(x, mapW - vpW));
		y = Math.max(0, Math.min(y, mapH - vpH));

		viewPort.MoveTo(x, y);
	}

	/**
	 * Rend la scène complète : fond, entités, debug, puis HUD.
	 */
	@Override
	public void paint(Canvas canvas, Graphics g) {
		debug.begin();

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

			if (model instanceof PengoModel pm && pm.isVibrating(e)) {
				int shake = (int) (Math.random() * 5) - 2;
				px += shake;
				py += shake;
			}

			e.avatar().paint(g, px, py, scale);
		}

		debug.paintBoundingBoxes(canvas, g, viewPort, entities);

		// HUD plein écran
		g.setClip(0, 0, canvas.getWidth(), canvas.getHeight());

		debug.paintPanel(canvas, g, followed);

		if (model instanceof PengoModel pm) {
			int scaleD = DebugOverlay.uiScale(canvas);
			int x = 12 * scaleD;
			int lineH = 22 * scaleD;
			int top = (debug.isEnabled() ? debug.panelBottom() + 14 * scaleD : 12 * scaleD);
			int y = top + lineH;
			g.setFont(g.getFont("Monospaced", Font.PLAIN, 15 * scaleD));
			g.setColor(Graphics.Colors.white);

			g.drawString("Score : " + pm.score(), x, y);

			if (pm.player() != null) {
				g.drawString("Lives : " + pm.player().lives(), x, y + lineH);
			}

			g.drawString("Enemies : " + pm.enemiesRemaining(), x, y + 2 * lineH);

			int cx = canvas.getWidth() / 2;
			int cy = canvas.getHeight() / 2;

			if (pm.won()) {
				g.drawString("YOU WIN", cx - 60 * scaleD, cy);
			}

			if (pm.lost()) {
				g.drawString("GAME OVER", cx - 80 * scaleD, cy);
			}
		}
	}

	public DebugOverlay debug() {
		return this.debug;
	}

	@Override
	public void revoked(Canvas canvas) {
	}
}