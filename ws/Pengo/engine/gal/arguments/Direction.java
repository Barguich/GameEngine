package gal.arguments;

import java.util.HashMap;
import java.util.Map;

/**
 * Représentation des directions manipulées par les automates GAL.
 *
 * Une direction peut être : - absolue : N, S, E, W, NE, NW, SE, SW - relative à
 * l'orientation de l'entité : F, B, L, R, H
 *
 * Cette classe centralise également les conversions vers les angles utilisés
 * par le moteur.
 */
public class Direction {

	// Directions relatives à l'entité.
	public static Direction B; // Back
	public static Direction F; // Forward
	public static Direction H; // Here

	// Directions absolues du monde.
	public static Direction N;
	public static Direction S;
	public static Direction E;
	public static Direction W;

	// Directions relatives gauche / droite.
	public static Direction L;
	public static Direction R;

	// Directions diagonales.
	public static Direction NE;
	public static Direction NW;
	public static Direction SE;
	public static Direction SW;

	// Table permettant de retrouver une direction à partir de son nom.
	private static Map<String, Direction> directions;

	/**
	 * Retourne l'instance unique correspondant au nom fourni.
	 */
	public static Direction canonical(String name) {
		return directions.get(name);
	}

	// Initialisation des directions reconnues par GAL.
	static {
		directions = new HashMap<>();

		N = new Direction("N");
		S = new Direction("S");
		E = new Direction("E");
		W = new Direction("W");

		F = new Direction("F");
		B = new Direction("B");
		L = new Direction("L");
		R = new Direction("R");
		H = new Direction("H");

		NE = new Direction("NE");
		NW = new Direction("NW");
		SE = new Direction("SE");
		SW = new Direction("SW");
	}

	// Nom utilisé dans les fichiers GAL.
	private String name;

	/**
	 * Lors de la création, la direction est automatiquement enregistrée dans la
	 * table des directions connues.
	 */
	public Direction(String name) {
		this.name = name;
		directions.put(name, this);
	}

	/**
	 * Indique si la direction est exprimée dans le repère du monde.
	 *
	 * Exemple : N signifie toujours le nord du monde, quelle que soit l'orientation
	 * de l'entité.
	 */
	public boolean isAbsolute() {
		return this == N || this == S || this == E || this == W || this == NE || this == NW || this == SE || this == SW;
	}

	/**
	 * Indique si la direction dépend de l'orientation courante de l'entité.
	 *
	 * Exemple : F signifie "devant l'entité".
	 */
	public boolean isRelative() {
		return this == F || this == B || this == L || this == R || this == H;
	}

	/**
	 * Convertit une direction en angle.
	 *
	 * Cette méthode est utilisée par les actions GAL afin de convertir les
	 * directions du langage vers le système d'orientation du moteur.
	 */
	public int toAngle() {

		if (this == E)
			return 0;

		if (this == NE)
			return 45;

		if (this == N)
			return 90;

		if (this == NW)
			return 135;

		if (this == W)
			return 180;

		if (this == SW)
			return -135;

		if (this == S)
			return -90;

		if (this == SE)
			return -45;

		// Directions relatives.
		if (this == F)
			return 0;

		if (this == B)
			return 180;

		if (this == L)
			return 90;

		if (this == R)
			return -90;

		if (this == H)
			return 0;

		throw new IllegalStateException("Direction " + name + " has no angle");
	}

	public String name() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}