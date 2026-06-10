package engine;
// = ISU =

import java.io.PrintStream;

import game.Game;

public class ISU {

	// FIELDS

	private final Axis xAxis, yAxis;
	private final ISU isu;
	private Grid grid;
	private Game game;

	// CONSTRUCTOR

	public ISU(Game game) {
		assert (game != null);
		this.game = game;

		isu = this;

		xAxis = new Axis(game.isTorusOnXaxis(), game.width_cm());
		yAxis = new Axis(game.isTorusOnYaxis(), game.height_cm());
	}
	public double width_cm() {
	    return game.width_cm();
	}

	public double height_cm() {
	    return game.height_cm();
	}

	// SETTER

	public void set(Grid grid) {
		assert (grid != null);

		this.grid = grid;
	}

	// == DIMENSION (cm) ==

	public class Dimension {
		protected double x_cm, y_cm;

		// CONSTRUCTOR

		public Dimension(double x_cm, double y_cm) {
			setxy(x_cm, y_cm);
		}

		// GEOMETRY

		void normalize() {
			x_cm = xAxis.normalize(x_cm);
			y_cm = yAxis.normalize(y_cm);
		}

		// SETTER

		void setxy(double x_cm, double y_cm) {
			this.x_cm = x_cm;
			this.y_cm = y_cm;

			normalize();
		}

		// GETTER

		public ISU isu() {
			return isu;
		}


		// EQUALS / EQUIV

		public boolean equals(Object o) {
			if (!(o instanceof Dimension)) {
				return false;
			}

			Dimension d = (Dimension) o;

			return x_cm == d.x_cm && y_cm == d.y_cm;
		}

		public boolean equiv(Dimension d) {
			if (d == null) {
				return false;
			}
			Dimension copy = new Dimension(d.x(), d.y());
			return this.equals(copy);
		}

		// GETTER

		public double x() {
			return x_cm;
		}

		public double y() {
			return y_cm;
		}

		// FACTORY

		ISU.Vector mkScaledVector(double factor) {
			return new Vector(x_cm * factor, y_cm * factor);
		}

		ISU.Vector mkScaledVector(double xFactor, double yFactor) {
			return new Vector(x_cm * xFactor, y_cm * yFactor);
		}

		ISU.Vector mkVector() {
			return new Vector(x_cm, y_cm);
		}

		// SHOW

		void show(PrintStream ps) {
			ps.println("Dimension(" + x_cm + ", " + y_cm + ")");
		}

	}

	// == POINT ==

	public class Coord extends Dimension {

		// CONSTRUCTOR

		public Coord(double x_cm, double y_cm) {
			super(x_cm, y_cm);
		}

		// SHOW

		void show(PrintStream ps) {
			ps.println("Coord(" + x_cm + ", " + y_cm + ")");
		}

		// EQUALS

		public boolean equals(Object o) {
			if (!(o instanceof Coord)) {
				return false;
			}

			Coord c = (Coord) o;

			return x_cm == c.x_cm && y_cm == c.y_cm;
		}

		// FACTORY

		public ISU.Vector mkVectorToward(Coord target) {
			assert (target != null);

			return new Vector(target.x() - x_cm, target.y() - y_cm);
		}

		// CONVERSION

		public Grid.Position toGridPosition() {
			assert (grid != null);

			int x_ncell = (int) (x_cm / Game.getCmpercell());
			int y_ncell = (int) (y_cm / Game.getCmpercell());

			return grid.new Position(x_ncell, y_ncell);
		}

		// TRANSLATION

		public void translate(ISU.Vector v) {
			assert (v != null);

			x_cm += v.x();
			y_cm += v.y();

			normalize();
		}

		ISU.Coord mkTranslated(ISU.Vector v) {
			Coord copy = mkCopy();
			copy.translate(v);
			return copy;
		}

		// COPY

		public ISU.Coord mkCopy() {
			return new Coord(x_cm, y_cm);
		}

		// ROTATION

		/**
		 * @apiNote rotation around the origin (0,0)
		 * @param angle_degree
		 */
		public void rotation(int angle_degree) {
			double angle = Math.toRadians(angle_degree);

			double oldX = x_cm;
			double oldY = y_cm;

			x_cm = oldX * Math.cos(angle) - oldY * Math.sin(angle);
			y_cm = oldX * Math.sin(angle) + oldY * Math.cos(angle);

			normalize();
		}

		/**
		 * @apiNote rotation around the given center
		 * @param center
		 * @param angle_degree
		 */
		public void rotateAround(Coord center, int angle_degree) {
			assert (center != null);

			x_cm = x_cm - center.x();
			y_cm = y_cm - center.y();

			rotation(angle_degree);

			x_cm = x_cm + center.x();
			y_cm = y_cm + center.y();

			normalize();
		}

		// DISTANCE

		public double distanceTo(Coord pt) {
			assert (pt != null);

			double dx = xAxis.distance(x_cm, pt.x());
			double dy = yAxis.distance(y_cm, pt.y());

			return Math.sqrt(dx * dx + dy * dy);
		}

	}

	// == VECTOR ==

	/**
	 * @apiNote The Vector class defines canonical vectors with origin in (0,0)
	 *          poiting at a target coordinate.
	 * @apiNote Canonocal vectors are defined by their target Coord.
	 */
	public class Vector extends Dimension {

		// CONSTRUCTOR

		public Vector(double targetX_cm, double targetY_cm) {
			super(targetX_cm, targetY_cm);
		}

		// OPERATOR

		void add(Vector v) {
			assert (v != null);

			x_cm += v.x();
			y_cm += v.y();

			normalize();
		}

		void scale(double factor) {
			x_cm *= factor;
			y_cm *= factor;

			normalize();
		}

		void scale(double xFactor, double yFactor) {
			x_cm *= xFactor;
			y_cm *= yFactor;

			normalize();
		}

		/**
		 * @apiNote produit scalaire
		 * @param v
		 * @return le produit scalaire de `this` et du vecteur v
		 */
		public double dot(ISU.Vector v) {
			assert (v != null);

			return x_cm * v.x() + y_cm * v.y();
		}

		public double norm() {
			return Math.sqrt(x_cm * x_cm + y_cm * y_cm);
		}

		/**
		 * @apiNote rend le vecteur unitaire, ie. de norme = 1
		 */
		public void unity() {
			double n = norm();

			assert (n != 0);

			x_cm = x_cm / n;
			y_cm = y_cm / n;
		}

		// TURN

		/**
		 * @apiNote turn the vector itself
		 * @implNote the center of the rotation is the origin of the vector
		 * @param angle_degree
		 */
		public void turn(int angle_degree) {
			double angle = Math.toRadians(angle_degree);

			double oldX = x_cm;
			double oldY = y_cm;

			x_cm = oldX * Math.cos(angle) - oldY * Math.sin(angle);
			y_cm = oldX * Math.sin(angle) + oldY * Math.cos(angle);

			normalize();
		}

	}

}