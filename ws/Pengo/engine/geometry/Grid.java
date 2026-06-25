// = GRID =
package geometry;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import engine.Game;
import model.Entity;

public class Grid {

	// Grille discrète du monde : une case contient une position et des entités
	private final ISU isu;
	private final Axis xAxis, yAxis;
	private final int width_ncell, height_ncell;
	private Cell[][] grid;

	public Grid(Game game) {
		this.width_ncell = Game.game().width_ncell;
		this.height_ncell = Game.game().height_ncell;
		this.isu = Game.isu();

		// Les axes gèrent la normalisation, notamment si le monde est torique
		this.xAxis = new Axis(Game.game().torusOnXaxis, this.width_ncell);
		this.yAxis = new Axis(Game.game().torusOnYaxis, this.height_ncell);

		init();
	}

	// Création de toutes les cellules de la grille
	private void init() {
		this.grid = new Cell[this.width_ncell][this.height_ncell];

		for (int i = 0; i < this.width_ncell; i++) {
			for (int j = 0; j < this.height_ncell; j++) {
				this.grid[i][j] = new Cell(new Position(i, j));
			}
		}
	}

	public int width() {
		return width_ncell;
	}

	public int height() {
		return height_ncell;
	}

	// Retourne la cellule correspondant à une position donnée
	public Grid.Cell cellAt(Grid.Position p) {
		int x = xAxis.normalize(p.x());
		int y = yAxis.normalize(p.y());

		if (Log.FINER)
			Log.logger.log(Level.FINER, "cellAt: pos({0},{1}) -> cell[{2}][{3}]", new Object[] { p.x(), p.y(), x, y });

		return this.grid[x][y];
	}

	public void show(PrintStream ps) {
		ps.println("Grid:");
		ps.println("width_ncell = " + width_ncell);
		ps.println("height_ncell = " + height_ncell);
	}

	// Dimension exprimée en nombre de cellules
	public class Dimension {
		protected int x_ncell, y_ncell;

		public Dimension(int x_ncell, int y_ncell) {
			this.x_ncell = xAxis.normalize(x_ncell);
			this.y_ncell = yAxis.normalize(y_ncell);
		}

		public int x() {
			return x_ncell;
		}

		public int y() {
			return y_ncell;
		}

		// Normalise la dimension selon les règles des axes
		public void normalize() {
			this.x_ncell = xAxis.normalize(this.x_ncell);
			this.y_ncell = yAxis.normalize(this.y_ncell);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || !(o instanceof Dimension))
				return false;

			Dimension d = (Dimension) o;
			return (this.x_ncell == d.x_ncell) && (this.y_ncell == d.y_ncell);
		}

		// Égalité tenant compte de la normalisation des axes
		public boolean equiv(Dimension d) {
			if (d == null)
				return false;

			return (xAxis.normalize(x_ncell) == xAxis.normalize(d.x_ncell))
					&& (yAxis.normalize(y_ncell) == yAxis.normalize(d.y_ncell));
		}

		// Conversion d'une dimension de grille vers une dimension réelle en cm
		public ISU.Dimension toISUDimension() {
			double cm = Game.game().cmPerCell;
			return isu.new Dimension(x_ncell * cm, y_ncell * cm);
		}

