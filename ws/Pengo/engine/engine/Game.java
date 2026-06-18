// == GAME ==
package engine;

import java.io.PrintStream;

import engine.geometry.Grid;
import engine.geometry.ISU;

public class Game {

	// CONSTANTS
	public final boolean torusOnXaxis = true;
	public final boolean torusOnYaxis = true;
	public final double cmPerCell = 3.7;
	public final int pixelPerCm = 7;

	// FIELDS
	public int width_ncell;
	public int height_ncell;
	public double width_cm;
	public double height_cm;

	private static Game game;
	private static ISU isu;
	private static Grid grid;

	// CONSTRUCTORS
	public Game(int w_ncell, int h_ncell) {
		game = this;
		this.width_ncell = w_ncell;
		this.height_ncell = h_ncell;
		this.width_cm = w_ncell * cmPerCell;
		this.height_cm = h_ncell * cmPerCell;
		init();
	}

	public Game(double w_cm, double h_cm) {
		game = this;
		this.width_cm = w_cm;
		this.height_cm = h_cm;
		this.width_ncell = (int) (w_cm / cmPerCell);
		this.height_ncell = (int) (h_cm / cmPerCell);
		init();
	}

	private void init() {
		isu = new ISU(this);
		grid = new Grid(this);
		isu.set(grid);
	}

	// GETTERS
	public static Game game() {
		return game;
	}

	public static ISU isu() {
		return isu;
	}

	public static Grid grid() {
		return grid;
	}

	// SHOW
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
