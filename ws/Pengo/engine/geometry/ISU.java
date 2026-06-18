// = ISU =
package geometry;

import java.io.PrintStream;
import java.util.logging.Level;

import engine.Game;

public class ISU {

	// FIELDS
	private Axis xAxis, yAxis;
	private Grid grid;
	private Game game;

	// CONSTRUCTOR
	public ISU(Game game) {
		this.game = game;
		this.xAxis = new Axis(game.torusOnXaxis, game.width_cm);
		this.yAxis = new Axis(game.torusOnYaxis, game.height_cm);
	}

	// SETTER
	public void set(Grid grid) {
		this.grid = grid;
	}

	// == DIMENSION (cm) ==
	public class Dimension {
		protected double x_cm, y_cm;

		// CONSTRUCTOR
		public Dimension(double x_cm, double y_cm) {
			setxy(x_cm, y_cm);
		}

		// GETTERS
		public double x() {
			return x_cm;
		}

		public double y() {
			return y_cm;
		}

		public ISU isu() {
			return ISU.this;
		}

		// SETTER
		public void setxy(double x_cm, double y_cm) {
			this.x_cm = x_cm;
			this.y_cm = y_cm;
			normalize();
		}

		// GEOMETRY
		public void normalize() {
			x_cm = xAxis.normalize(x_cm);
			y_cm = yAxis.normalize(y_cm);
		}

		// EQUALS / EQUIV
		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (!(o instanceof Dimension))
				return false;
			Dimension d = (Dimension) o;
			return x_cm == d.x_cm && y_cm == d.y_cm;
		}

		public boolean equiv(Dimension d) {
			if (d == null)
				return false;
			return xAxis.normalize(x_cm) == xAxis.normalize(d.x_cm)
					&& yAxis.normalize(y_cm) == yAxis.normalize(d.y_cm);
		}

		// FACTORY
		public Vector mkVector() {
			return new Vector(x_cm, y_cm);
		}

		public Vector mkScaledVector(double factor) {
			return new Vector(x_cm * factor, y_cm * factor);
		}

		public Vector mkScaledVector(double xFactor, double yFactor) {
			return new Vector(x_cm * xFactor, y_cm * yFactor);
		}

		// SHOW
		public void show(PrintStream ps) {
			ps.println("Dimension(" + x_cm + ", " + y_cm + ")");
		}
	}

	// == POINT ==
	public class Coord extends Dimension {

		// CONSTRUCTOR
		public Coord(double x_cm, double y_cm) {
			super(x_cm, y_cm);
		}

		// FACTORY
		public ISU.Vector mkVectorToward(Coord target) {
			assert target != null;
			double dx = target.x_cm - this.x_cm;
			double dy = target.y_cm - this.y_cm;
			if (xAxis.onTorus) {
				if (Math.abs(dx) > xAxis.perimeter / 2)
					dx -= Math.signum(dx) * xAxis.perimeter;
			}
			if (yAxis.onTorus) {
				if (Math.abs(dy) > yAxis.perimeter / 2)
					dy -= Math.signum(dy) * yAxis.perimeter;
			}

			if (Log.FINER)
				Log.logger.log(Level.FINER,
						"mkVectorToward: dx={0} dy={1}", new Object[] { dx, dy });

			return new Vector(dx, dy);
		}

		// CONVERSION
		// CONVERSION
		public Grid.Position toGridPosition() {
			assert grid != null;
			int x_ncell = (int) Math.floor(x_cm / game.cmPerCell);
			int y_ncell = (int) Math.floor(y_cm / game.cmPerCell);

			if (Log.FINER)
				Log.logger.log(Level.FINER,
						"toGridPosition: ({0},{1})cm -> cell({2},{3})",
						new Object[] { x_cm, y_cm, x_ncell, y_ncell });

			return grid.new Position(x_ncell, y_ncell);
		}

		// TRANSLATION
		public void translate(ISU.Vector v) {
			assert v != null;
			x_cm = xAxis.normalize(x_cm + v.x_cm);
			y_cm = yAxis.normalize(y_cm + v.y_cm);
		}

		public ISU.Coord mkTranslated(ISU.Vector v) {
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
			double rad = Math.toRadians(angle_degree);
			double cos = Math.cos(rad);
			double sin = Math.sin(rad);
			double nx = x_cm * cos - y_cm * sin;
			double ny = x_cm * sin + y_cm * cos;
			x_cm = xAxis.normalize(nx);
			y_cm = yAxis.normalize(ny);
		}

		/**
		 * @apiNote rotation around the given center
		 * @param center
		 * @param angle_degree
		 */
		public void rotateAround(Coord center, int angle_degree) {
			assert center != null;
			x_cm -= center.x_cm;
			y_cm -= center.y_cm;
			rotation(angle_degree);
			x_cm = xAxis.normalize(x_cm + center.x_cm);
			y_cm = yAxis.normalize(y_cm + center.y_cm);
		}

		// DISTANCE
		public double distanceTo(Coord pt) {
			assert pt != null;
			double dx = xAxis.distance(x_cm, pt.x_cm);
			double dy = yAxis.distance(y_cm, pt.y_cm);
			return Math.sqrt(dx * dx + dy * dy);
		}

		// SHOW
		@Override
		public void show(PrintStream ps) {
			ps.println("Coord(" + x_cm + ", " + y_cm + ")");
		}
	}

	// == VECTOR ==

	/**
	 * @apiNote The Vector class defines canonical vectors with origin in (0,0)
	 *          pointing at a target coordinate.
	 * @apiNote Canonical vectors are defined by their target Coord.
	 */
	public class Vector {
		private double x_cm, y_cm;

		// CONSTRUCTOR
		public Vector(double targetX_cm, double targetY_cm) {
			this.x_cm = targetX_cm;
			this.y_cm = targetY_cm;
		}

		// GETTER
		public double x() {
			return x_cm;
		}

		public double y() {
			return y_cm;
		}

		// OPERATOR
		public void add(Vector v) {
			assert v != null;
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

		public double dot(ISU.Vector v) {
			assert v != null;
			return x_cm * v.x_cm + y_cm * v.y_cm;
		}

		public double norm() {
			return Math.sqrt(x_cm * x_cm + y_cm * y_cm);
		}

		public void unity() {
			double n = norm();
			if (n == 0)
				return;
			x_cm /= n;
			y_cm /= n;
		}

		// TURN
		/**
		 * @apiNote turn the vector itself
		 * @implNote the center of the rotation is the origin of the vector
		 * @param angle_degree
		 */
		public void turn(int angle_degree) {
			double rad = Math.toRadians(angle_degree);
			double cos = Math.cos(rad);
			double sin = Math.sin(rad);
			double nx = x_cm * cos - y_cm * sin;
			double ny = x_cm * sin + y_cm * cos;
			x_cm = nx;
			y_cm = ny;
		}

		// SHOW
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
