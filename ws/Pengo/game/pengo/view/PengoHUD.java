package pengo.view;

import oop.graphics.Canvas;
import oop.graphics.Font;
import oop.graphics.Graphics;
import pengo.model.PengoModel;
import view.DebugOverlay;
import view.Overlay;

/**
 * HUD (Head-Up Display) du jeu.
 *
 * Cet overlay affiche en permanence les informations
 * utiles au joueur :
 * - score actuel ;
 * - nombre de vies restantes ;
 * - nombre d'ennemis encore présents ;
 * - nombre total d'entités dans la scène.
 *
 * Le HUD est dessiné au-dessus de la vue du jeu mais
 * ne modifie jamais le modèle.
 */
public class PengoHUD implements Overlay {

	private final PengoModel model;
	private final DebugOverlay debug;

	public PengoHUD(PengoModel model, DebugOverlay debug) {
		this.model = model;
		this.debug = debug;
	}

	@Override
	public void paint(Canvas canvas, Graphics g) {

		// Aucun affichage si le joueur n'existe pas.
		if (model.player() == null) {
			return;
		}

		/*
		 * Facteur d'échelle utilisé pour conserver
		 * une taille lisible quelle que soit la taille
		 * de la fenêtre.
		 */
		float scale = DebugOverlay.uiScale(canvas);

		int x = Math.round(12 * scale);
		int lineH = Math.round(22 * scale);

		/*
		 * Le HUD est affiché sous le panneau de debug
		 * lorsqu'il est présent.
		 */
		int y = debug.panelBottom() + Math.round(24 * scale);

		g.setFont(
				g.getFont(
						"SansSerif",
						Font.BOLD,
						Math.max(10, Math.round(18 * scale))));

		g.setColor(g.getColor(0, 100, 255, 0));

		// Informations principales de la partie.
		g.drawString("Score : " + model.score(), x, y);

		g.drawString(
				"Vies : " + model.player().lives(),
				x,
				y + lineH);

		g.drawString(
				"Enemies : " + model.enemiesRemaining(),
				x,
				y + 2 * lineH);

		/*
		 * Information utile pendant le développement :
		 * nombre total d'entités actuellement présentes
		 * dans le modèle.
		 */
		g.drawString(
				"Total entités : " + model.entities().size(),
				x,
				y + 3 * lineH);
	}
}