package view;

import java.util.List;

import collision.Box;
import model.Entity;
import oop.graphics.Canvas;
import oop.graphics.Color;
import oop.graphics.Font;
import oop.graphics.Graphics;

/**
 * HUD de debug overlay dessiné par-dessus le rendu normal de la scène.
 *
 * <p>
 * Affiche : FPS réel (frames de rendu), durée de la dernière frame, tick
 * rate logique (fourni par View), bounding boxes des entités, et l'action
 * inférée de l'entité suivie.
 *
 * <p>
 * Contraintes respectées :
 * <ul>
 * <li>Game-agnostic : aucun import de game.* ou pengo.* — ne dépend que de
 * engine/model, engine/collision et oop.graphics.</li>
 * <li>paint() ne mute pas le modèle : l'overlay ne lit que des données
 * publiques et ne touche jamais l'état des entités.</li>
 * <li>Le seul état mutable (FrameClock) est confiné et mis à jour
 * explicitement par tick de rendu via begin().</li>
 * </ul>
 */
public class DebugOverlay {

	private static final int PANEL_X = 12;
	private static final int PANEL_Y = 12;
	private static final int PANEL_W = 260;
	private static final int PANEL_LINES = 5;
	private static final int LINE_H = 18;
	private static final int PADDING = 10;

	private final FrameClock frameClock = new FrameClock();

	private boolean enabled = false;

	// Couleurs et police résolues paresseusement (besoin d'un Graphics vivant).
	private Color hudBg;
	private Color hudText;
	private Color bbColor;
	private Font hudFont;
	private int cachedFontSize = -1;
	private int lastPanelBottom = PANEL_Y;

	// Tick rate logique poussé depuis l'extérieur (mesuré par la boucle Ticker).
	private double lastTickMs = 0.0;
	private long tickCount = 0;

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public void toggle() {
		this.enabled = !this.enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Alimente le HUD avec le temps logique mesuré côté boucle de simulation.
	 * À appeler depuis le code qui orchestre le tick (ex. via View) à chaque
	 * pas de modèle. On garde frame (rendu) et tick (logique) séparés : ce sont
	 * deux horloges distinctes (Painter ~30Hz, Ticker ~60Hz).
	 */
	public void recordTick(long elapsedMs) {
		this.lastTickMs = elapsedMs;
		this.tickCount++;
	}

	/**
	 * À appeler au tout début de View.paint(), avant tout dessin.
	 * Avance l'horloge de frame (unique mutation tolérée du cycle de rendu).
	 */
	public void begin() {
		if (!enabled) {
			return;
		}
		frameClock.onFrame();
	}

	/**
	 * Dessine les bounding boxes. À appeler APRÈS le clip du viewport et le
	 * rendu des entités, mais AVANT de retirer le clip, pour que les BB soient
	 * clippées comme les sprites.
	 */
	public void paintBoundingBoxes(Canvas canvas, Graphics g, ViewPort viewPort,
			List<Entity> entities) {
		if (!enabled) {
			return;
		}

		resolveColors(g);
		g.setColor(bbColor);

		for (Entity e : entities) {
			if (e == null || e.box() == null) {
				continue;
			}
			drawBox(canvas, g, viewPort, e.box());
		}
	}

	/**
	 * Dessine le panneau de texte (FPS / tick / action). À appeler EN DERNIER,
	 * après avoir retiré le clip du viewport, pour que le HUD s'affiche en
	 * surimpression plein écran et ne soit pas rogné.
	 */
	public void paintPanel(Canvas canvas, Graphics g, Entity followed) {
		if (!enabled) {
			return;
		}

		int scale = uiScale(canvas);
		resolveColors(g);
		resolveFont(g, scale);

		int padding = PADDING * scale;
		int lineH = LINE_H * scale;
		int panelX = PANEL_X * scale;
		int panelY = PANEL_Y * scale;
		int panelW = PANEL_W * scale;

		int panelH = padding * 2 + PANEL_LINES * lineH;

		g.setColor(hudBg);
		g.fillRect(panelX, panelY, panelW, panelH);

		g.setFont(hudFont);
		g.setColor(hudText);

		int tx = panelX + padding;
		int ty = panelY + padding + lineH - 4 * scale;

		drawLine(g, tx, ty, 0, lineH, String.format(
				"FPS   %5.1f  (%.1f ms/frame)",
				frameClock.fps(), frameClock.lastFrameMs()));
		drawLine(g, tx, ty, 1, lineH, String.format(
				"TICK  #%d  (%.0f ms)", tickCount, lastTickMs));
		drawLine(g, tx, ty, 2, lineH, String.format(
				"CANVAS %dx%d px", canvas.getWidth(), canvas.getHeight()));

		String followedName = (followed == null) ? "—" : safeName(followed);
		drawLine(g, tx, ty, 3, lineH, "FOLLOW " + followedName);
		drawLine(g, tx, ty, 4, lineH, "ACT   " + ActionInference.describe(followed));
		lastPanelBottom = panelY + panelH;
	}

	// ─── helpers de rendu ────────────────────────────────────────────────────

	private void drawLine(Graphics g, int x, int yBase, int index, int lineH, String text) {
		g.drawString(text, x, yBase + index * lineH);
	}

	private void drawBox(Canvas canvas, Graphics g, ViewPort vp, Box box) {
		int x1 = vp.toPixelX(canvas, box.xmin());
		int y1 = vp.toPixelY(canvas, box.ymin());
		int x2 = vp.toPixelX(canvas, box.xmax());
		int y2 = vp.toPixelY(canvas, box.ymax());
		g.drawRect(Math.min(x1, x2), Math.min(y1, y2),
				Math.abs(x2 - x1), Math.abs(y2 - y1));
	}

	private String safeName(Entity e) {
		// Entity n'expose pas forcément getName() ; on retombe sur toString().
		String s = e.toString();
		return (s == null) ? "?" : s;
	}

	private void resolveColors(Graphics g) {
		if (hudBg == null) {
			hudBg = g.getColor(180, 0, 0, 0);
		}
		if (hudText == null) {
			hudText = g.getColor(255, 0, 255, 90);
		}
		if (bbColor == null) {
			bbColor = g.getColor(200, 255, 60, 60);
		}
	}

	private void resolveFont(Graphics g, int scale) {
		int fontSize = 13 * scale;
		if (hudFont == null || cachedFontSize != fontSize) {
			hudFont = g.getFont("Monospaced", Font.PLAIN, fontSize);
			cachedFontSize = fontSize;
		}
	}

	/** Résout couleurs et police une seule fois (nécessite un Graphics vivant). */
	// private void resolveResources(Graphics g) {
	// 	if (hudFont == null) {
	// 		hudFont = g.getFont("Monospaced", Font.PLAIN, 13);
	// 	}
	// 	if (hudBg == null) {
	// 		hudBg = g.getColor(180, 0, 0, 0); // noir semi-transparent
	// 	}
	// 	if (hudText == null) {
	// 		hudText = g.getColor(255, 0, 255, 90); // vert terminal
	// 	}
	// 	if (bbColor == null) {
	// 		bbColor = g.getColor(200, 255, 60, 60); // rouge BB
	// 	}
	// }

	static int uiScale(Canvas canvas) {
		return Math.max(1, Math.round(canvas.getWidth() / 1920f));
	}

	public int panelBottom() {
		return enabled ? lastPanelBottom : 0;
	}
}