package collision;

import geometry.ISU;

public class Circle extends Shape implements iShape {

	// Rayon du cercle
	double radius;

	public Circle(ISU.Coord center, double radius) {
		super(center);
		this.radius = radius;
	}

	// La détection avec un rectangle est déléguée à Rect
	public boolean intersects(Rect rect) {
		return rect.intersects(this);
	}

	// Deux cercles sont en collision si la distance entre leurs centres
	// est inférieure à la somme de leurs rayons
	public boolean intersects(Circle circle) {
		double d = circle.center.distanceTo(this.center);
		return d < circle.radius + this.radius;
	}
 
	// Permet le double dispatch pour choisir le bon test d'intersection
	public boolean intersects(iShape shape) {
		return shape.intersects(this);
	}

	// Boîte englobante du cercle
	@Override
	public Box box() {
		double cx = center.x();
		double cy = center.y();

		return new Box(
				cx - radius,
				cy - radius,
				cx + radius,
				cy + radius);
	}
}