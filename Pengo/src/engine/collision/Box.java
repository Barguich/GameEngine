package engine;

public class Box {

	private final double xmin, ymin, xmax, ymax;

	public Box(double xmin, double ymin, double xmax, double ymax) {

		if (xmin > xmax)
			throw new IllegalArgumentException("xmin > xmax");

		if (ymin > ymax)
			throw new IllegalArgumentException("ymin > ymax");

		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
	}

	// GETTERS

	public double xmin() {
		return xmin;
	}

	public double xmax() {
		return xmax;
	}

	public double ymin() {
		return ymin;
	}

	public double ymax() {
		return ymax;
	}

	public double width() {
		return xmax - xmin;
	}

	public double height() {
		return ymax - ymin;
	}

	public double centerX() {
		return (xmin + xmax) / 2;
	}

	public double centerY() {
		return (ymin + ymax) / 2;
	}

	// COLLISION

	public boolean overlaps(Box other) {
		if (other == null)
			return false;

		return this.xmax > other.xmin && this.xmin < other.xmax && this.ymax > other.ymin && this.ymin < other.ymax;
	}

	// UNION

	public static Box union(Box b1, Box b2) {

		if (b1 == null)
			return b2;

		if (b2 == null)
			return b1;

		return new Box(Math.min(b1.xmin, b2.xmin), Math.min(b1.ymin, b2.ymin), Math.max(b1.xmax, b2.xmax),
				Math.max(b1.ymax, b2.ymax));
	}

	@Override
	public String toString() {
		return "Box[" + xmin + "," + ymin + " -> " + xmax + "," + ymax + "]";
	}
}
