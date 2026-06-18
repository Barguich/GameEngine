package game.testSprite;

import engine.Game;
import model.Entity;
import oop.graphics.BufferedImage;
import oop.graphics.Graphics;
import view.Avatar;

public class SpriteAvatar extends Avatar {

	private final String path;

	public SpriteAvatar(Entity entity, String path) {
		super(entity);
		this.path = path;
	}

	@Override
	public void paint(Graphics g, int xPix, int yPix, double scale) {
		BufferedImage img = Sprites.get(g, path);

		double cm = Game.game().cmPerCell;
		int side = (int) Math.round(cm * scale);

		g.drawImage(img, xPix - side / 2, yPix - side / 2, side, side);
	}
}
