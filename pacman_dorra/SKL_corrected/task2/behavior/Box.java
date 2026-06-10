package behavior;

public class Box {

	private final double xmin, ymin, xmax, ymax;

	public Box(double xmin, double ymin, double xmax, double ymax) {
		this.xmax = xmax;
		this.xmin = xmin;
		this.ymin = ymin;
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

	// UNION

	public static Box union(Box b1, Box b2) {

	    double xmin = Math.min(b1.xmin(), b2.xmin());
	    double ymin = Math.min(b1.ymin(), b2.ymin());

	    double xmax = Math.max(b1.xmax(), b2.xmax());
	    double ymax = Math.max(b1.ymax(), b2.ymax());

	    return new Box(xmin, ymin, xmax, ymax);
	}

	public boolean overlaps(Box box) {
		return this.xmax >= box.xmin && box.xmax >= this.xmin && this.ymax >= box.ymin && box.ymax >= this.ymin;
	}
}