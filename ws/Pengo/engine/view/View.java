package view;

import java.util.ArrayList;

import engine.Game;
import model.Entity;
import model.Model;
import oop.graphics.Canvas;
import oop.graphics.Graphics;
import pengo.model.PengoModel;

/**
 * Couche de rendu du moteur : observe le {@link Model} et le projette à l'écran
 * à travers un {@link ViewPort} (caméra). Suit éventuellement une entité.
 *
 * @apiNote {@code update()} et {@code paint()} ont des responsabilités
 *          disjointes : {@code update()} mute la position caméra,
 *          {@code paint()}
 *          ne fait que dessiner. Ne jamais déplacer la logique de l'un vers
 *          l'autre — cela casse la séparation MVCB.
 */

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

	/** Désigne l'entité que la caméra recentre à chaque {@code update()}. */
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
	 * Recentre le viewport sur l'entité suivie, puis clamp pour rester dans la
	 * map.
	 *
	 * @apiNote lit les dimensions via {@code Game.game()} à chaque appel plutôt
	 *          que de les cacher en champ : indispensable pour supporter des
	 *          maps de taille variable.
	 * @implNote seul endroit de la View qui mute l'état caméra ; appelé depuis
	 *           la boucle logique, jamais depuis {@code paint()}.
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

		// Clamping Non-Tore
		x = Math.max(0, Math.min(x, mapW - vpW));
		y = Math.max(0, Math.min(y, mapH - vpH));

		viewPort.MoveTo(x, y);
	}

	/**
	 * Rend la scène complète pour une frame : fond, entités visibles, puis HUD.
	 *
	 * @apiNote ordre de rendu imposé. Les bounding boxes sont dessinées dans le
	 *          clip du viewpor. Le panneau de debug est
	 *          dessiné après le déclip pour s'afficher plein écran.
	 * @implNote {@code paint()} ne mute pas le modèle. La seule mutation est
	 *           {@code debug.begin()}, confinée au sous-système de mesure.
	 */
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

	/* alimentation du tick rate */
	public DebugOverlay debug() {
		return this.debug;
	}

	@Override
	public void revoked(Canvas canvas) {
	}

}
