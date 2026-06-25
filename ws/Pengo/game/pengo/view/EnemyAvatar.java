package pengo.view;

import engine.Game;
import model.Entity;
import oop.graphics.BufferedImage;
import oop.graphics.Graphics;
import pengo.model.Enemy;
import view.Avatar;

public class EnemyAvatar extends Avatar {

	private static final String DIR = "Asset/ennemie/";

	private static final long SPAWN_TOTAL_MS = 800;
	private static final long SPAWN_FRAME_MS = SPAWN_TOTAL_MS / 6;
	private static final long BOUNCE_FRAME_MS = 200;

	public EnemyAvatar(Entity entity) {
		super(entity);
	}

	@Override
	public void paint(Graphics g, int xPix, int yPix, double scale) {

		if (!(entity instanceof Enemy)) {
			return;
		}

		Enemy enemy = (Enemy) entity;
		String path = pickImagePath(enemy);
		BufferedImage img = Sprites.get(g, path);

		double cm = Game.game().cmPerCell;
		int side = (int) Math.round(cm * scale);

		g.drawImage(img, xPix - side / 2, yPix - side / 2, side, side);
	}

	private static int pickFrame(Enemy enemy) {
		if (enemy.spawning()) {
			long played = SPAWN_TOTAL_MS - enemy.spawnAnimationRemaining();
			int f = (int) (played / SPAWN_FRAME_MS);

			if (f < 0) {
				f = 0;
			}
			if (f > 5) {
				f = 5;
			}

			return f;
		}

		if (enemy.dying()) {
			return 6;
		}

		if (enemy.passedOut()) {
			return 7;
		}

		if (enemy.frozen()) {
			return -1;
		}

		int base = bounceBase(enemy.orientation());
		int twoFrame = (int) ((enemy.walkAnimationClock() / BOUNCE_FRAME_MS) % 2);
		return base + twoFrame;
	}

	private static int bounceBase(int orientation) {
		switch (orientation) {
			case 0:
				return 14;
			case 90:
				return 8;
			case 180:
				return 10;
			case 270:
				return 12;
			default:
				return 8;
		}
	}
	private static String pickImagePath(Enemy enemy) {
		if (enemy.crushAnimation()) {
			return crushImagePath(enemy);
		}

		int frame = pickFrame(enemy);

		if (frame == -1) {
			return DIR + "sprite_enemie_freeze_blue.png";
		}

		return DIR + "sprite_enemie_" + frame + ".png";
	}
	private static String crushImagePath(Enemy enemy) {
		int frame = enemy.crushFrame();

		if (frame < 0) {
			frame = 0;
		}

		if (frame > 1) {
			frame = 1;
		}

		int direction = enemy.crushDirection();

		/*
		 * 0   = IceBlock va vers la droite
		 * 90  = IceBlock va vers le bas
		 * 180 = IceBlock va vers la gauche
		 * 270 = IceBlock va vers le haut
		 */

		switch (direction) {
			case 0:
				// écrasé vers la droite
				return DIR + "sprite_enemie_" + (34 + frame) + ".png";

			case 180:
				// écrasé vers la gauche
				return DIR + "sprite_enemie_" + (38 + frame) + ".png";

			case 90:
				// écrasé vers le bas
				return DIR + "sprite_enemie_" + (36 + frame) + ".png";

			case 270:
				// écrasé vers le haut
				return DIR + "sprite_enemie_" + (32 + frame) + ".png";

			default:
				return DIR + "sprite_enemie_32.png";
		}
	}
}