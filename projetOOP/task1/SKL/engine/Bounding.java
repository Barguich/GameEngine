package engine;// = BOUNDING =

import java.util.HashSet;
import java.util.Set;

public class Bounding {

    // FIELDS

    private Set<iShape> boundings;

    // CONSTRUCTOR

    public Bounding() {
        this.boundings = new HashSet<iShape>();
    }

    // BUILDER

    public void add(iShape shape) {
        this.boundings.add(shape);
    }

    // INTERSECTION

    public boolean intersects(iShape shape) {
        for (iShape b : this.boundings) {
            if (b.intersects(shape)) {
                return true;
            }
        }
        return false;
    }

    public boolean intersects(Bounding bounding) {
        for (iShape b : boundings) {
            for (iShape c : bounding.boundings) {
                if (b.intersects(c)) {
                    return true;
                }
            }
        }
        return false;
    }

}
