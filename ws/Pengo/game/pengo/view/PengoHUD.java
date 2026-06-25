package pengo.view;

import oop.graphics.Canvas;
import oop.graphics.Font;
import oop.graphics.Graphics;
import pengo.model.PengoModel;
import view.DebugOverlay;
import view.Overlay;

public class PengoHUD implements Overlay {

	private final PengoModel model;
	private final DebugOverlay debug;

	public PengoHUD(PengoModel model, DebugOverlay debug) {
		this.model = model;
		this.debug = debug;
	}

	@Override
	public void paint(Canvas canvas, Graphics g) {
		if (model.player() == null) {
			return;
		}

		int scale = DebugOverlay.uiScale(canvas);
		int x = 12 * scale;
		int lineH = 22 * scale;
		int y = debug.panelBottom() + 24 * scale;

		g.setFont(g.getFont("SansSerif", Font.BOLD, 18 * scale));
		g.setColor(g.getColor(0, 100, 255, 0));

		g.drawString("Score : " + model.score(), x, y);
		g.drawString("Vies : " + model.player().lives(), x, y + lineH);
		g.drawString("Enemies : " + model.enemiesRemaining(), x, y + 2 * lineH);
		g.drawString("Total entités : " + model.entities().size(), x, y + 3 * lineH);
	}

}
