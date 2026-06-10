package engine;

public class Rect extends Shape implements iShape {

	// FIELDS

	protected double halfWidth, halfHeight;
	protected int angle_degree;

	// CONSTRUCTOR

	public Rect(ISU.Coord center, ISU.Dimension size, int angle_degree) {
		super(center);
		this.angle_degree = angle_degree;
		this.halfWidth = size.x() / 2;
		this.halfHeight = size.y() / 2;
	}

	// TRANSLATION ?

	// ROTATION

	void rotate(int angle_degree) {
		this.angle_degree += angle_degree;
		this.angle_degree = this.angle_degree % 360;

		if (this.angle_degree < 0) {
			this.angle_degree += 360;
		}
	}

	// == INTERSECTION ==
	public boolean intersects(iShape shape) {
		return shape.intersects(this);
	}

}