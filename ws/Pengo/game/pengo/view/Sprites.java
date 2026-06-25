package pengo.view;

import java.util.HashMap;
import java.util.Map;

import oop.graphics.BufferedImage;
import oop.graphics.Graphics;

/**
 * Gestionnaire de sprites partagé par tous les avatars.
 *
 * Cette classe implémente un cache d'images :
 * chaque sprite est chargé une seule fois depuis le disque,
 * puis réutilisé pendant toute la durée de la partie.
 *
 * Cela évite des accès disque inutiles à chaque frame
 * et améliore les performances du rendu.
 */
public final class Sprites {

	// Associe un chemin de fichier à l'image déjà chargée.
	private static final Map<String, BufferedImage> cache = new HashMap<>();

	private Sprites() {
		// Classe utilitaire : aucune instance nécessaire.
	}

	/**
	 * Retourne le sprite associé au chemin demandé.
	 *
	 * Si l'image n'a jamais été chargée,
	 * elle est lue depuis le disque puis mémorisée.
	 * Les appels suivants réutilisent directement
	 * l'image présente dans le cache.
	 */
	public static BufferedImage get(Graphics g, String path) {

		BufferedImage img = cache.get(path);

		if (img == null) {
			img = g.load(path);
			cache.put(path, img);
		}

		return img;
	}
}