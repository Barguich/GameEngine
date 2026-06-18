package testSprite;

import engine.Game;
import model.Entity;
import oop.graphics.Graphics;
import view.Avatar;

public class WallAvatar extends Avatar {

	public WallAvatar(Entity entity) {
		super(entity);
	}

	@Override
	public void paint(Graphics g, int xPix, int yPix, double scale) {
		double cm = Game.game().cmPerCell;
		int side = (int) Math.round(cm * scale);
		int half = side / 2;

		g.setColor(Graphics.Colors.blue);
		g.fillRect(xPix - half, yPix - half, side, side);
	}
}