		public void show(PrintStream ps) {
			ps.println("Dimension(" + x_ncell + "," + y_ncell + ")");
		}
	}

	// Vecteur de déplacement exprimé en cellules
	public class Vector {
		private int x_ncell, y_ncell;

		public Vector(int x_ncell, int y_ncell) {
			this.x_ncell = x_ncell;
			this.y_ncell = y_ncell;
		}

		public int x() {
			return x_ncell;
		}

		public int y() {
			return y_ncell;
		}

		public void add(Vector v) {
			x_ncell += v.x_ncell;
			y_ncell += v.y_ncell;
		}

		public double length() {
			return Math.sqrt(x_ncell * x_ncell + y_ncell * y_ncell);
		}

		public void show(PrintStream ps) {
			ps.println("Vector(" + x_ncell + "," + y_ncell + ")");
		}
	}

	// Position d'une entité ou d'une cellule dans la grille
	public class Position {
		private int x_ncell, y_ncell;

		public Position(int x_ncell, int y_ncell) {
			this.x_ncell = xAxis.normalize(x_ncell);
			this.y_ncell = yAxis.normalize(y_ncell);
		}

		public int x() {
			return x_ncell;
		}

		public int y() {
			return y_ncell;
		}

		public Grid.Position copy() {
			return new Position(this.x_ncell, this.y_ncell);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof Position))
				return false;

			Position p = (Position) o;
			return (this.x_ncell == p.x_ncell) && (this.y_ncell == p.y_ncell);
		}

		// Compare deux positions en tenant compte du monde torique
		public boolean equiv(Position p) {
			if (p == null)
				return false;

			return (xAxis.normalize(x_ncell) == xAxis.normalize(p.x_ncell))
					&& (yAxis.normalize(y_ncell) == yAxis.normalize(p.y_ncell));
		}

		// Déplace la position en normalisant le résultat
		public void translate(Vector v) {
			if (v == null)
				return;

			x_ncell = xAxis.normalize(x_ncell + v.x());
			y_ncell = yAxis.normalize(y_ncell + v.y());

			if (Log.FINER)
				Log.logger.log(Level.FINER, "translate: +({0},{1}) -> ({2},{3})",
						new Object[] { v.x(), v.y(), x_ncell, y_ncell });
		}

		public void moveNorth(int n_ncell) {
			y_ncell = yAxis.normalize(y_ncell - n_ncell);
		}

		// Rotation d'une position autour d'une autre position
		public void rotateAround(Grid.Position position, int angle_degree) {
			int dx = x_ncell - position.x();
			int dy = y_ncell - position.y();
			double angle = Math.toRadians(angle_degree);

			int newX = position.x() + (int) Math.round(dx * Math.cos(angle) - dy * Math.sin(angle));
			int newY = position.y() + (int) Math.round(dx * Math.sin(angle) + dy * Math.cos(angle));

			x_ncell = xAxis.normalize(newX);
			y_ncell = yAxis.normalize(newY);
		}

		// Distance entre deux positions en tenant compte des axes
		public double distanceTo(Position p) {
			double dx = xAxis.distance(x_ncell, p.x_ncell);
			double dy = yAxis.distance(y_ncell, p.y_ncell);
			return Math.sqrt(dx * dx + dy * dy);
		}

		// Conversion vers le repère continu en cm
		public ISU.Coord toISUCoord() {
			return isu.new Coord(this.x_ncell * Game.game().cmPerCell, this.y_ncell * Game.game().cmPerCell);
		}

		// Conversion vers le centre de la cellule en cm
		public ISU.Coord toISUCoordCentered() {
			return isu.new Coord((this.x_ncell + .5) * Game.game().cmPerCell,
					(this.y_ncell + .5) * Game.game().cmPerCell);
		}

		@Override
		public String toString() {
			return "(" + x_ncell + "," + y_ncell + ")";
		}

		public void show(PrintStream ps) {
			ps.println("Position(" + x_ncell + "," + y_ncell + ")");
		}

		public Grid grid() {
			return Grid.this;
		}
	}

	// Case de la grille contenant les entités présentes à cette position
	public class Cell {
		private Grid.Dimension size;
		private Grid.Position position;
		private List<Entity> entities;

		public Cell(Position p) {
			this.position = p;
			this.size = new Dimension(1, 1);
			this.entities = new ArrayList<>();
		}

		public Grid.Position position() {
			return this.position;
		}

		public List<Entity> entities() {
			return this.entities;
		}

		public void add(Entity e) {
			if (!entities.contains(e)) {
				entities.add(e);
			}
		}

		public void remove(Entity e) {
			this.entities.remove(e);
		}

		public boolean contains(Entity e) {
			return this.entities.contains(e);
		}

		public void show(PrintStream ps) {
			ps.println("Cell(" + position.x() + "," + position.y() + ")");
			ps.println("  size = " + size);
			ps.println("  entities = " + entities);
		}
	}
}