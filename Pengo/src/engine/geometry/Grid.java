package engine.geometry;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import engine.Picture;
import engine.Picture.Pixel;
import engine.model.Entity;
import game.Game;

public class Grid {

	// FIELDS

	private ISU isu;
	private Picture pict;

	private Axis xAxis;
	private Axis yAxis;

	private int width_ncell;
	private int height_ncell;

	private double cmPerCell;
	private int pixelPerCm;

	private Cell[][] grid;

	public Grid(Game game) {
		this.isu = game.isu();
		this.pict = game.pict();

		this.cmPerCell = game.getCmpercell();
		this.pixelPerCm = game.getPixelPerCm();

		width_ncell = game.width_ncell();
		height_ncell = game.height_ncell();

		xAxis = new Axis(game.isTorusOnXaxis(), width_ncell);
		yAxis = new Axis(game.isTorusOnYaxis(), height_ncell);

		init();

	}
	// INIT

	private void init() {

		grid = new Cell[width_ncell][height_ncell];

		for (int x = 0; x < width_ncell; x++) {
			for (int y = 0; y < height_ncell; y++) {

				Position p = new Position(x, y);

				grid[x][y] = new Cell(p);
			}
		}
	}
	// GETTER

	public int width() {
		return width_ncell;
	}

	public int height() {
		return height_ncell;
	}

	public ISU isu() {
		return isu;
	}

	public Cell cellAt(Position p) {

		p.normalize();
		return grid[p.x()][p.y()];
	}

	// SHOW
	public void show(PrintStream ps) {

		ps.println("Grid:");

		ps.println("width_ncell = " + width_ncell);
		ps.println("height_ncell = " + height_ncell);
	}
	// ============================
	// DIMENSION
	// ============================

	public class Dimension {

		protected int x_ncell;
		protected int y_ncell;

		public Dimension(int x_ncell, int y_ncell) {

			this.x_ncell = x_ncell;
			this.y_ncell = y_ncell;

			normalize();
		}

		public int x() {
			return x_ncell;
		}

		public int y() {
			return y_ncell;
		}

		public void normalize() {

			x_ncell = xAxis.normalize(x_ncell);
			y_ncell = yAxis.normalize(y_ncell);
		}

		// EQUALS / EQUIV
		@Override
		public boolean equals(Object o) {

			if (!(o instanceof Dimension)) {
				return false;
			}

			Dimension d = (Dimension) o;

			return x_ncell == d.x_ncell && y_ncell == d.y_ncell;
		}

		// on tient compte de la normalization
		public boolean equiv(Dimension d) {

			if (d == null) {
				return false;
			}
			Dimension copy = new Dimension(d.x(), d.y());
			return this.equals(copy);
		}

		public ISU.Dimension toISUDimension() {

			return isu.new Dimension(x_ncell * cmPerCell, y_ncell * cmPerCell);
		}

		public void show(PrintStream ps) {

			ps.println("Dimension(" + x_ncell + "," + y_ncell + ")");
		}
	}
	// ============================
	// VECTOR
	// ============================

	public class Vector extends Dimension {

		public Vector(int x_ncell, int y_ncell) {
			super(x_ncell, y_ncell);
		}

		public void add(Vector v) {
			x_ncell += v.x();
			y_ncell += v.y();

			normalize();
		}

		public void show(PrintStream ps) {

			ps.println("Vector(" + x_ncell + "," + y_ncell + ")");
		}
	}

	// ============================
	// POSITION
	// ============================

	public class Position extends Dimension {

		public Position(int x_ncell, int y_ncell) {
			super(x_ncell, y_ncell);
		}

		public Position copy() {
			return new Position(x_ncell, y_ncell);
		}

		@Override
		public boolean equals(Object o) {

			if (!(o instanceof Position)) {
				return false;
			}

			Position p = (Position) o;

			return x_ncell == p.x_ncell && y_ncell == p.y_ncell;
		}

		public boolean equiv(Position p) {

			if (p == null) {
				return false;
			}

			Position copy = new Position(p.x(), p.y());

			return this.equals(copy);
		}

		public void translate(Vector v) {

			x_ncell += v.x();
			y_ncell += v.y();

			normalize();
		}

		public void moveNorth(int n_ncell) {
			translate(new Vector(0, -n_ncell));
		}

		public void rotateAround(Grid.Position position, int angle_degree) {
			// translation vers origine
			int dx = x_ncell - position.x();
			int dy = y_ncell - position.y();

			// angle en radians
			double angle = Math.toRadians(angle_degree);

			// rotation
			int newX = position.x() + (int) Math.round(dx * Math.cos(angle) - dy * Math.sin(angle));

			int newY = position.y() + (int) Math.round(dx * Math.sin(angle) + dy * Math.cos(angle));

			// mise à jour
			x_ncell = newX;
			y_ncell = newY;

			// tore
			normalize();
		}

		public double distanceTo(Position p) {

			double dx = xAxis.distance(x_ncell, p.x());
			double dy = yAxis.distance(y_ncell, p.y());

			return Math.sqrt(dx * dx + dy * dy);
		}

		public ISU.Coord toISUCoord() {

			return isu.new Coord(x_ncell * cmPerCell, y_ncell * cmPerCell);
		}

		public ISU.Coord toISUCoordCentered() {

			return isu.new Coord(x_ncell * cmPerCell + cmPerCell / 2.0, y_ncell * cmPerCell + cmPerCell / 2.0);
		}

		public Picture.Pixel toPicturePixel() {

			ISU.Coord coord = toISUCoordCentered();

			int x_pixel = (int) (coord.x() * pixelPerCm);
			int y_pixel = (int) (coord.y() * pixelPerCm);

			return pict.new Pixel(x_pixel, y_pixel);
		}

		public void show(PrintStream ps) {

			ps.println("Position(" + x_ncell + "," + y_ncell + ")");
		}

		public Grid grid() {
			return Grid.this;
		}
	}

	// ============================
	// CELL
	// ============================

	public class Cell {

		private Grid.Dimension size;

		public Grid.Position position;

		private List<Entity> entities;

		public Cell(Position p) {

			position = p.copy();

			size = new Dimension(1, 1);

			entities = new ArrayList<Entity>();
		}

		public void add(Entity e) {
			if (!entities.contains(e)) {
				entities.add(e);
			}
		}

		public void remove(Entity e) {
			entities.remove(e);
		}

		public boolean contains(Entity e) {
			return entities.contains(e);
		}

		public void show(PrintStream ps) {

			ps.println("Cell(" + position.x() + "," + position.y() + ")");
		}

		public Position position() {
			return position;
		}
	}

}
