package engine.gal.arguments;

public class Category {

    // CONSTANTS

    Category Adversary, Obstacle, Team, Void;
    // ...

    // STATIC

    Object categories; // <-- FIXME

    boolean interaction[][];

    // STATIC INITIALIZATION

    static {
    }

    // FACTORY

    Category canonical(String name) {
        throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `canonical`");
    }

    // CONSTRUCTOR

    int index;

    Category(String name, int index) {
        throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `Category`");
    }

    // SETTER

    /**
     * @apiNote updates the interaction table such that <i>c1 interactsWith c2
     *          &equiv; bool</i>
     * @param c1
     * @param c2
     * @param bool
     */
    void setInteraction(Category c1, Category c2, boolean bool) {
        throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `setInteraction`");
    }

    // PREDICATE

    /**
     * @apiNote Check the table to see if there is an interaction between
     *          {@code this} and the {@code c} category.
     * @param c
     */
    boolean interactsWith(Category c) {
        throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `interactsWith`");
    }

}