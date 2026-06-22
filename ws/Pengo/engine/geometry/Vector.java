package geometry;

/*
 * Représente un vecteur du plan.
 * Cette classe est utilisée pour les calculs géométriques
 * comme les déplacements, rotations et collisions.
 */
public class Vector {

    private double x, y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    // Additionne un vecteur au vecteur courant
    public void add(Vector v) {
        x += v.x();
        y += v.y();
    }

    // Retourne une copie du vecteur mise à l'échelle
    public Vector scaled(double factor) {
        return new Vector(x * factor, y * factor);
    }

    // Produit scalaire entre deux vecteurs
    public double dot(Vector v) {
        return x * v.x() + y * v.y();
    }

    // Norme (longueur) du vecteur
    public double norm() {
        return Math.sqrt(x * x + y * y);
    }

    // Transforme le vecteur en vecteur unitaire
    public void unity() {
        double n = norm();
        x = x / n;
        y = y / n;
    }

    // Retourne une copie du vecteur après rotation
    public Vector turned(double angle_degree) {
        double angle = Math.toRadians(angle_degree);

        double newX = x * Math.cos(angle) - y * Math.sin(angle);
        double newY = x * Math.sin(angle) + y * Math.cos(angle);

        return new Vector(newX, newY);
    }
}