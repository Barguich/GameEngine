package engine.collision;

import java.util.HashSet;
import java.util.Set;

public class Bounding {

	// FIELDS

	Set<iShape> boundings;

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

	public boolean intersects(Bounding bounding) {
		assert bounding != null;

		for (iShape s1 : this.boundings) {
			for (iShape s2 : bounding.boundings) {
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
