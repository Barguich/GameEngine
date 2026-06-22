package collision;

import java.util.HashSet;
import java.util.Set;

public class Bounding {

    // Ensemble des formes composant la zone de collision
    private final Set<iShape> boundings;

    public Bounding() {
        this.boundings = new HashSet<iShape>();
    }

    // Ajoute une forme à la zone de collision
    public void add(iShape shape) {
        if (shape != null) {
            boundings.add(shape);
        }
    }

    public boolean isEmpty() {
        return boundings.isEmpty();
    }

    // Calcule la boîte englobante de toutes les formes
    public Box box() {
        Box result = null;

        for (iShape shape : boundings) {
            Box b = shape.box();

            if (result == null) {
                result = b;
            } else {
                result = Box.union(result, b);
            }
        }

        return result;
    }

    // Vérifie si une forme intersecte au moins une des formes du Bounding
    public boolean intersects(iShape shape) {

        if (shape == null) {
            return false;
        }

        for (iShape s : boundings) {
            if (s.intersects(shape)) {
                return true;
            }
        }

        return false;
    }

    // Vérifie l'intersection entre deux Bounding
    public boolean intersects(Bounding other) {

        if (other == null) {
            return false;
        }

        Box b1 = this.box();
        Box b2 = other.box();

        // Premier test rapide avec les boîtes englobantes
        if (b1 == null || b2 == null) {
            return false;
        }

        if (!b1.overlaps(b2)) {
            return false;
        }

        // Si les boîtes se chevauchent, on teste les formes réelles
        for (iShape s1 : this.boundings) {
            for (iShape s2 : other.boundings) {
                if (s1.intersects(s2)) {
                    return true;
                }
            }
        }

        return false;
    }
}