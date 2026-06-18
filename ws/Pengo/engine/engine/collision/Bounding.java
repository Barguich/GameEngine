package engine.collision;

import java.util.HashSet;
import java.util.Set;

public class Bounding {
	// FIELDS
	private final Set<iShape> boundings;

	// CONSTRUCTOR
	public Bounding() {
		this.boundings = new HashSet<iShape>();
	}

	// BUILDER
	public void add(iShape shape) {
		assert shape != null;
		boundings.add(shape);
	}

	// INTERSECTION
	public boolean intersects(iShape shape) {
		assert shape != null;

		for (iShape s : boundings) {
			if (s.intersects(shape)) {
				return true;
			}
		}

		return false;
	}

	public boolean intersects(Bounding other) {

		if (other == null) {
			return false;
		}

		// Test rapide avec les Box
		if (this.box() != null && other.box() != null) {
			if (!this.box().overlaps(other.box())) {
				return false;
			}
		}

		// Test précis forme contre forme
		for (iShape s1 : this.boundings) {
			for (iShape s2 : other.boundings) {
				if (s1.intersects(s2)) {
					return true;
				}
			}
		}

		return false;
	}

	public Box box() {
		Box result = null;
		for (iShape shape : boundings) {
			Box b = shape.box();
			if (result == null) {
				result = b;
			} else {
				result = Box.union(result, b);
			}
		}
		return result;
	}
}
