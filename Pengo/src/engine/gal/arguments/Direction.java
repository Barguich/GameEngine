package engine.gal.arguments;

public class Direction {

    // CONSTANTS

    Direction B; // B, Backward, Back
    Direction F; // F, Forward, Front
    Direction H; // H, Here
    Direction N; // N, North
    Direction S; // S, South
    // ...

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
    Object // <-- FIXME
    directions;

    // FACTORY

    /**
     * @apiNote implements sharing : it avoids creating new direction each time the
     *          parser encounters a direction.
     * @return the existing direction associated to a name if it already exists
     */
    Direction canonical(String name) {
        throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `canonical`");
    }

    // STATIC INITIALIZATION

    /**
     * @apiNote the global variables must be initialized in that section,
     * @implNote which is executed at the class loading
     */
    static {
    }

    // CONSTRUCTOR

    String name;

    Direction(String name) {
        throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `Direction`");
    }

    // PREDICATE

    boolean isAbsolute() {
        throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `isAbsolute`");
    }

    boolean isRelative() {
        throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `isRelative`");
    }

    // CONVERSION

    int toAngle() {
        throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `toAngle`");
    }

}