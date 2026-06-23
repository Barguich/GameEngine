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
	private static final long SPAWN_FRAME_MS = SPAWN_TOTAL_MS / 6; // ~133

	private static final long FROZEN_FRAME_MS = 250;
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
		int frame = pickFrame(enemy);

		String path = DIR + "sprite_enemie_" + frame + ".png";
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

		if (enemy.frozen()) {
			int tic = (int) ((enemy.frozenAnimationClock() / FROZEN_FRAME_MS) % 2);
			return 6 + tic;
		}

		int base = bounceBase(enemy.orientation());
		int twoFrame = (int) ((enemy.walkAnimationClock() / BOUNCE_FRAME_MS) % 2);
		return base + twoFrame;
	}

	private static int bounceBase(int orientation) {
		switch (orientation) {
			case 0:
				return 14; // Right
			case 90:
				return 8; // Front
			case 180:
				return 10; // Left
			case 270:
				return 12; // Back
			default:
				return 8;
		}
	}
}

