package testSprite;

import engine.Game;
import model.Entity;
import oop.graphics.BufferedImage;
import oop.graphics.Graphics;
import view.Avatar;

public class AnimatedAvatar extends Avatar {

	private final String[] frames;
	private final long frameDuration_ms;

	private int current = 0;
	private long lastSwitch = System.currentTimeMillis();

	public AnimatedAvatar(Entity entity, String[] frames, long frameDuration_ms) {
		super(entity);
		this.frames = frames;
		this.frameDuration_ms = frameDuration_ms;
	}

	@Override
	public void paint(Graphics g, int xPix, int yPix, double scale) {
		long now = System.currentTimeMillis();
		if (now - lastSwitch >= frameDuration_ms) {
			current = (current + 1) % frames.length;
			lastSwitch = now;
		}

		BufferedImage img = Sprites.get(g, frames[current]);

		double cm = Game.game().cmPerCell;
		int side = (int) Math.round(cm * scale);

		g.drawImage(img, xPix - side / 2, yPix - side / 2, side, side);
	}
}
