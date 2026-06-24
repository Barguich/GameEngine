package collision;

import geometry.ISU;

/**
 * Source unique de vérité pour le rétrécissement des hitbox rectangulaires.
 * Les entités occupant une case entière (Wall, IceBlock, Enemy) doivent
 * utiliser une box légèrement plus petite que la case pour que deux cases
 * ADJACENTES ne soient pas considérées en collision sur leur arête commune.
 */
public final class Hitbox {

    /** Fraction de la taille conservée (0.90 => 5% de marge de chaque côté). */
    public static final double INSET = 0.90;

    private Hitbox() {}

    public static Rect shrunkRect(ISU.Coord center, ISU.Dimension size, int angle_degree) {
        ISU.Dimension inset = center.isu().new Dimension(size.x() * INSET, size.y() * INSET);
        return new Rect(center, inset, angle_degree);
    }
}