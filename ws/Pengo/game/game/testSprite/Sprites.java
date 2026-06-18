package game.testSprite;

import java.util.HashMap;
import java.util.Map;

import oop.graphics.BufferedImage;
import oop.graphics.Graphics;

public class Sprites {

	private static final Map<String, BufferedImage> cache = new HashMap<>();

	public static BufferedImage get(Graphics g, String path) {
		BufferedImage img = cache.get(path);
		if (img == null) {
			img = g.load(path);
			cache.put(path, img);
		}
		return img;
	}
}
