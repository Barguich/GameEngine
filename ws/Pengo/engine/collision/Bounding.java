package collision;

import java.util.HashSet;
import java.util.Set;

public class Bounding {

    private final Set<iShape> boundings;

    public Bounding() {
        this.boundings = new HashSet<iShape>();
    }

    public void add(iShape shape) {
        assert shape != null;
        boundings.add(shape);
    }

    public boolean isEmpty() {
        return boundings.isEmpty();
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

        Box b1 = this.box();
        Box b2 = other.box();

        if (b1 == null || b2 == null) {
            return false;
        }

        if (!b1.overlaps(b2)) {
            return false;
        }

        for (iShape s1 : this.boundings) {
            for (iShape s2 : other.boundings) {
                if (s1.intersects(s2)) {
                    return true;
                }
            }
        }

        return false;
    }
}