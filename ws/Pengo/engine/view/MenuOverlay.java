package view;

import oop.graphics.Canvas;
import oop.graphics.Color;
import oop.graphics.Font;
import oop.graphics.Graphics;

/**
 * Menu plein écran dessiné par-dessus la scène (pause / game over / victoire).
*/
public class MenuOverlay {

	private String title = "";
	private String[] items = new String[0];
	private int selected = 0;

	// Couleurs/police résolue.
	private Color dim;        // voile sombre sur la scène
	private Color panelBg;    // fond du panneau
	private Color titleColor;
	private Color itemColor;
	private Color itemSelected;
	private Color selectBar;
	private Font titleFont;
	private Font itemFont;
	private int cachedScale = -1;

	/** Configure le contenu à afficher. selected est clampé sur [0, items-1]. */
	public void set(String title, String[] items, int selected) {
		this.title = (title == null) ? "" : title;
		this.items = (items == null) ? new String[0] : items;
		this.selected = clamp(selected);
	}

	public String[] items() {
		return items;
	}

	public int selected() {
		return selected;
	}

	/** Déplace la sélection (avec wrap) ; delta vaut typiquement -1 ou +1. */
	public void move(int delta) {
		if (items.length == 0) {
			return;
		}
		selected = Math.floorMod(selected + delta, items.length);
	}

	private int clamp(int i) {
		if (items.length == 0) {
			return 0;
		}
		return Math.max(0, Math.min(i, items.length - 1));
	}

	/**
	 * Dessine le menu plein écran. À appeler en tout dernier dans View.paint(),
	 * après avoir retiré le clip du viewport.
	 */
	public void paint(Canvas canvas, Graphics g) {
		// uiScale est désormais flottant (adaptatif petits/grands écrans) ; on
		// dérive un facteur entier borné à 1 minimum pour les calculs de mise
		// en page du menu, qui reste centré et lisible.
		int scale = Math.max(1, Math.round(DebugOverlay.uiScale(canvas)));
		resolve(g, scale);

		int w = canvas.getWidth();
		int h = canvas.getHeight();

		// Voile assombrissant toute la scène.
		g.setColor(dim);
		g.fillRect(0, 0, w, h);

		int lineH = 40 * scale;
		int panelW = 360 * scale;
		int panelH = (items.length + 3) * lineH;
		int panelX = (w - panelW) / 2;
		int panelY = (h - panelH) / 2;

		g.setColor(panelBg);
		g.fillRect(panelX, panelY, panelW, panelH);

		// Titre
		g.setFont(titleFont);
		g.setColor(titleColor);
		int titleX = panelX + 30 * scale;
		int titleY = panelY + lineH + 6 * scale;
		g.drawString(title, titleX, titleY);

		// Entrées
		g.setFont(itemFont);
		int itemX = panelX + 50 * scale;
		int firstItemY = panelY + 3 * lineH;

		for (int i = 0; i < items.length; i++) {
			int y = firstItemY + i * lineH;

			if (i == selected) {
				g.setColor(selectBar);
				g.fillRect(panelX + 20 * scale, y - lineH + 12 * scale,
						panelW - 40 * scale, lineH - 6 * scale);
				g.setColor(itemSelected);
				g.drawString("> " + items[i], itemX - 24 * scale, y);
			} else {
				g.setColor(itemColor);
				g.drawString(items[i], itemX, y);
			}
		}
	}

	private void resolve(Graphics g, int scale) {
		if (dim == null) {
			dim = g.getColor(170, 0, 0, 0);
			panelBg = g.getColor(235, 20, 24, 40);
			titleColor = g.getColor(255, 255, 220, 0);
			itemColor = g.getColor(255, 200, 200, 200);
			itemSelected = g.getColor(255, 0, 0, 0);
			selectBar = g.getColor(255, 255, 220, 0);
		}
		if (titleFont == null || cachedScale != scale) {
			titleFont = g.getFont("SansSerif", Font.BOLD, 28 * scale);
			itemFont = g.getFont("SansSerif", Font.PLAIN, 22 * scale);
			cachedScale = scale;
		}
	}
}
