package entities;

import engine.Entity;
import engine.Grid;
import engine.ISU;
import engine.ISU.Dimension;
import intersection.Bounding;
import intersection.Rect;
import view.BossAvatar;

public class Boss extends Entity {

	// CONSTRUCTOR

	public Boss(Grid.Position position) {
		super("Boss");
		setPosition(position);
		double cell = this.step.x();
		setSize(isu.new Dimension(3.0 * cell, 4.0 * cell));
		setBounding();
		setAvatar(new BossAvatar(this));
	}

	// === Task COLLISION ===

	@Override
	public void setBounding() {

		bounding = new Bounding();

		double cell = step.x();

		// rectangle vertical : centre, 1 au-dessus, 2 en-dessous
		ISU.Dimension verticalSize = isu.new Dimension(cell, 4.0 * cell);

		ISU.Coord verticalCenter = center.mkCopy();

		Rect vertical = new Rect(verticalCenter, verticalSize, orientation_degree);

		// rectangle horizontal : cellule du centre + 2 cellules à droite
		ISU.Dimension horizontalSize = isu.new Dimension(3.0 * cell, cell);

		ISU.Coord horizontalCenter = isu.new Coord(center.x() + cell, center.y());

		Rect horizontal = new Rect(horizontalCenter, horizontalSize, orientation_degree);

		bounding.add(vertical);
		bounding.add(horizontal);
	}
}