package game.testSprite;

import engine.Game;
import model.Entity;
import model.Model;

public class GameEntityFactory {

	private static final String ASSETS = "Asset/";
	private final Model model;

	public GameEntityFactory(Model model) {
		this.model = model;
	}

	public void create(char c, int x, int y) {
		String sprite = spriteFor(c);
		if (sprite == null)
			return;

		Entity e = new Entity(nameFor(c));
		e.setPosition(Game.grid().new Position(x, y));
		e.setAvatar(new SpriteAvatar(e, sprite));
		model.add(e);
	}

	private String spriteFor(char c) {
		switch (c) {
			case '#':
				return ASSETS + "border/border.png";
			case 'I':
				return ASSETS + "bloc/ice_bloc.png";
			case 'G':
				return ASSETS + "bloc/gold_bloc.png";
			case 'D':
				return ASSETS + "bloc/diamond_bloc.png";
			default:
				return null;
		}
	}

	private String nameFor(char c) {
		switch (c) {
			case '#':
				return "border";
			case 'I':
				return "ice";
			case 'G':
				return "gold";
			case 'D':
				return "diamond";
			default:
				return "?";
		}
	}
}
