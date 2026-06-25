package pengo.model;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Gestionnaire de configuration du jeu Pengo.
 *
 * Cette classe centralise les paramètres de gameplay dans un fichier
 * .properties afin d'éviter la présence de constantes dispersées dans le code.
 *
 * Les valeurs peuvent être modifiées sans recompilation, ce qui facilite les
 * phases de test et d'équilibrage.
 *
 * Chaque paramètre possède une valeur par défaut afin que le jeu reste
 * fonctionnel même si le fichier de configuration est absent ou incomplet.
 */
public class PengoConfig {

	// Emplacement par défaut du fichier de configuration.
	public static final String DEFAULT_PATH = "Asset/rsrc/config/pengo.properties";

	private final Properties props = new Properties();

	public PengoConfig() {
		this(DEFAULT_PATH);
	}

	/**
	 * Charge les paramètres depuis un fichier .properties.
	 *
	 * En cas d'erreur de lecture, les valeurs par défaut définies dans les getters
	 * seront utilisées.
	 */
	public PengoConfig(String path) {
		assert path != null;

		try (InputStream in = new FileInputStream(path)) {
			props.load(in);

		} catch (IOException e) {
			System.out.println("[config] fichier introuvable (" + path + "), utilisation des valeurs par defaut");
		}
	}

	/**
	 * Carte chargée au démarrage du jeu.
	 */
	public String mapPath() {
		return get("map.path", "Asset/rsrc/maps/lvl1.txt");
	}

	/**
	 * Vitesse de glissement des blocs de glace.
	 */
	public double iceSpeed() {
		return getDouble("ice.speed", 18.0);
	}

	/**
	 * Coefficient de ralentissement appliqué pendant la glissade.
	 */
	public double iceFriction() {
		return getDouble("ice.friction", 0.998);
	}

	/**
	 * Durée du bonus poisson.
	 */
	public long fishBoostDuration() {
		return getLong("fish.boost.duration", 8000);
	}

	/**
	 * Durée du gel provoqué par un GoldBlock.
	 */
	public long goldFreezeDuration() {
		return getLong("gold.freeze.duration", 5000);
	}

	/**
	 * Durée du bonus de score associé au GoldBlock.
	 */
	public long goldDoubleScoreDuration() {
		return getLong("gold.doubleScore.duration", 5000);
	}

	/**
	 * Score obtenu lors de l'écrasement d'un ennemi.
	 */
	public int scoreCrush() {
		return (int) getLong("score.crush", 100);
	}

	/**
	 * Score obtenu lorsqu'un ennemi est éliminé contre un mur.
	 */
	public int scoreWall() {
		return (int) getLong("score.wall", 400);
	}

	// -----------------------------------------------------------------
	// Méthodes utilitaires de lecture des propriétés.
	// -----------------------------------------------------------------

	/**
	 * Lecture générique d'une propriété texte.
	 *
	 * Si la clé n'existe pas, la valeur par défaut est retournée.
	 */
	private String get(String key, String def) {
		String v = props.getProperty(key);
		return v == null ? def : v.trim();
	}

	/**
	 * Lecture sécurisée d'une propriété numérique réelle.
	 *
	 * Une valeur invalide ne provoque pas l'arrêt du jeu : la valeur par défaut est
	 * utilisée à la place.
	 */
	private double getDouble(String key, double def) {
		try {
			return Double.parseDouble(get(key, Double.toString(def)));

		} catch (NumberFormatException e) {
			System.out.println("[config] valeur invalide pour " + key + ", defaut=" + def);

			return def;
		}
	}

	/**
	 * Lecture sécurisée d'une propriété entière longue.
	 */
	private long getLong(String key, long def) {
		try {
			return Long.parseLong(get(key, Long.toString(def)));

		} catch (NumberFormatException e) {
			System.out.println("[config] valeur invalide pour " + key + ", defaut=" + def);

			return def;
		}
	}
}