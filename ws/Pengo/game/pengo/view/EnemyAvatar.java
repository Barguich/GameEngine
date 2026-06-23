package pengo.view;

import model.Entity;
import oop.graphics.Graphics;
import view.Avatar;

public class EnemyAvatar extends Avatar {

	public EnemyAvatar(Entity entity) {
		super(entity);
	}

	@Override
	public void paint(Graphics g, int xPix, int yPix, double scale) {
		int side = (int) (entity.size().x() * scale);

		g.setColor(Graphics.Colors.yellow);
		g.fillOval(xPix - side / 2, yPix - side / 2, side, side);

		// Bec bleu, dessiné sans changer la hitbox
		g.setColor(Graphics.Colors.cyan);
		g.fillRect(xPix - side / 2, yPix, side / 5, side / 4);
	}
}