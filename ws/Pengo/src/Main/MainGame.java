package Main;

import engine.Game;
import geometry.Grid;
import pengo.model.PengoMapLoader;
import pengo.model.PengoModel;

public class MainGame {

	public static void main(String[] args) throws Exception {

		String[] map = PengoMapLoader.readMap("Pengo/rsrc/maps/lvl1.txt");

		Game game = new Game(
				PengoMapLoader.width(map),
				PengoMapLoader.height(map));

		Grid grid = game.grid();

		PengoModel model = new PengoModel(grid);

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
