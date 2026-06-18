package gal.condition;

import model.Entity;

import java.util.List;

import gal.arguments.Category;
import gal.arguments.Direction;
import geometry.Grid;

public class AtStep extends GALCondition {

	private final Direction direction;
	private final Category category;

	private final int nbStep;

	// CONSTRUCTOR
	public AtStep(Direction dir, Category cat, int nbStep) {
		this.direction = dir;
		this.category = cat;
		this.nbStep = nbStep;
	}
	// constructeur sans direction

	public AtStep(int nbStep, Category cat) {
		this(Direction.F, cat, nbStep);
	}

	// EVAL
	/**
	 * @apiNote check if the condition AtStep(...) is satisfied by the given
	 *          entity
	 * @param e = the entity that does the evaluation
	 * @implNote AtStep(...) conditions are intensively used and must be
	 *           efficient: efficiency is perhaps more important than accuracy.
	 * @implNote There is plenty room for optimization here in collaboration
	 *           with the Model and the Bot.
	 */
	public boolean eval(Entity e) {
		// Grid grid = e.grid();

		Grid.Position pos = e.position();
		Grid grid = pos.grid();
		Grid.Position target = pos.copy();
		if (direction != Direction.H) {
			int angle;
			if (direction.isRelative()) {
				angle = (e.orientation() + direction.toAngle() + 360) % 360;
			} else {
				angle = direction.toAngle();
			}
			int dx = 0;
			int dy = 0;
			switch (angle) {
				case 0:
					dx = 1;
					dy = 0;
					break;
				case 90:
					dx = 0;
					dy = -1;
					break;
				case 180:
					dx = -1;
					dy = 0;
					break;
				case 270:
					dx = 0;
					dy = 1;
					break;
				default:
					dx = (int) Math.round(Math.cos(Math.toRadians(angle)));
					dy = -(int) Math.round(Math.sin(Math.toRadians(angle)));
					break;

			}
			target.translate(grid.new Vector(dx * nbStep, dy * nbStep));

		}
		Grid.Cell cell = grid.cellAt(target);
		return checkCategory(e, cell.entities());
	}

	private boolean checkCategory(Entity e, List<Entity> occupants) {
		if (category == Category.V) {
			for (Entity candidate : occupants) {
				if (candidate != e) {
					return false;
				}
			}
			return true;
		}
		if (category == Category.ANY) {
			for (Entity candidate : occupants) {
				if (candidate != e) {
					setSelected(e, candidate);
					return true;
				}
			}
			return false;
		}
		for (Entity candidate : occupants) {
			if (candidate == e) {
				continue;
			}
			if (candidate.category() == category) {
				setSelected(e, candidate);
				return true;
			}
		}
		return false;

	}

	private static void setSelected(Entity e, Entity found) {
		// Stunt stunt = e.stunt();
		// if (e.bot() instanceof GALBot galBot) {
		// galBot.selectedEntity(found);
		// }
	}

	@Override
	public String toString() {
		return "Step(" + direction + ", " + nbStep + ", " + category + ")";
	}

}
