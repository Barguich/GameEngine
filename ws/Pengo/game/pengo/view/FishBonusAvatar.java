package pengo.view;

import model.Entity;
import oop.graphics.Graphics;
import view.Avatar;

public class FishBonusAvatar extends Avatar {

	public FishBonusAvatar(Entity entity) {
		super(entity);
	}

	@Override
	public void paint(Graphics g, int xPix, int yPix, double scale) {
		int side = (int) (entity.size().x() * scale);

		g.setColor(Graphics.Colors.cyan);
		g.fillOval(xPix - side / 2, yPix - side / 2, side, side);
	}
}