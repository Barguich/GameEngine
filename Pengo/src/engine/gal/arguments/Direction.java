package engine.gal.arguments;

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

    /**
     * @apiNote {@code directions} associates a Direction to a name in order to
     *          ensure uniqueness (thus sharing) of the instance associated to that
     *          name.
     * @apiNote Advantages
     *          <UL>
     *          <LI>Sharing reduces memory consumption</LI>
     *          <LI>Comparison can be done using {@code ==} instead of
     *          {@code String:equals} on names.</LI>
     *          <LI>it is easy to use: {@code Direction.canonical(name)} provides
     *          the unique representative of the Direction associated to that
     *          name.</LI>
     *          </UL>
     */

    private static Map<String, Direction> directions;

    // FACTORY

    /**
     * @apiNote implements sharing : it avoids creating new direction each time the
     *          parser encounters a direction.
     * @return the existing direction associated to a name if it already exists
     */
    public Direction canonical(String name) {
        return new Direction(name);
    }

    // STATIC INITIALIZATION

    /**
     * @apiNote the global variables must be initialized in that section,
     * @implNote which is executed at the class loading
     */
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

        throw new IllegalStateException(
                "Relative direction " + name + " has no absolute angle");
    }

    public String name() {
        return name;
    }
}