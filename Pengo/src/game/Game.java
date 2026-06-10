package game;

import java.io.PrintStream;

import engine.Grid;
import engine.ISU;
import engine.Picture;

public class Game {

	// CONSTANT

	private final boolean torusOnXaxis = true;
	private final boolean torusOnYaxis = true;

	private final static double cmPerCell = 3.7;
	private final static int pixelPerCm = 7;

	// FIELDS

	private int width_ncell;
	private int height_ncell;

	private double width_cm;
	private double height_cm;

	private Grid grid;
	private ISU isu;
	private Picture pict;

	// CONSTRUCTORS
	public Game(int w_ncell, int h_ncell) {
		this.width_ncell = w_ncell;
		this.height_ncell = h_ncell;
		this.width_cm = w_ncell * cmPerCell;
		this.height_cm = h_ncell * cmPerCell;

		// assert (this.width_ncell * cmPerCell == this.width_cm);
		// assert (this.height_ncell * cmPerCell == this.height_cm);

		isu = new ISU(this);
		grid = new Grid(this);
		isu.set(grid);
	}

	public Game(double w_cm, double h_cm) {

		this.width_cm = w_cm;
		this.height_cm = h_cm;
		this.width_ncell = (int) (w_cm / cmPerCell);
		this.height_ncell = (int) (h_cm / cmPerCell);

		// assert (this.width_ncell * cmPerCell == this.width_cm);
		// assert (this.height_ncell * cmPerCell == this.height_cm);

		isu = new ISU(this);
		grid = new Grid(this);
		isu.set(grid);
	}

	// GETTER
	public boolean isTorusOnYaxis() {
		return torusOnYaxis;

	}

	public boolean isTorusOnXaxis() {
		return torusOnXaxis;

	}

	public static int getPixelPerCm() {
		return pixelPerCm;

	}

	public static double getCmpercell() {
		return cmPerCell;

	}

	public int width_ncell() {
		return width_ncell;
	}

	public int height_ncell() {
		return height_ncell;
	}

	public double width_cm() {
		return width_cm;
	}

	public double height_cm() {
		return height_cm;
	}

	public Grid grid() {
		return grid;
	}

	public ISU isu() {
		return isu;
	}

	public Picture pict() {
		return pict;
	}

	public Game game() {
		return this;
	}

	// SHOW

	public void show(PrintStream ps) {

		ps.println("Game:");

		ps.println("width_ncell = " + width_ncell);
		ps.println("height_ncell = " + height_ncell);

		ps.println("width_cm = " + width_cm);
		ps.println("height_cm = " + height_cm);

		ps.println("cmPerCell = " + getCmpercell());
		ps.println("pixelPerCm = " + getPixelPerCm());
	}

}
