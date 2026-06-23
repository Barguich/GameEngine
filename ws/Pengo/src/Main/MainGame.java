package Main;

import engine.Game;
import geometry.Grid;
import pengo.model.PengoConfig;
import pengo.model.PengoMapLoader;
import pengo.model.PengoModel;

public class MainGame {

	public static void main(String[] args) throws Exception {

		PengoConfig config = (args.length > 0)
				? new PengoConfig(args[0])
				: new PengoConfig();

		String[] map = PengoMapLoader.readMap(config.mapPath());

		Game game = new Game(
				PengoMapLoader.width(map),
				PengoMapLoader.height(map));

		Grid grid = game.grid();

		PengoModel model = new PengoModel(grid, config);

		PengoMapLoader.load(model, map);

		System.out.println("Largeur = "
				+ PengoMapLoader.width(map));

		System.out.println("Hauteur = "
				+ PengoMapLoader.height(map));

		System.out.println("Nombre entités = "
				+ model.entities().size());

		System.out.println("Position joueur = "
				+ model.player().position());
	}
}
