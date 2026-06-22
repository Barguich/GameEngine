package gal.arguments;

import java.util.HashMap;
import java.util.Map;

public class Direction {

	// CONSTANTS
	public static Direction B; // B, Backward, Back
	public static Direction F; // F, Forward, Front
	public static Direction H; // H, Here

	public static Direction N; // N, North
	public static Direction S; // S, South
	public static Direction E;
	public static Direction W;

	public static Direction L;
	public static Direction R;

	public static Direction NE;
	public static Direction NW;
	public static Direction SE;
	public static Direction SW;

	// STATIC
	private static Map<String, Direction> directions;

	// FACTORY
	public static Direction canonical(String name) {
		return directions.get(name);
	}

	// STATIC INITIALIZATION
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

	// CONSTRUCTOR

	private String name;

	public Direction(String name) {
		this.name = name;
		this.directions.put(name, this);
	}

	// PREDICATE

	public boolean isAbsolute() {
		return this == N
				|| this == S
				|| this == E
				|| this == W
				|| this == NE
				|| this == NW
				|| this == SE
				|| this == SW;
	}

	public boolean isRelative() {
		return this == F
				|| this == B
				|| this == L
				|| this == R
				|| this == H;
	}

	// CONVERSION

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

    public String toString() {
        return name;
    }
}
