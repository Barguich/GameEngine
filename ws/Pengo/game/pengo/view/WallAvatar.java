package pengo.view;

import model.Entity;
import oop.graphics.BufferedImage;
import oop.graphics.Graphics;
import view.Avatar;

public class WallAvatar extends Avatar {

	private static final String WALL_PATH = "Asset/border/border.png";

	public WallAvatar(Entity entity) {
		super(entity);
	}

	@Override
	public void paint(Graphics g, int xPix, int yPix, double scale) {
		if (entity == null || entity.size() == null) {
			return;
		}

		BufferedImage img = Sprites.get(g, WALL_PATH);

		int side = (int) Math.round(entity.size().x() * scale);

		g.drawImage(img, xPix - side / 2, yPix - side / 2, side, side);
	}
}