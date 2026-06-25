// = ISU =
package geometry;

import java.io.PrintStream;
import java.util.logging.Level;

import engine.Game;

public class ISU {

	// Repère continu du jeu, exprimé en centimètres
	private Axis xAxis, yAxis;

	// Lien avec la grille discrète du jeu
	private Grid grid;

	// Configuration globale du jeu
	private Game game;

	public ISU(Game game) {
		this.game = game;

		// Les axes gèrent la normalisation, notamment dans un monde torique
		this.xAxis = new Axis(game.torusOnXaxis, game.width_cm);
		this.yAxis = new Axis(game.torusOnYaxis, game.height_cm);
	}

	public void set(Grid grid) {
		this.grid = grid;
	}

	// Dimension exprimée dans le repère continu, donc en centimètres
	public class Dimension {
		protected double x_cm, y_cm;

		public Dimension(double x_cm, double y_cm) {
			setxy(x_cm, y_cm);
		}

		public double x() {
			return x_cm;
		}

		public double y() {
			return y_cm;
		}

		public ISU isu() {
			return ISU.this;
		}

		public void setxy(double x_cm, double y_cm) {
			this.x_cm = x_cm;
			this.y_cm = y_cm;
			normalize();
		}

		// Ramène les valeurs dans les limites du monde si le tore est actif
		public void normalize() {
			x_cm = xAxis.normalize(x_cm);
			y_cm = yAxis.normalize(y_cm);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof Dimension))
				return false;

