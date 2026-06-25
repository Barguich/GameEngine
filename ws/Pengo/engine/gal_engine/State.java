package gal_engine;

import java.util.Objects;

/**
 * Représentation d'un état d'automate GAL.
 *
 * Un état est défini par :
 * - un mode (Walking, Fighting, Sleeping, ...)
 * - un identifiant permettant de distinguer plusieurs états
 *   appartenant au même mode.
 *
 * Exemple :
 * Walking_0
 * Walking_1
 * Fighting_0
 *
 * Cette représentation permet de construire les automates
 * utilisés par les entités du jeu.
 */
public class State {

	// Mode associé à l'état.
	private Mode mode;

	// Identifiant de l'état dans le mode.
	private int id;

	/**
	 * Construit un état à partir d'un nom de mode et d'un identifiant.
	 *
	 * Le mode est récupéré via Mode.canonical() afin de réutiliser
	 * les instances existantes lorsque cela est possible.
	 */
	public State(String mode, int id) {
		this.mode = Mode.canonical(mode);
		this.id = id;
	}

	public Mode mode() {
		return mode;
	}

	public int id() {
		return id;
	}

	/**
	 * Deux états sont égaux lorsqu'ils possèdent
	 * le même mode et le même identifiant.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof State)) {
			return false;
		}
		return equals((State) o);
	}

	public boolean equals(State s) {
		return s != null
				&& id == s.id
				&& mode.equals(s.mode);
	}

	/**
	 * Hash cohérent avec equals().
	 * Permet d'utiliser les états dans les collections
	 * de type HashMap ou HashSet.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(mode, id);
	}

	/**
	 * Format d'affichage utilisé dans les traces
	 * et lors du débogage des automates.
	 *
	 * Exemple : Walking_0
	 */
	@Override
	public String toString() {
		return mode.name() + "_" + id;
	}
}