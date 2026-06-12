package engine;// = Rect =

public class Rect extends Shape {

    // FIELDS

    private double halfWidth, halfHeight;
    private int angle_degree;

    // CONSTRUCTOR

    public Rect(ISU.Coord center, ISU.Dimension size, int angle_degree) {
        super(center);
        this.angle_degree = angle_degree;
        this.halfHeight = size.y_cm / 2;
        this.halfWidth = size.x_cm / 2;
    }

    // TRANSLATION ?

    // ROTATION

    public void rotate(int angle_degree) {
        this.angle_degree += angle_degree;
    }

    // == INTERSECTION ==
    public boolean intersects(iShape shape) {
        return shape.intersects(this);
    }

    // === Rect/Circle Intersection ===
    public boolean intersects(Circle circle) {
        return new RectCircleIntersection(this, circle).intersects();
    }

    // === Helping inner class ===

    /**
     * @implNote Principe
     * <UL>
     * <LI>translate le centre du cercle vers le repère formé par les axes
     * du
     * rectangle,</LI>
     * <LI>redresse le repère du rectangle en annulant la rotation du
     * rectangle,</LI>
     * <LI>détermine le point <i>P</i> du rectangle le plus proche du
     * centre <i>C</i> du cercle
     * de façon efficace car le rectangle est aligné sur les axes
     * X,Y.</LI>
     * </UL>
     * @implNote Il y a intersection si distance(P,C) < rayon du cercle</LI>
     */

    public class RectCircleIntersection {

        // FIELDS

        private Rect outer;
        private Circle circle;
        private ISU isu;

        // CONSTRUCTOR

        public RectCircleIntersection(Rect outer, Circle circle) {
            this.outer = outer;
            this.circle = circle;
            this.isu = outer.center.isu();
            remedy();
        }

        // REMEDY means `set right an undesirable situation`

        /**
         * @apiNote
         * @implNote Translate virtuellement Rect et Circle dans un repère centré sur le
         * centre du rectangle donc les axes sont ceux du rectangle.
         * @implNote Les coordonnées du centre du rectangle deviennent alors (0,0)
         * @implNote On translate le centre du cercle
         * @implNote On déplace par rotation le centre du cercle de -Rect.angle.
         */
        public void remedy() {
            ISU.Coord newCenter = circle.center.mkCopy();
            newCenter.translate(newCenter.isu().new Vector(-outer.center.x(), -outer.center.y()));
            newCenter.rotation(-outer.angle_degree);

            circle = new Circle(newCenter, circle.radius);
        }

        // INTERSECTION in the easy case

        public boolean intersects() {
            ISU.Coord e = closestRectpoint();
            double d = e.distanceTo(circle.center);
            return d <= circle.radius;
        }

        /**
         * @apiNote POINT LE PLUS PROCHE DU CENTRE DU CERCLE
         * @implNote on projette les coins du rectange (c1,c2) et le centre (c) du
         * cercle sur l'axe des <i>x<i>,
         * la coordonnées en x du point le plus proche est parmi {c1.x, c2.x,
         * c.x}
         * @implNote on projette les coins du rectange (c1,c2) et le centre (c) du
         * cercle sur l'axe des <i>y<i>,
         * la coordonnées en x du point le plus proche est parmi {c1.y, c2.y,
         * c.y}
         *
         */

        public ISU.Coord closestRectpoint() {
            double x = clamp(this.circle.center.x(), -this.outer.halfWidth, this.outer.halfWidth);
            double y = clamp(this.circle.center.y(), -this.outer.halfHeight, this.outer.halfHeight);;
            return isu.new Coord(x,y);
        }

        /**
         * @param p = position
         * @param l = borne inférieure de l'interval
         * @param r = borne supérieure de l'interval
         * @return &in; {p, l, r}
         * @implNote la position dans l'interval [l,r] la plus proche de p est :
         * @implNote p si p &in; [l,r]
         * @implNote l si p < l
         * @implNote r si r < p
         */
        public double clamp(double p, double l, double r) {
            if (p < l) {
                return l;
            }
            if (p > r) {
                return r;
            }
            return p;
        }

    }

    // === Rect/Rect Intersection ===
    public boolean intersects(Rect rect) {
        return new RectRectIntersection(this, rect).intersects();
    }

    // === Helping inner class ===

    public class RectRectIntersection {
        private Rect r1;
        private Rect r2;

        public RectRectIntersection(Rect r1, Rect r2) {
            this.r1 = r1;
            this.r2 = r2;
        }

        public boolean intersects() {
            ISU.Vector centerVector = r1.center.mkVectorToward(r2.center);

            return !hasSeparatingAxis(centerVector, localAxis(r1, 0))
                    && !hasSeparatingAxis(centerVector, localAxis(r1, 90))
                    && !hasSeparatingAxis(centerVector, localAxis(r2, 0))
                    && !hasSeparatingAxis(centerVector, localAxis(r2, 90));
        }

        private ISU.Vector localAxis(Rect rect, int offsetDegree) {
            ISU.Vector axis = isu.new Vector(1, 0);
            axis.turn(rect.angle_degree + offsetDegree);
            return axis;
        }

        private boolean hasSeparatingAxis(ISU.Vector centerVector, ISU.Vector axis) {
            double projectedDistance = Math.abs(centerVector.dot(axis));
            double totalReach = projectedHalfSize(r1, axis) + projectedHalfSize(r2, axis);
            return projectedDistance > totalReach;
        }

        private double projectedHalfSize(Rect rect, ISU.Vector axis) {
            ISU.Vector localX = isu.new Vector(1, 0);
            localX.turn(rect.angle_degree);
            ISU.Vector localY = isu.new Vector(0, 1);
            localY.turn(rect.angle_degree);
            return rect.halfWidth * Math.abs(axis.dot(localX)) + rect.halfHeight * Math.abs(axis.dot(localY));
        }

    }
}
