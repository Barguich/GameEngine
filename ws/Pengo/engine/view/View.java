package view;

import java.util.ArrayList;
import java.util.List;

import engine.Game;
import model.Entity;
import model.Model;
import oop.graphics.Canvas;
import oop.graphics.Graphics;

/**
 * Couche de rendu du moteur : observe le Model et le projette à l'écran à
 * travers un ViewPort.
 */
public class View implements Canvas.PaintListener {

	private final Model model;
	private final ViewPort viewPort;
	private final List<Overlay> overlays = new ArrayList<>();
	private final List<EntityRenderHook> renderHooks = new ArrayList<>();

	private Entity followed;
	private final DebugOverlay debug = new DebugOverlay();
	private final MenuOverlay menu = new MenuOverlay();

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

	public void addOverlay(Overlay o){
		overlays.add(o);
	}

	public void addRenderHook(EntityRenderHook h){
		renderHooks.add(h);
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

			for (EntityRenderHook h : renderHooks){
				px += h.offsetX(e);
				py += h.offsetY(e);
			}

			e.avatar().paint(g, px, py, scale);
		}

		debug.paintBoundingBoxes(canvas, g, viewPort, entities);

		// HUD plein écran
		g.setClip(0, 0, canvas.getWidth(), canvas.getHeight());

		debug.paintPanel(canvas, g, followed);

		for (Overlay o : overlays){
			o.paint(canvas, g);
		}
		
	}

	public DebugOverlay debug() {
		return this.debug;
	}

	public MenuOverlay menu() {
		return this.menu;
	}

	@Override
	public void revoked(Canvas canvas) {
	}
	private void drawEntity(Canvas canvas, Graphics g, double scale, Entity e) {
		if (e == null || e.center() == null || e.avatar() == null) {
			return;
		}

		if (!viewPort.contains(e.center())) {
			return;
		}

		int px = viewPort.toPixelX(canvas, e.center().x());
		int py = viewPort.toPixelY(canvas, e.center().y());

		for (EntityRenderHook h : renderHooks) {
			px += h.offsetX(e);
			py += h.offsetY(e);
		}

		e.avatar().paint(g, px, py, scale);
	}
}