package pengo.view;

import oop.graphics.Canvas;
import oop.graphics.Graphics;
import pengo.model.PengoModel;
import view.MenuOverlay;
import view.Overlay;

public class PengoMenuOverlay implements Overlay{

	private final PengoModel model;
	private final MenuOverlay menu;


	public PengoMenuOverlay(PengoModel model, MenuOverlay menu){
		this.menu = menu;
		this.model = model;
	}

	@Override
	public void paint(Canvas canvas, Graphics g) {
		if (model.menuVisible()) {
			menu.paint(canvas, g);
		}
	}

}
