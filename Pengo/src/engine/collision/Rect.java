// = Rect =
package engine.collision;

import engine.geometry.ISU;
import engine.geometry.Vector;
import engine.geometry.Point;

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

	// === Rect/Circle Intersection ===
	public boolean intersects(Circle circle) {
		// on depasse pas et on teste la collisition
		if (intersectsCircleAt(circle, 0, 0, 0, 0)) {
			return true;
		}
		// le cercle depasse on copie
		if (intersectsWithVirtualCircleCopies(circle)) {
			return true;
		}
		// le rect depasse on copie
		if (intersectsWithVirtualRectCopies(circle)) {
			return true;
		}
		return false;
	}

	private boolean intersectsWithVirtualCircleCopies(Circle circle) {
		boolean left = circle.center.x() - circle.radius < 0;
		boolean right = circle.center.x() + circle.radius > isu.width_cm();
		boolean top = circle.center.y() - circle.radius < 0;
		boolean bottom = circle.center.y() + circle.radius > isu.height_cm();

		// on depasse a gauche on test collision avec la copie a droite
		if (left && intersectsCircleAt(circle, isu.width_cm(), 0, 0, 0)) {
			return true;
		}
		// on depasse a droite on test collision avec la copie a gauche
		if (right && intersectsCircleAt(circle, -isu.width_cm(), 0, 0, 0)) {
			return true;
		}
		// on depasse en haut on test collision avec la copie a en bas
		if (top && intersectsCircleAt(circle, 0, isu.height_cm(), 0, 0)) {
			return true;
		}
		// on depasse en bas on test collision avec la copie en haut
		if (bottom && intersectsCircleAt(circle, 0, -isu.height_cm(), 0, 0)) {
			return true;
		}
		// coin depassement en haut et a gauche
		if (left && top && intersectsCircleAt(circle, isu.width_cm(), isu.height_cm(), 0, 0)) {
			return true;
		}
		// coin depassement en bas et a gauche
		if (left && bottom && intersectsCircleAt(circle, isu.width_cm(), -isu.height_cm(), 0, 0)) {
			return true;
		}
		// coin depassement en haut et a droite
		if (right && top && intersectsCircleAt(circle, -isu.width_cm(), isu.height_cm(), 0, 0)) {
			return true;
		}
		// coin deppassement en bas et a droite
		if (right && bottom && intersectsCircleAt(circle, -isu.width_cm(), -isu.height_cm(), 0, 0)) {
			return true;
		}
		// si aucun cas de collision
		return false;
	}

	private boolean intersectsWithVirtualRectCopies(Circle circle) {
		boolean left = rectTouchesLeft(this);
		boolean right = rectTouchesRight(this);
		boolean top = rectTouchesTop(this);
		boolean bottom = rectTouchesBottom(this);

		if (left && intersectsCircleAt(circle, 0, 0, isu.width_cm(), 0)) {
			return true;
		}

		if (right && intersectsCircleAt(circle, 0, 0, -isu.width_cm(), 0)) {
			return true;
		}

		if (top && intersectsCircleAt(circle, 0, 0, 0, isu.height_cm())) {
			return true;
		}

		if (bottom && intersectsCircleAt(circle, 0, 0, 0, -isu.height_cm())) {
			return true;
		}

		if (left && top && intersectsCircleAt(circle, 0, 0, isu.width_cm(), isu.height_cm())) {
			return true;
		}

		if (left && bottom && intersectsCircleAt(circle, 0, 0, isu.width_cm(), -isu.height_cm())) {
			return true;
		}

		if (right && top && intersectsCircleAt(circle, 0, 0, -isu.width_cm(), isu.height_cm())) {
			return true;
		}

		if (right && bottom && intersectsCircleAt(circle, 0, 0, -isu.width_cm(), -isu.height_cm())) {
			return true;
		}

		return false;
	}

	private boolean intersectsCircleAt(Circle circle, double circleDx, double circleDy, double rectDx, double rectDy) {
		Point virtualCircleCenter = new Point(circle.center.x() + circleDx, circle.center.y() + circleDy);

		return new RectCircleIntersection(this, virtualCircleCenter, circle.radius, rectDx, rectDy).intersects();
	}

