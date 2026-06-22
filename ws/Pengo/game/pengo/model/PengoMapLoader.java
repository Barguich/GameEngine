package pengo.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import geometry.Grid;
import model.Entity;

public class PengoMapLoader {

	// Lit le fichier texte décrivant la map
	public static String[] readMap(String path) throws IOException {
		if (path == null) {
			throw new IllegalArgumentException("Chemin de map null");
		}

		List<String> lines = new ArrayList<String>();

		// Fermeture automatique du fichier
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;

			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()) {
					lines.add(line);
				}
			}
		}

		if (lines.isEmpty()) {
			throw new IllegalArgumentException("Map vide : " + path);
		}

		// Vérifie que la map est rectangulaire
		int width = lines.get(0).length();

		for (String l : lines) {
			if (l.length() != width) {
				throw new IllegalArgumentException("Toutes les lignes doivent avoir la même taille");
			}
		}

		return lines.toArray(new String[0]);
	}

	public static int width(String[] map) {
		return map[0].length();
	}

	public static int height(String[] map) {
		return map.length;
	}

	// Crée et place les entités à partir des caractères de la map
	public static void load(PengoModel model, String[] map) {
		if (model == null || map == null) {
			return;
		}

		Grid grid = model.grid();

		for (int y = 0; y < map.length; y++) {
			String line = map[y];

			for (int x = 0; x < line.length(); x++) {
				char c = line.charAt(x);

				Entity e = createEntity(c);

				if (e == null) {
					continue;
				}

				e.setSize(grid.new Dimension(1, 1));
				e.setPosition(grid.new Position(x, y));

				// Le joueur est gardé séparément dans le modèle
				if (e instanceof PengoPlayer) {
					model.setPlayer((PengoPlayer) e);
				} else {
					model.add(e);
				}
			}
		}
	}

	// Associe chaque symbole du fichier texte à une entité du jeu
	private static Entity createEntity(char c) {
		switch (c) {
		case '#':
			return new Wall();

		case 'P':
			return new PengoPlayer();

		case 'I':
			return new IceBlock();

		case 'G':
			return new GoldBlock();

		case 'D':
			return new DiamondBlock();

		case 'E':
			return new Enemy();

		case 'F':
			return new FishBonus();

		case '.':
		case ' ':
			return null;

		default:
			throw new IllegalArgumentException("Symbole inconnu dans la map : " + c);
		}
	}
}