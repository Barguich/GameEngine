package gal.arguments;

import java.util.HashMap;
import java.util.Map;

public class Category {

    // CONSTANTS

    public static Category A, C, D, G, I, J, K, M,
            O, P, T, U, V,
            PLAYER, BOSS, ANY, SELECTED,
            Q, X, Y, Z;

    // STATIC

    private static Map<String, Category> categories;
    private static boolean interaction[][];

    // STATIC INITIALIZATION

    static {
        categories = new HashMap<>();

        A = new Category("A", 0);
        C = new Category("C", 1);
        D = new Category("D", 2);
        G = new Category("G", 3);
        I = new Category("I", 4);
        J = new Category("J", 5);
        K = new Category("K", 6);
        M = new Category("M", 7);
        O = new Category("O", 8);
        P = new Category("P", 9);
        T = new Category("T", 10);
        U = new Category("U", 11);
        V = new Category("V", 12);

        PLAYER = new Category("@", 13);
        BOSS = new Category("#", 14);
        ANY = new Category("_", 15);
        SELECTED = new Category("$", 16);

        Q = new Category("Q", 17);
        X = new Category("X", 18);
        Y = new Category("Y", 19);
        Z = new Category("Z", 20);

        interaction = new boolean[21][21];
    }

    private String name;
    // FACTORY

    public static Category canonical(String name) {
        return categories.get(name);
    }

    // CONSTRUCTOR

    private int index;

    public Category(String name, int index) {
        this.name = name;
        this.index = index;
        this.categories.put(name, this);
    }

    // SETTER

    /**
     * @apiNote updates the interaction table such that <i>c1 interactsWith c2
     *          &equiv; bool</i>
     * @param c1
     * @param c2
     * @param bool
     */
    public void setInteraction(Category c1, Category c2, boolean bool) {
        interaction[c1.index][c2.index] = bool;
    }

    // PREDICATE

    /**
     * @apiNote Check the table to see if there is an interaction between
     *          {@code this} and the {@code c} category.
     * @param c
     */
    public boolean interactsWith(Category c) {
        return interaction[this.index][c.index];
    }

    public String name(){
        return this.name;
    }
     public String toString() {
        return name;
    }
}