// === Helping inner class ===
	/**
	 * @implNote Principe
	 *           <UL>
	 *           <LI>translate le centre du cercle vers le repère formé par les axes
	 *           du rectangle,</LI>
	 *           <LI>redresse le repère du rectangle en annulant la rotation du
	 *           rectangle,</LI>
	 *           <LI>détermine le point <i>P</i> du rectangle le plus proche du
	 *           centre <i>C</i> du cercle de façon efficace car le rectangle est
	 *           aligné sur les axes X,Y.</LI>
	 *           </UL>
	 * @implNote Il y a intersection si distance(P,C) < rayon du cercle</LI>
	 */

	class RectCircleIntersection {

		// FIELDS

		private Rect outer;
		private Point center;
		private double radius;
		private double rectDx;
		private double rectDy;

		// CONSTRUCTOR

		RectCircleIntersection(Rect outer, Point center, double radius, double rectDx, double rectDy) {
			this.outer = outer;
			this.center = center;
			this.radius = radius;
			this.rectDx = rectDx;
			this.rectDy = rectDy;

			remedy();
		}
		// REMEDY means `set right an undesirable situation`

		/**
		 * @apiNote
		 * @implNote Translate virtuellement Rect et Circle dans un repère centré sur le
		 *           centre du rectangle donc les axes sont ceux du rectangle.
		 * @implNote Les coordonnées du centre du rectangle deviennent alors (0,0)
		 * @implNote On translate le centre du cercle
		 * @implNote On déplace par rotation le centre du cercle de -Rect.angle.
		 */

		void remedy() {
			Point rectCenter = new Point(outer.center.x() + rectDx, outer.center.y() + rectDy);
			Vector fromRectToCircle = rectCenter.vectorToward(center);
			Vector local = fromRectToCircle.turned(-outer.angle_degree);
			center = new Point(local.x(), local.y());
		}
		// INTERSECTION in the easy case

		boolean intersects() {
			Point p = closestRectpoint();
			double d = p.vectorToward(center).norm();
			return d <= radius;
		}

		Point closestRectpoint() {
			double x = clamp(center.x(), -outer.halfWidth, outer.halfWidth);
			double y = clamp(center.y(), -outer.halfHeight, outer.halfHeight);
			return new Point(x, y);
		}

		double clamp(double p, double l, double r) {
			if (p < l) {
				return l;
			}
			if (r < p) {
				return r;
			}
			return p;
		}

	}

	// === Rect/Rect Intersection ===

	public boolean intersects(Rect rect) {
		if (intersectsRectAt(this, rect, 0, 0)) {
			return true;
		}
		if (intersectsRectWithVirtualCopies(this, rect)) {
			return true;
		}
		if (intersectsRectWithVirtualCopies(rect, this)) {
			return true;
		}
		// on teste les 2 sens car si b est au bord mais a non
		// sa ne suffit pas de copier juste a car il reste a sa place
		return false;
	}

	private boolean intersectsRectAt(Rect fixed, Rect moved, double dx, double dy) {
		return new RectRectIntersection(fixed, moved, dx, dy).intersects();
	}

	private boolean intersectsRectWithVirtualCopies(Rect fixed, Rect moved) {
		boolean left = rectTouchesLeft(moved);
		boolean right = rectTouchesRight(moved);
		boolean top = rectTouchesTop(moved);
		boolean bottom = rectTouchesBottom(moved);

		if (left && intersectsRectAt(fixed, moved, isu.width_cm(), 0)) {
			return true;
		}

		if (right && intersectsRectAt(fixed, moved, -isu.width_cm(), 0)) {
			return true;
		}

		if (top && intersectsRectAt(fixed, moved, 0, isu.height_cm())) {
			return true;
		}

		if (bottom && intersectsRectAt(fixed, moved, 0, -isu.height_cm())) {
			return true;
		}

		if (left && top && intersectsRectAt(fixed, moved, isu.width_cm(), isu.height_cm())) {
			return true;
		}

		if (left && bottom && intersectsRectAt(fixed, moved, isu.width_cm(), -isu.height_cm())) {
			return true;
		}

		if (right && top && intersectsRectAt(fixed, moved, -isu.width_cm(), isu.height_cm())) {
			return true;
		}

		if (right && bottom && intersectsRectAt(fixed, moved, -isu.width_cm(), -isu.height_cm())) {
			return true;
		}

		return false;
	}

	private boolean rectTouchesRight(Rect rect) {
		Point[] corners = rect.cornersAt(0, 0);
		double maxX = maxX(corners);
		return maxX > isu.width_cm();
	}

	private boolean rectTouchesTop(Rect rect) {
		Point[] corners = rect.cornersAt(0, 0);
		double minY = minY(corners);
		return minY < 0;
	}

	private boolean rectTouchesBottom(Rect rect) {
		Point[] corners = rect.cornersAt(0, 0);
		double maxY = maxY(corners);
		return maxY > isu.height_cm();
	}

	private boolean rectTouchesLeft(Rect rect) {
		Point[] corners = rect.cornersAt(0, 0);
		double minX = minX(corners);
		return minX < 0;
	}

	private double minX(Point[] points) {
		double min = points[0].x();

		for (int i = 1; i < points.length; i++) {
			if (points[i].x() < min) {
				min = points[i].x();
			}
		}

		return min;
	}

	private double minY(Point[] points) {
		double min = points[0].y();

		for (int i = 1; i < points.length; i++) {
			if (points[i].y() < min) {
				min = points[i].y();
			}
		}

		return min;
	}

	private double maxX(Point[] points) {
		double max = points[0].x();

		for (int i = 1; i < points.length; i++) {
			if (points[i].x() > max) {
				max = points[i].x();
			}
		}

		return max;
	}

	private double maxY(Point[] points) {
		double max = points[0].y();

		for (int i = 1; i < points.length; i++) {
			if (points[i].y() > max) {
				max = points[i].y();
			}
		}

		return max;
	}

	Point[] cornersAt(double dx, double dy) {
		double cos = Math.cos(Math.toRadians(angle_degree));
		double sin = Math.sin(Math.toRadians(angle_degree));
		// centre virtuel euclidien du rectangle
		Point center = new Point(this.center.x() + dx, this.center.y() + dy);

		// axe X local mis à l'échelle par halfWidth
		Vector axisX = new Vector(cos * halfWidth, sin * halfWidth);
		// axe Y local mis à l'échelle par halfHeight
		Vector axisY = new Vector(-sin * halfHeight, cos * halfHeight);

		// Vecteurs du centre vers chacun des 4 corners
		Vector topRightV = new Vector(axisX.x() + axisY.x(), axisX.y() + axisY.y());
		Vector topLeftV = new Vector(-axisX.x() + axisY.x(), -axisX.y() + axisY.y());
		Vector bottomLeftV = new Vector(-axisX.x() - axisY.x(), -axisX.y() - axisY.y());
		Vector bottomRightV = new Vector(axisX.x() - axisY.x(), axisX.y() - axisY.y());

		// Les 4 corners du rectangle virtuel
		Point topRight = center.translated(topRightV);
		Point topLeft = center.translated(topLeftV);
		Point bottomLeft = center.translated(bottomLeftV);
		Point bottomRight = center.translated(bottomRightV);

		return new Point[] { topRight, topLeft, bottomLeft, bottomRight };

	}

	@Override
	public Box box() {
		Point[] corners = cornersAt(0, 0);

		double minX = minX(corners);
		double minY = minY(corners);
		double maxX = maxX(corners);
		double maxY = maxY(corners);

		return new Box(minX, minY, maxX, maxY);
	}

	public Box box_360() {
		double radius = Math.sqrt(halfWidth * halfWidth + halfHeight * halfHeight);

		return new Box(center.x() - radius, center.y() - radius, center.x() + radius, center.y() + radius);
	}
}

