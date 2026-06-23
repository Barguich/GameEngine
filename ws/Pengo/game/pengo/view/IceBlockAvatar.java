package pengo.view;

import engine.Game;
import model.Entity;
import oop.graphics.BufferedImage;
import oop.graphics.Graphics;
import pengo.model.IceBlock;
import view.Avatar;

/**
 * Rendu d'un IceBlock selon hp et anim de cassure.
 * Identique au comportement du mock, juste déplacé proprement côté game.
 */
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
		String path = pickPath(ice);

		BufferedImage img = Sprites.get(g, path);

		double cm = Game.game().cmPerCell;
		int side = (int) Math.round(cm * scale);

		g.drawImage(img, xPix - side / 2, yPix - side / 2, side, side);
	}

	private static String pickPath(IceBlock ice) {
		if (ice.breakingAnimation()) {
			int base;
			if (ice.hp() == 2) {
				base = 0;
			} else if (ice.hp() == 1) {
				base = 3;
			} else {
				base = 6;
			}
			return "Asset/bloc_destroy/sprite_bloc_destroy_" + (base + ice.breakingFrame()) + ".png";
		}

		if (ice.hp() >= 3) {
			return "Asset/bloc/ice_bloc.png";
		}
		if (ice.hp() == 2) {
			return "Asset/bloc_destroy/sprite_bloc_destroy_2.png";
		}
		if (ice.hp() == 1) {
			return "Asset/bloc_destroy/sprite_bloc_destroy_5.png";
		}
		return "Asset/bloc_destroy/sprite_bloc_destroy_8.png";
	}
}
