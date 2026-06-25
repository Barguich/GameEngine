package gal_engine;

import java.util.HashMap;
import java.util.Map;

/**
 * Représentation d'un mode utilisé par les automates GAL.
 *
 * Un mode permet de décrire l'état courant d'une entité
 * (Walking, Fighting, Sleeping, etc.) et peut être utilisé
 * dans les conditions et les transitions des automates.
 *
 * Cette classe garantit qu'un même nom de mode correspond
 * toujours à une unique instance grâce à la méthode canonical().
 */
public class Mode {

	// Modes fréquemment utilisés dans les automates.
	public static final Mode Blocking = new Mode("Blocking");
	public static final Mode Dying = new Mode("Dying");
	public static final Mode Escaping = new Mode("Escaping");
	public static final Mode Fighting = new Mode("Fighting");
	public static final Mode Resting = new Mode("Resting");
	public static final Mode Running = new Mode("Running");
	public static final Mode Searching = new Mode("Searching");
	public static final Mode Sleeping = new Mode("Sleeping");
	public static final Mode Waiting = new Mode("Waiting");
	public static final Mode Walking = new Mode("Walking");

	// Table permettant d'associer un nom de mode à son instance unique.
	private static final Map<String, Mode> modes = new HashMap<>();

	static {
		modes.put("Blocking", Blocking);
		modes.put("Dying", Dying);
		modes.put("Escaping", Escaping);
		modes.put("Fighting", Fighting);
		modes.put("Resting", Resting);
		modes.put("Running", Running);
		modes.put("Searching", Searching);
		modes.put("Sleeping", Sleeping);
		modes.put("Waiting", Waiting);
		modes.put("Walking", Walking);
	}

	// Nom du mode tel qu'il apparaît dans les fichiers GAL.
	private final String name;

	/**
	 * Constructeur privé.
	 *
	 * Les modes doivent être créés via canonical()
	 * afin d'éviter les doublons.
	 */
	private Mode(String name) {
		this.name = name;
	}

	/**
	 * Retourne l'instance unique associée au nom demandé.
	 *
	 * Si le mode existe déjà, on réutilise l'instance existante.
	 * Sinon, une nouvelle instance est créée puis mémorisée.
	 *
	 * Ce mécanisme simplifie les comparaisons de modes
	 * dans le moteur d'automates.
	 */
	public static Mode canonical(String name) {
		Mode mode = modes.get(name);

		if (mode == null) {
			mode = new Mode(name);
			modes.put(name, mode);
		}

		return mode;
	}

	public String name() {
		return name;
	}

	/**
	 * Deux modes sont considérés égaux lorsqu'ils portent
	 * le même nom logique.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Mode)) {
			return false;
		}

		Mode m = (Mode) o;
		return name.equals(m.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}
}