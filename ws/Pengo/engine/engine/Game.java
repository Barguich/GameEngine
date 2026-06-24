// == GAME ==
package engine;

import java.io.PrintStream;

import geometry.Grid;
import geometry.ISU;

public class Game {

	// Paramètres globaux du monde
	public  boolean torusOnXaxis = false;
	public boolean torusOnYaxis = false;
	public final double cmPerCell = 3.7;
	public final int pixelPerCm = 2;

	// Dimensions du monde exprimées en cellules et en centimètres
	public int width_ncell;
	public int height_ncell;
	public double width_cm;
	public double height_cm;

	// Instance unique du jeu et systèmes de coordonnées associés
	private static Game game;
	private static ISU isu;
	private static Grid grid;

	// Création d'un monde à partir d'un nombre de cellules
	public Game(int w_ncell, int h_ncell) {
		game = this;
		this.width_ncell = w_ncell;
		this.height_ncell = h_ncell;
		this.width_cm = w_ncell * cmPerCell;
		this.height_cm = h_ncell * cmPerCell;
		init();
	}

	// Création d'un monde à partir de dimensions réelles
	public Game(double w_cm, double h_cm) {
		game = this;
		this.width_cm = w_cm;
		this.height_cm = h_cm;
		this.width_ncell = (int) (w_cm / cmPerCell);
		this.height_ncell = (int) (h_cm / cmPerCell);
		init();
	}

	// Initialise les systèmes de coordonnées utilisés par le moteur
	private void init() {
		isu = new ISU(this);
		grid = new Grid(this);
		isu.set(grid);
	}

	// Accès à l'instance courante du jeu
	public static Game game() {
		return game;
	}

	// Accès au repère continu (cm)
	public static ISU isu() {
		return isu;
	}

	// Accès au repère discret (cellules)
	public static Grid grid() {
		return grid;
	}

	// Affichage des caractéristiques du monde
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