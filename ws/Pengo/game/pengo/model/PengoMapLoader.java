package pengo.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import geometry.Grid;
import model.Entity;

public class PengoMapLoader {

	// Regroupe les lignes de la grille et le mode torique lu dans le fichier.
	public static class MapData {
		public final String[] lines;
		public final boolean torus;

		public MapData(String[] lines, boolean torus) {
			this.lines = lines;
			this.torus = torus;
		}
	}

	// Lit le fichier et extrait le marqueur #TORUS éventuel, sans casser
	// la lecture rectangulaire de la grille.
	public static MapData readMapData(String path) throws IOException {
		if (path == null) {
			throw new IllegalArgumentException("Chemin de map null");
		}

		List<String> rawLines = new ArrayList<String>();

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()) {
					rawLines.add(line);
				}
			}
		}

		if (rawLines.isEmpty()) {
			throw new IllegalArgumentException("Map vide : " + path);
		}

		boolean torus = false;
		List<String> gridLines = new ArrayList<String>();

		for (String l : rawLines) {
			if (l.trim().equalsIgnoreCase("#TORUS")) {
				torus = true;
				continue;
			}
			gridLines.add(l);
		}

		if (gridLines.isEmpty()) {
			throw new IllegalArgumentException("Map vide après extraction du marqueur : " + path);
		}

		int width = gridLines.get(0).length();
		for (String l : gridLines) {
			if (l.length() != width) {
				throw new IllegalArgumentException("Toutes les lignes doivent avoir la même taille");
			}
		}

		return new MapData(gridLines.toArray(new String[0]), torus);
	}

	// Conservé pour compatibilité : équivaut à readMapData(path).lines
	public static String[] readMap(String path) throws IOException {
		return readMapData(path).lines;
	}

	public static int width(String[] map) {
		return map[0].length();
	}

	public static int height(String[] map) {
		return map.length;
	}

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
			case 'B':
				return new IceBlock(true, 10_000);
			case '.':
			case ' ':
				return null;
			default:
				throw new IllegalArgumentException("Symbole inconnu dans la map : " + c);
		}
	}
}