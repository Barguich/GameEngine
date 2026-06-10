package entities;

import engine.Entity;
import engine.Grid;
import engine.ISU.Dimension;
import intersection.Bounding;
import intersection.Rect;
import view.ObstacleAvatar;


public class Obstacle extends Entity {

	public Obstacle(Grid.Position position) {
		super("Obstacle");
		setPosition(position);
		double cell = this.step.x();
		setSize(isu.new Dimension(cell,cell));
		setBounding();
		setAvatar(new ObstacleAvatar(this));
	}

	@Override
	public void setBounding() {
		bounding = new Bounding();

		bounding.add(new Rect(center, size, orientation_degree));
	}

}
