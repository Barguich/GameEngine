package collision;

public interface iShape {

	// Test d'intersection avec une forme quelconque
	boolean intersects(iShape shape);

	// Test d'intersection avec un cercle
	boolean intersects(Circle circle);

	// Test d'intersection avec un rectangle
	boolean intersects(Rect rect);

	// Retourne la boîte englobante de la forme
	Box box();
}