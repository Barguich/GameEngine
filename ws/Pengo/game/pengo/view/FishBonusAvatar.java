package pengo.view;

import model.Entity;
import oop.graphics.BufferedImage;
import oop.graphics.Graphics;
import view.Avatar;

public class FishBonusAvatar extends Avatar {

	private static final String FISH_PATH = "Asset/fish_speed/fish.png";

	public FishBonusAvatar(Entity entity) {
		super(entity);
	}

	@Override
	public void paint(Graphics g, int xPix, int yPix, double scale) {
		if (entity == null || entity.size() == null) {
			return;
		}

		BufferedImage img = Sprites.get(g, FISH_PATH);

		int baseSide = (int) Math.round(entity.size().x() * scale);

		// facteur pour agrandir le sprite dans sa bounding box
		int drawSide = (int) Math.round(baseSide * 1.8);

		g.drawImage(img,
				xPix - drawSide / 2,
				yPix - drawSide / 2,
				drawSide,
				drawSide);
	}
}