			Dimension d = (Dimension) o;
			return x_cm == d.x_cm && y_cm == d.y_cm;
		}

		// Compare deux dimensions en tenant compte de la normalisation des axes
		public boolean equiv(Dimension d) {
			if (d == null)
				return false;

			return xAxis.normalize(x_cm) == xAxis.normalize(d.x_cm) && yAxis.normalize(y_cm) == yAxis.normalize(d.y_cm);
		}

		// Crée un vecteur ayant les mêmes composantes que cette dimension
		public Vector mkVector() {
			return new Vector(x_cm, y_cm);
		}

		public Vector mkScaledVector(double factor) {
			return new Vector(x_cm * factor, y_cm * factor);
		}

		public Vector mkScaledVector(double xFactor, double yFactor) {
			return new Vector(x_cm * xFactor, y_cm * yFactor);
		}

		public void show(PrintStream ps) {
			ps.println("Dimension(" + x_cm + ", " + y_cm + ")");
		}
	}

	// Coordonnée d'un point dans le monde continu
	public class Coord extends Dimension {

		public Coord(double x_cm, double y_cm) {
			super(x_cm, y_cm);
		}

		// Calcule le vecteur le plus court vers une autre coordonnée
		public ISU.Vector mkVectorToward(Coord target) {
			if (target == null)
				return new Vector(0, 0);

			double dx = target.x_cm - this.x_cm;
			double dy = target.y_cm - this.y_cm;

			// Dans un monde torique, on choisit le chemin le plus court
			if (xAxis.onTorus) {
				if (Math.abs(dx) > xAxis.perimeter / 2)
					dx -= Math.signum(dx) * xAxis.perimeter;
			}

			if (yAxis.onTorus) {
				if (Math.abs(dy) > yAxis.perimeter / 2)
					dy -= Math.signum(dy) * yAxis.perimeter;
			}

			if (Log.FINER)
				Log.logger.log(Level.FINER, "mkVectorToward: dx={0} dy={1}", new Object[] { dx, dy });

			return new Vector(dx, dy);
		}

		// Convertit une coordonnée continue en position de grille
		public Grid.Position toGridPosition() {
			int x_ncell = (int) Math.floor(x_cm / game.cmPerCell);
			int y_ncell = (int) Math.floor(y_cm / game.cmPerCell);

			if (Log.FINER)
				Log.logger.log(Level.FINER, "toGridPosition: ({0},{1})cm -> cell({2},{3})",
						new Object[] { x_cm, y_cm, x_ncell, y_ncell });

			return grid.new Position(x_ncell, y_ncell);
		}

		// Déplace la coordonnée avec un vecteur en cm
		public void translate(ISU.Vector v) {
			if (v == null)
				return;

			x_cm = xAxis.normalize(x_cm + v.x_cm);
			y_cm = yAxis.normalize(y_cm + v.y_cm);
		}

		public ISU.Coord mkTranslated(ISU.Vector v) {
			Coord copy = mkCopy();
			copy.translate(v);
			return copy;
		}

		public ISU.Coord mkCopy() {
			return new Coord(x_cm, y_cm);
		}

		// Rotation autour de l'origine du repère
		public void rotation(int angle_degree) {
			double rad = Math.toRadians(angle_degree);
			double cos = Math.cos(rad);
			double sin = Math.sin(rad);

			double nx = x_cm * cos - y_cm * sin;
			double ny = x_cm * sin + y_cm * cos;

			x_cm = xAxis.normalize(nx);
			y_cm = yAxis.normalize(ny);
		}

		// Rotation autour d'un autre point
		public void rotateAround(Coord center, int angle_degree) {
			if (center == null)
				return;

			x_cm -= center.x_cm;
			y_cm -= center.y_cm;

			rotation(angle_degree);

			x_cm = xAxis.normalize(x_cm + center.x_cm);
			y_cm = yAxis.normalize(y_cm + center.y_cm);
		}

		// Distance entre deux coordonnées, compatible avec le monde torique
		public double distanceTo(Coord pt) {
			if (pt == null)
				return 0;

			double dx = xAxis.distance(x_cm, pt.x_cm);
			double dy = yAxis.distance(y_cm, pt.y_cm);

			return Math.sqrt(dx * dx + dy * dy);
		}

		@Override
		public void show(PrintStream ps) {
			ps.println("Coord(" + x_cm + ", " + y_cm + ")");
		}
	}

	// Vecteur du repère continu, utilisé pour les déplacements en cm
	public class Vector {
		private double x_cm, y_cm;

		public Vector(double targetX_cm, double targetY_cm) {
			this.x_cm = targetX_cm;
			this.y_cm = targetY_cm;
		}

		public double x() {
			return x_cm;
		}

		public double y() {
			return y_cm;
		}

		// Addition de deux vecteurs
		public void add(Vector v) {
			if (v == null)
				return;

			x_cm += v.x_cm;
			y_cm += v.y_cm;
		}

		public void scale(double factor) {
			x_cm *= factor;
			y_cm *= factor;
		}

		public void scale(double xFactor, double yFactor) {
			x_cm *= xFactor;
			y_cm *= yFactor;
		}

		public Vector mkScaledCopy(double factor) {
			return new Vector(x_cm * factor, y_cm * factor);
		}

		// Produit scalaire entre deux vecteurs
		public double dot(ISU.Vector v) {
			if (v == null)
				return 0;

			return x_cm * v.x_cm + y_cm * v.y_cm;
		}

		public double norm() {
			return Math.sqrt(x_cm * x_cm + y_cm * y_cm);
		}

		// Transforme le vecteur en vecteur unitaire
		public void unity() {
			double n = norm();

			if (n == 0)
				return;

			x_cm /= n;
			y_cm /= n;
		}

		// Rotation du vecteur autour de son origine
		public void turn(int angle_degree) {
			double rad = Math.toRadians(angle_degree);
			double cos = Math.cos(rad);
			double sin = Math.sin(rad);

			double nx = x_cm * cos - y_cm * sin;
			double ny = x_cm * sin + y_cm * cos;

			x_cm = nx;
			y_cm = ny;
		}

		public void show(PrintStream ps) {
			ps.println("Vector(" + x_cm + ", " + y_cm + ")");
		}
	}

	public double width_cm() {
		return game.width_cm;
	}

	public double height_cm() {
		return game.height_cm;
	}
}