package pengo.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 *
 * Centralise tous les parametres de gameplay dans un fichier .properties.
 *
 * Chaque parametre possede une valeur par defaut : si le fichier est absent
 * ou si une cle manque, le jeu reste jouable.
 */
public class PengoConfig {
	public static final String DEFAULT_PATH = "Asset/rsrc/config/pengo.properties";

	private final Properties props = new Properties();

	public PengoConfig() {
		this(DEFAULT_PATH);
	}

	public PengoConfig(String path) {
		assert path != null;
		try (InputStream in = new FileInputStream(path)) {
			props.load(in);
		} catch (IOException e) {
			System.out.println("[config] fichier introuvable (" + path
					+ "), utilisation des valeurs par defaut");
		}
	}

	public String mapPath() {
		return get("map.path", "Asset/rsrc/maps/lvl1.txt");
	}

	public double iceSpeed() {
		return getDouble("ice.speed", 18.0);
	}

	public double iceFriction() {
		return getDouble("ice.friction", 0.998);
	}

	public long fishBoostDuration() {
		return getLong("fish.boost.duration", 8000);
	}

	public long goldFreezeDuration() {
		return getLong("gold.freeze.duration", 5000);
	}

	public long goldDoubleScoreDuration() {
		return getLong("gold.doubleScore.duration", 5000);
	}

	public int scoreCrush() {
		return (int) getLong("score.crush", 100);
	}

	public int scoreWall() {
		return (int) getLong("score.wall", 400);
	}

	// --- Helpers ---

	private String get(String key, String def) {
		String v = props.getProperty(key);
		return v == null ? def : v.trim();
	}

	private double getDouble(String key, double def) {
		try {
			return Double.parseDouble(get(key, Double.toString(def)));
		} catch (NumberFormatException e) {
			System.out.println("[config] valeur invalide pour " + key + ", defaut=" + def);
			return def;
		}
	}

	private long getLong(String key, long def) {
		try {
			return Long.parseLong(get(key, Long.toString(def)));
		} catch (NumberFormatException e) {
			System.out.println("[config] valeur invalide pour " + key + ", defaut=" + def);
			return def;
		}
	}
}
