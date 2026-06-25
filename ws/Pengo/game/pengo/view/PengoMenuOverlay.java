package pengo.view;

import oop.graphics.Canvas;
import oop.graphics.Graphics;
import pengo.model.PengoModel;
import view.MenuOverlay;
import view.Overlay;

/**
 * Overlay responsable de l'affichage des menus de Pengo.
 *
 * Cette classe s'appuie sur le MenuOverlay générique du moteur et décide
 * simplement si le menu doit être affiché ou non selon l'état du modèle.
 */
public class PengoMenuOverlay implements Overlay {

	private final PengoModel model;
	private final MenuOverlay menu;

	public PengoMenuOverlay(PengoModel model, MenuOverlay menu) {
		this.menu = menu;
		this.model = model;
	}

	@Override
	public void paint(Canvas canvas, Graphics g) {

		/*
		 * Le menu n'est dessiné que lorsqu'un état du jeu nécessite une interaction
		 * utilisateur : pause, victoire, défaite ou choix de map.
		 */
		if (model.menuVisible()) {
			menu.paint(canvas, g);
		}
	}
}