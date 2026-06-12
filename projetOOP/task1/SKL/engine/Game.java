package engine;// == GAME ==

import java.io.PrintStream;

public class Game {

    // CONSTANT

    final boolean torusOnXaxis = true; // vrai si l'axe X est une boucle fermée
    final boolean torusOnYaxis = true; // vrai si l'axe Y est une boucle fermée

    public final double cmPerCell = 3.7; // échelle qui relie l'unité ncell à cm
    public final int pixelPerCm = 2; // échelle qui relie l'unité pixel à cm

    // FIELDS

    int width_ncell; // largeur du monde en nombre de cellules
    int height_ncell; // hauteur du monde en nombre de celluls

    double width_cm; // largeur du monde en cm
    double height_cm; // hauteur du monde en cm

    public Grid grid; // permet la création de coordonnées en unités ncell
    public ISU isu; // permet la création de coordonnées en unités cm
    //private Picture pict; // permet la création de coordonnées en unités pixel, ne sera utilisé qu'à
    // partir de Task2

    // CONSTRUCTORS
    private static Game game;

    public Game(int w_ncell, int h_ncell) {
        assert w_ncell > 0 && h_ncell > 0;
        this.width_ncell = w_ncell;
        this.height_ncell = h_ncell;
        this.width_cm = cmPerCell * w_ncell;
        this.height_cm = cmPerCell * h_ncell;

        assert width_ncell * cmPerCell == width_cm && height_ncell * cmPerCell == height_cm;

        this.grid = new Grid(this);
        this.isu = new ISU(this);
        this.grid.set(this.isu);
        this.isu.set(this.grid);
        Game.game = this;
        //this.pict = null;
    }

    // GETTER

    public Game(double w_cm, double h_cm) {
        assert w_cm > 0 && h_cm > 0;

        this.width_cm = w_cm;
        this.height_cm = h_cm;

        width_ncell = (int) (width_cm / cmPerCell);
        height_ncell = (int) (height_cm / cmPerCell);

        grid = new Grid(this);
        isu = new ISU(this);
        this.grid.set(this.isu);
        this.isu.set(this.grid);

        Game.game = this;
        //pict = null;
    }

    public static Game game() {
        return game;
    }

    // SHOW

    public void show(PrintStream ps) {
        ps.println("===== GAME =====");

        ps.println("width_ncell  = " + width_ncell);
        ps.println("height_ncell = " + height_ncell);

        ps.println("width_cm  = " + width_cm);
        ps.println("height_cm = " + height_cm);

        ps.println("cmPerCell = " + cmPerCell);
        ps.println("pixelPerCm = " + pixelPerCm);

        ps.println("torus X = " + torusOnXaxis);
        ps.println("torus Y = " + torusOnYaxis);
    }
}