// === Helping inner class ===

class RectRectIntersection {

	private Rect r1;
	private Rect r2;

	private double r2dx; // déplacement virtuel en x de r2
	private double r2dy; // déplacement virtuel en y de r2

	public RectRectIntersection(Rect r1, Rect r2, double r2dx, double r2dy) {
		this.r1 = r1;
		this.r2 = r2;
		this.r2dx = r2dx;
		this.r2dy = r2dy;
	}

	void remedy() {
		// rien a faire on fait les projection avec vecteurs et ps
	}

	public boolean intersects() {
		// Optimisation : si les deux rects sont axis-aligned → AABB simple
		if (r1.angle_degree == 0 && r2.angle_degree == 0) {
			return intersectsAABB();
		}
		// Cas général : SAT (Separating Axis Theorem)
		double[][] axes = new double[4][2];

		double[] Ar1 = getAxes(r1);
		double[] Ar2 = getAxes(r2);
		axes[0] = new double[] { Ar1[0], Ar1[1] };
		axes[1] = new double[] { Ar1[2], Ar1[3] };
		axes[2] = new double[] { Ar2[0], Ar2[1] };
		axes[3] = new double[] { Ar2[2], Ar2[3] };

		for (int i = 0; i < axes.length; i++) {
			double[] projR1 = project(r1, axes[i], 0, 0);
			double[] projR2 = project(r2, axes[i], r2dx, r2dy);
			if (!overlaps(projR1, projR2))
				return false; // axe séparateur trouvé → pas de collision
		}
		return true; // aucun axe séparateur → collision
	}

	// AABB : test rapide quand angle_degree == 0 pour les deux rects
	private boolean intersectsAABB() {
		double cx1 = r1.center.x();
		double cy1 = r1.center.y();
		double cx2 = r2.center.x() + r2dx;
		double cy2 = r2.center.y() + r2dy;

		return Math.abs(cx1 - cx2) < r1.halfWidth + r2.halfWidth && Math.abs(cy1 - cy2) < r1.halfHeight + r2.halfHeight;
	}

	private boolean overlaps(double[] projR1, double[] projR2) {
		return projR1[1] >= projR2[0] && projR2[1] >= projR1[0];
	}

	private double[] project(Rect r, double[] axis, double dx, double dy) {
		Point[] corners = r.cornersAt(dx, dy);

		double min = dot(axis, corners[0]);
		double max = min;

		for (int i = 1; i < corners.length; i++) {
			double p = dot(axis, corners[i]);
			if (p < min)
				min = p;
			if (p > max)
				max = p;
		}

		return new double[] { min, max };
	}

	private double[] getAxes(Rect r) {
		double cos = Math.cos(Math.toRadians(r.angle_degree));
		double sin = Math.sin(Math.toRadians(r.angle_degree));
		// les 2 axes normaux du rectangle dans le monde
		return new double[] { cos, sin, -sin, cos };
	}

	private double dot(double[] axis, Point p) {
		return axis[0] * p.x() + axis[1] * p.y();
	}
}
