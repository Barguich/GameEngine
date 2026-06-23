package pengo.view;

import oop.graphics.Color;
import oop.graphics.Graphics;
import model.Entity;
import pengo.model.IceBlock;
import view.ShapeAvatar;

/**
 * Variante de {@link ShapeAvatar} propre à Pengo : la couleur de la forme
 * dépend des points de vie restants du bloc de glace. Cette logique de jeu
 * vit côté game, le moteur ne connaissant pas le type {@link IceBlock}.
 */
public class IceShapeAvatar extends ShapeAvatar {

	public IceShapeAvatar(Entity entity, Shape shape, int a, int r, int g, int b) {
		super(entity, shape, a, r, g, b);
	}

	@Override
	protected Color resolveBaseColor(Graphics g) {
		if (entity instanceof IceBlock ice) {
			if (ice.hp() == 3) {
				return g.getColor(255, 120, 180, 255);
			} else if (ice.hp() == 2) {
				return g.getColor(255, 100, 140, 220);
			} else if (ice.hp() == 1) {
				return g.getColor(255, 180, 80, 80);
			}
		}

		return super.resolveBaseColor(g);
	}
}
