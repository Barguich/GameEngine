package collision;

import geometry.ISU;

public final class Hitbox {

    // On réduit légèrement les rectangles de collision.
    // Sans ça, deux entités placées dans deux cases voisines peuvent être
    // détectées comme en collision juste parce que leurs bords se touchent.
    public static final double INSET = 0.90;

    // Classe utilitaire : on ne veut pas créer d'objet Hitbox.
    private Hitbox() {}

    public static Rect shrunkRect(ISU.Coord center, ISU.Dimension size, int angle_degree) {
        // On garde le même centre que l'entité, mais on utilise une dimension
        // un peu plus petite que la taille visuelle de la case.
        ISU.Dimension inset =
                center.isu().new Dimension(size.x() * INSET, size.y() * INSET);

        return new Rect(center, inset, angle_degree);
    }
}