package gal.condition;

import gal.arguments.Category;
import gal.arguments.Direction;
import geometry.ISU;
import model.Entity;

public class Closest implements iGALCondition {

	// FIELDS
	private final Category category;
	private final Direction direction; // null = pas de test de cadran

	// CONSTANTS
	private static final double COS_45 = Math.sqrt(2) / 2.0;

	// CONSTRUCTORS
	public Closest(Category category) {
		this(category, null);
	}

	public Closest(Category category, Direction direction) {
		if (category == null)
			throw new IllegalArgumentException();
		this.category = category;
		this.direction = direction;
	}

	// EVAL
	@Override
	public boolean eval(Entity e) {
		if (e == null || e.center() == null || e.model() == null)
			return false;

		Entity target = findClosest(e);
		if (target == null)
			return false;

		if (direction != null && !cone(e, target))
			return false;

		return true;
	}

	// === RECHERCHE ===
	private Entity findClosest(Entity self) {
		Entity best = null;
		double bestDist = Double.MAX_VALUE;
		for (Entity o : self.model().entities()) {
			if (o == self)
				continue;
			if (o.category() != category)
				continue;
			if (o.center() == null)
				continue;
			double d = self.center().distanceTo(o.center());
			if (d < bestDist) {
				bestDist = d;
				best = o;
			}
		}
		return best;
	}

	// Renvoie vrai si il y a une entité dans la direction qu'on regarde +/- 45 deg
	private boolean cone(Entity self, Entity target) {
		ISU.Vector v = self.center().mkVectorToward(target.center());
		double vNorm = v.norm();
		if (vNorm == 0)
			return true;

		ISU.Vector d = directionVector(self.orientation(), self.center().isu());
		double cosAngle = v.dot(d) / vNorm;
		return cosAngle >= COS_45;
	}

	// Vecteur unitaire dans la direction
	private ISU.Vector directionVector(int selfOrientation, ISU isu) {
		int angle_deg;
		if (direction.isAbsolute()) {
			angle_deg = direction.toAngle();
		} else if (direction == Direction.H) {
			angle_deg = selfOrientation;
		} else {
			angle_deg = selfOrientation + direction.toAngle();
		}
		double rad = Math.toRadians(angle_deg);
		return isu.new Vector(Math.cos(rad), -Math.sin(rad));
	}
}
