package entities;

import engine.Entity;
import engine.Grid;
import engine.ISU.Dimension;
import intersection.Bounding;
import intersection.Circle;
import intersection.Rect;
import view.GhostAvatar;

public class Ghost extends Entity {

	// CONSTRUCTOR

	public Ghost(Grid.Position position) {
		super("Ghost");
		setPosition(position);
		double cell = this.step.x();
		setSize(isu.new Dimension(cell/2,cell/2));
		setBounding();
		setAvatar(new GhostAvatar(this));
	}

	// === Task COLLISION ===

	@Override
	public void setBounding() {

		bounding = new Bounding();

		double radius = size.x() / 4.0;

		// tête
		Circle head = new Circle(isu.new Coord(center.x(), center.y() - radius), radius);

		// corps
		Rect body = new Rect(center.mkCopy(), size, orientation_degree);

		bounding.add(head);
		bounding.add(body);
	}
}