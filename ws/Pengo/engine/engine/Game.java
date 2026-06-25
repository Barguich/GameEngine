// == GAME ==
package engine;

import java.io.PrintStream;

import geometry.Grid;
import geometry.ISU;

public class Game {

	// Configuration générale du monde.
	// Le tore permet de faire réapparaître une entité de l'autre côté
	// lorsqu'elle sort par un bord.
	public boolean torusOnXaxis = true;
	public boolean torusOnYaxis = true;

	// Conversion entre la grille logique, les centimètres et les pixels.
	public final double cmPerCell = 3.7;
	public final int pixelPerCm = 2;

	// Taille du monde dans les deux repères utilisés par le moteur.
	public int width_ncell;
	public int height_ncell;
	public double width_cm;
	public double height_cm;

	// Références globales utilisées par les autres classes du moteur.
	private static Game game;
	private static ISU isu;
	private static Grid grid;

	// Constructeur principal : on crée le monde à partir de sa taille en cellules.
	public Game(int w_ncell, int h_ncell) {
		this(w_ncell, h_ncell, true, true);
	}

	// Constructeur utile pour créer un monde à partir de dimensions continues.
	public Game(double w_cm, double h_cm) {
		game = this;
		this.width_cm = w_cm;
		this.height_cm = h_cm;
		this.width_ncell = (int) (w_cm / cmPerCell);
		this.height_ncell = (int) (h_cm / cmPerCell);
		init();
	}

	// Constructeur utilisé quand on veut choisir explicitement
	// si le monde est torique ou non sur chaque axe.
	public Game(int w_ncell, int h_ncell, boolean torusOnXaxis, boolean torusOnYaxis) {
		game = this;
		this.torusOnXaxis = torusOnXaxis;
		this.torusOnYaxis = torusOnYaxis;
		this.width_ncell = w_ncell;
		this.height_ncell = h_ncell;
		this.width_cm = w_ncell * cmPerCell;
		this.height_cm = h_ncell * cmPerCell;
		init();
	}

	// Initialise les deux systèmes de coordonnées :
	// - Grid : repère discret en cellules
	// - ISU  : repère continu en centimètres
	private void init() {
		isu = new ISU(this);
		grid = new Grid(this);
		isu.set(grid);
	}

	public static Game game() {
		return game;
	}

	public static ISU isu() {
		return isu;
	}

	public static Grid grid() {
		return grid;
	}

	// Méthode de debug pour vérifier rapidement la configuration du monde.
	public void show(PrintStream ps) {
		ps.println("Game:");
		ps.println("width_ncell = " + width_ncell);
		ps.println("height_ncell = " + height_ncell);
		ps.println("width_cm = " + width_cm);
		ps.println("height_cm = " + height_cm);
		ps.println("cmPerCell = " + cmPerCell);
		ps.println("pixelPerCm = " + pixelPerCm);
	}
}