package entities;

import engine.Entity;
import engine.Grid;
import engine.ISU.Dimension;
import intersection.Bounding;
import intersection.Circle;
import view.GumAvatar;

public class Gum extends Entity {

	public Gum(Grid.Position position) {
		super("Gum");
		setPosition(position);
		double cell = this.step.x();
		setSize(isu.new Dimension(cell / 4, cell / 4));
		setBounding();
		setAvatar(new GumAvatar(this));
	}

	@Override
	public void setBounding() {
		bounding = new Bounding();

		double radius = size.x() / 2.0;

		bounding.add(new Circle(center, radius));
	}
}