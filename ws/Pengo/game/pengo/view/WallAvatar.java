package pengo.view;

import model.Entity;
import oop.graphics.Graphics;
import view.Avatar;

public class WallAvatar extends Avatar {

	public WallAvatar(Entity entity) {
		super(entity);
	}

	@Override
	public void paint(Graphics g, int xPix, int yPix, double scale) {
		int side = (int) (entity.size().x() * scale);

		g.setColor(Graphics.Colors.gray);
		g.fillRect(xPix - side / 2, yPix - side / 2, side, side);
	}
}