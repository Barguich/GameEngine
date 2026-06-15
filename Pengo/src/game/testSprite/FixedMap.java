package game.testSprite;

import engine.Game;
import engine.model.Entity;
import engine.model.Model;

public class FixedMap {

	// # = bord, I = glace, G = or, D = diamant, espace = vide
	private static final String[] MAP = {
			"############",
			"#          #",
			"#  I  I I  #",
			"#   II     #",
			"#  I    G  #",
			"#     II   #",
			"############",
	};

	public static int width() {
		return MAP[0].length();
	}

	public static int height() {
		return MAP.length;
	}

	public static void build(Model model) {
		assert model != null;

		GameEntityFactory factory = new GameEntityFactory(model);

		for (int y = 0; y < MAP.length; y++) {
			String row = MAP[y];
			for (int x = 0; x < row.length(); x++) {
				factory.create(row.charAt(x), x, y);
			}
		}

		// pingu de test, posé au centre, animé
		String[] pinguFrames = new String[32];
		for (int i = 0; i < 32; i++) {
			pinguFrames[i] = "Asset/pingu/sprite_pingu_" + i + ".png";
		}

		Entity pingu = new Entity("pingu");
		pingu.setPosition(Game.grid().new Position(5, 3));
		pingu.setAvatar(new AnimatedAvatar(pingu, pinguFrames, 100));
		model.add(pingu);
	}
}
