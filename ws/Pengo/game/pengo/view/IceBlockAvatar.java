package pengo.view;

import model.Entity;
import oop.graphics.Graphics;
import pengo.model.IceBlock;
import view.Avatar;

public class IceBlockAvatar extends Avatar {

	public IceBlockAvatar(Entity entity) {
		super(entity);
	}

	@Override
	public void paint(Graphics g, int xPix, int yPix, double scale) {
		int side = (int) (entity.size().x() * scale);

		g.setColor(Graphics.Colors.cyan);
		g.fillRect(xPix - side / 2, yPix - side / 2, side, side);

		if (entity instanceof IceBlock block) {
			if (block.cracked()) {
				drawCrack(g, xPix, yPix, side, 1);
			} else if (block.veryCracked()) {
				drawCrack(g, xPix, yPix, side, 2);
			}
		}
	}

	private void drawCrack(Graphics g, int xPix, int yPix, int side, int level) {
		g.setColor(Graphics.Colors.black);

		int x0 = xPix - side / 4;
		int y0 = yPix - side / 3;

		g.drawLine(x0, y0, xPix, yPix);

		if (level >= 2) {
			g.drawLine(xPix, yPix, xPix + side / 4, yPix + side / 3);
		}
	}
}