package pengo.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import geometry.Grid;
import model.Entity;

public class PengoMapLoader {

	// Lire le fichier texte et retourner un tableau de lignes
	public static String[] readMap(String path) throws IOException {
		assert path != null;

		List<String> lines = new ArrayList<String>();

		// Le try ferme automatiquement le fichier à la fin
		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;

			// Lire le fichier jusqu'à la fin
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()) {
					lines.add(line);
				}
			}
		}

		if (lines.isEmpty()) {
			throw new IllegalArgumentException("Map vide : " + path);
		}

		// La largeur de la map est la taille de la première ligne
		int width = lines.get(0).length();

		// Toutes les lignes doivent avoir la même largeur
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

	// Créer les entités correspondant aux caractères de la map
	public static void load(PengoModel model, String[] map) {

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

				if (e instanceof PengoPlayer) {
					model.setPlayer((PengoPlayer) e);
				} else {
					model.add(e);
				}
			}
		}
	}

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