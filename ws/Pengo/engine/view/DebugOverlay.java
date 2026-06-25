package view;

import java.util.List;

import collision.Box;
import model.Entity;
import oop.graphics.Canvas;
import oop.graphics.Color;
import oop.graphics.Font;
import oop.graphics.Graphics;

/**
 * HUD de debug dessiné par-dessus la scène : FPS, tick rate, bounding boxes et
 * action inférée de l'entité suivie.
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

	// Résolus paresseusement (besoin d'un Graphics vivant).
	private Color hudBg;
	private Color hudText;
	private Color bbColor;
	private Font hudFont;
	private int cachedFontSize = -1;
	private int lastPanelBottom = PANEL_Y;

	// Tick logique poussé depuis la boucle Ticker.
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

	/** Alimente le HUD avec le temps du tick logique (horloge distincte du rendu). */
	public void recordTick(long elapsedMs) {
		this.lastTickMs = elapsedMs;
		this.tickCount++;
	}

	/** À appeler au début de View.paint() : avance l'horloge de frame. */
	public void begin() {
		if (!enabled) {
			return;
		}
		frameClock.onFrame();
	}

	/** Dessine les bounding boxes. À appeler pendant que le clip viewport est actif. */
	public void paintBoundingBoxes(Canvas canvas, Graphics g, ViewPort viewPort, List<Entity> entities) {
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

	/** Dessine le panneau de texte (FPS / tick / action), clip viewport retiré. */
	public void paintPanel(Canvas canvas, Graphics g, Entity followed) {
		if (!enabled) {
			return;
		}

		float scale = uiScale(canvas);
		resolveColors(g);
		resolveFont(g, scale);

		int padding = Math.round(PADDING * scale);
		int lineH = Math.round(LINE_H * scale);
		int panelX = Math.round(PANEL_X * scale);
		int panelY = Math.round(PANEL_Y * scale);
		int panelW = Math.round(PANEL_W * scale);

		int panelH = padding * 2 + PANEL_LINES * lineH;

		g.setColor(hudBg);
		g.fillRect(panelX, panelY, panelW, panelH);

		g.setFont(hudFont);
		g.setColor(hudText);

		int tx = panelX + padding;
		int ty = panelY + padding + lineH - Math.round(4 * scale);

		drawLine(g, tx, ty, 0, lineH,
				String.format("FPS   %5.1f  (%.1f ms/frame)", frameClock.fps(), frameClock.lastFrameMs()));
		drawLine(g, tx, ty, 1, lineH, String.format("TICK  #%d  (%.0f ms)", tickCount, lastTickMs));
		drawLine(g, tx, ty, 2, lineH, String.format("CANVAS %dx%d px", canvas.getWidth(), canvas.getHeight()));

		String followedName = (followed == null) ? "—" : safeName(followed);
		drawLine(g, tx, ty, 3, lineH, "FOLLOW " + followedName);
		drawLine(g, tx, ty, 4, lineH, "ACT   " + ActionInference.describe(followed));
		lastPanelBottom = panelY + panelH;
	}

	private void drawLine(Graphics g, int x, int yBase, int index, int lineH, String text) {
		g.drawString(text, x, yBase + index * lineH);
	}

	private void drawBox(Canvas canvas, Graphics g, ViewPort vp, Box box) {
		int x1 = vp.toPixelX(canvas, box.xmin());
		int y1 = vp.toPixelY(canvas, box.ymin());
		int x2 = vp.toPixelX(canvas, box.xmax());
		int y2 = vp.toPixelY(canvas, box.ymax());
		g.drawRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
	}

	private String safeName(Entity e) {
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

	private void resolveFont(Graphics g, float scale) {
		int fontSize = Math.max(9, Math.round(13 * scale));
		if (hudFont == null || cachedFontSize != fontSize) {
			hudFont = g.getFont("Monospaced", Font.PLAIN, fontSize);
			cachedFontSize = fontSize;
		}
	}

	/**
	 * Facteur d'échelle du HUD, proportionnel à la largeur du canvas (1920px = 1.0),
	 * borné entre 0.5 et 2.0.
	 */
	public static float uiScale(Canvas canvas) {
		float s = canvas.getWidth() / 1920f;
		return Math.max(0.5f, Math.min(2.0f, s));
	}

	public int panelBottom() {
		return enabled ? lastPanelBottom : 0;
	}
}