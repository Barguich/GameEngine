package testSprite;

import engine.Game;
import model.Entity;
import oop.graphics.BufferedImage;
import oop.graphics.Graphics;
import pengo.model.IceBlock;
import view.Avatar;

public class IceBlockAvatar extends Avatar {

	public IceBlockAvatar(Entity entity) {
		super(entity);
	}

	@Override
	public void paint(Graphics g, int xPix, int yPix, double scale) {
		if (!(entity instanceof IceBlock)) {
			return;
		}

		IceBlock ice = (IceBlock) entity;

		String path;

		if (ice.breakingAnimation()) {
			int base;

			if (ice.hp() == 2) {
				base = 0;
			} else if (ice.hp() == 1) {
				base = 3;
			} else {
				base = 6;
			}

			path = "Asset/bloc_destroy/sprite_bloc_destroy_" + (base + ice.breakingFrame()) + ".png";

		} else if (ice.hp() >= 3) {
			path = "Asset/bloc/ice_bloc.png";

		} else if (ice.hp() == 2) {
			path = "Asset/bloc_destroy/sprite_bloc_destroy_2.png";

		} else if (ice.hp() == 1) {
			path = "Asset/bloc_destroy/sprite_bloc_destroy_5.png";

		} else {
			path = "Asset/bloc_destroy/sprite_bloc_destroy_8.png";
		}

		BufferedImage img = Sprites.get(g, path);

		double cm = Game.game().cmPerCell;
		int side = (int) Math.round(cm * scale);

		g.drawImage(img, xPix - side / 2, yPix - side / 2, side, side);
	}
}