package pengo.view;

import engine.Game;
import model.Entity;
import oop.graphics.BufferedImage;
import oop.graphics.Graphics;
import pengo.model.PengoPlayer;

import view.Avatar;

public class PengoAvatar extends Avatar {

	private static final String DIR = "Asset/pingu/";
	private static final long FRAME_DURATION_MS = 40;

	public PengoAvatar(Entity entity) {
		super(entity);
	}

	@Override
	public void paint(Graphics g, int xPix, int yPix, double scale) {

		if (!(entity instanceof PengoPlayer)) {
			return;
		}

		PengoPlayer player = (PengoPlayer) entity;

		int base = walkBase(player.orientation());
		int twoFrame = (int) ((player.walkAnimationClock() / FRAME_DURATION_MS) % 2);
		int frame = base + twoFrame;

		String path = DIR + "sprite_pingu_" + frame + ".png";
		BufferedImage img = Sprites.get(g, path);

		double cm = Game.game().cmPerCell;
		int side = (int) Math.round(cm * scale);

		g.drawImage(img, xPix - side / 2, yPix - side / 2, side, side);
	}

	private static int walkBase(int orientation) {
		switch (orientation) {
		case 0:
			return 6; // Right
		case 90:
			return 0; // Front
		case 180:
			return 2; // Left
		case 270:
			return 4; // Back
		default:
			return 0;
		}
	}
}
