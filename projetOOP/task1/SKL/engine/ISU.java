package engine;

import java.io.PrintStream;

public class ISU {

    // FIELDS

    private Axis xAxis, yAxis;
    private ISU isu;
    private Grid grid;
    private Game game;
    // CONSTRUCTOR

    public ISU(Game game) {
        this.game = game;
        this.isu = this;
        this.xAxis = new Axis(game.torusOnXaxis, game.width_cm);
        this.yAxis = new Axis(game.torusOnYaxis, game.height_cm);
    }

    // SETTER

    public void set(Grid grid) {
        this.grid = grid;
    }

    // == DIMENSION (cm) ==

    public class Dimension {
        protected double x_cm, y_cm;

        // CONSTRUCTOR

        public Dimension(double x_cm, double y_cm) {
            setxy(x_cm, y_cm);
        }

        // GEOMETRY

        public void normalize() {
            this.x_cm = xAxis.normalize(x_cm);
            this.y_cm = yAxis.normalize(y_cm);
        }

        // SETTER

        public void setxy(double x_cm, double y_cm) {
            this.x_cm = x_cm;
            this.y_cm = y_cm;
            normalize();
        }

        // GETTER

        public ISU isu() {
            return isu;
        }

        // EQUALS / EQUIV
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ISU.Dimension)) {
                return false;
            }
            ISU.Dimension d = (ISU.Dimension) o;
            return x_cm == d.x_cm && y_cm == d.y_cm;
        }

        public boolean equiv(Dimension d) {
            return equals(d);
        }

        // GETTER

        public double x() {
            return this.x_cm;
        }

        public double y() {
            return this.y_cm;
        }

        // FACTORY

        public ISU.Vector mkScaledVector(double factor) {
            return new Vector(x_cm * factor, y_cm * factor);
        }

        public ISU.Vector mkScaledVector(double xFactor, double yFactor) {
            return new Vector(x_cm * xFactor, y_cm * yFactor);
        }

        public ISU.Vector mkVector() {
            return new Vector(x_cm, y_cm);
        }

        // SHOW

        public void show(PrintStream ps) {
            ps.println("(" + x_cm + " cm , " + y_cm + " cm)");
        }

    }

    // == POINT ==

    public class Coord extends ISU.Dimension {

        // CONSTRUCTOR

        public Coord(double x_cm, double y_cm) {
            super(x_cm, y_cm);
        }

        // SHOW
        @Override
        public void show(PrintStream ps) {
        }

        // EQUALS
//        @Override
//        public boolean equals(Object o) {
//            return false;
//        }

        // FACTORY

        public ISU.Vector mkVectorToward(Coord target) {
            return new Vector(target.x_cm - x_cm, target.y_cm - y_cm);
        }

        // CONVERSION

        public Grid.Position toGridPosition() {
            int x_ncell = (int) (x_cm / game.cmPerCell);
            int y_ncell = (int) Math.round(y_cm / game.cmPerCell);
            return grid.new Position(x_ncell, y_ncell);
        }

        // TRANSLATION

        public void translate(ISU.Vector v) {
            this.x_cm += v.x_cm;
            this.y_cm += v.y_cm;
            normalize();
        }

        public ISU.Coord mkTranslated(ISU.Vector v) {
            return new Coord(this.x_cm + v.x_cm, this.y_cm + v.y_cm);
        }

        // COPY

        public ISU.Coord mkCopy() {
            return new Coord(this.x_cm, this.y_cm);
        }

        // ROTATION

        /**
         * @param angle_degree
         * @apiNote rotation around the origin (0,0)
         */
        public void rotation(int angle_degree) {
            double angle_rad = Math.toRadians(angle_degree);
            double nx = x_cm * Math.cos(angle_rad) - y_cm * Math.sin(angle_rad);
            double ny = x_cm * Math.sin(angle_rad) + y_cm * Math.cos(angle_rad);
            x_cm = nx;
            y_cm = ny;
            normalize();
        }

        /**
         * @param center
         * @param angle_degree
         * @apiNote rotation around the given center
         */
        public void rotateAround(Coord center, int angle_degree) {
            x_cm -= center.x_cm;
            y_cm -= center.y_cm;
            rotation(angle_degree);
            x_cm += center.x_cm;
            y_cm += center.y_cm;
            normalize();
        }

        // DISTANCE

        public double distanceTo(Coord pt) {
            double dx = xAxis.distance(this.x_cm, pt.x_cm);
            double dy = yAxis.distance(this.y_cm, pt.y_cm);
            return Math.sqrt(dx * dx + dy * dy);
        }

    }

    // == VECTOR ==

    /**
     * @apiNote The Vector class defines canonical vectors with origin in (0,0)
     * poiting at a target coordinate.
     * @apiNote Canonocal vectors are defined by their target Coord.
     */
    public class Vector extends Coord {

        // CONSTRUCTOR

        public Vector(double targetX_cm, double targetY_cm) {
            super(targetX_cm, targetY_cm);
        }

        // OPERATOR

        public void add(Vector v) {
            this.x_cm += v.x_cm;
            this.y_cm += v.y_cm;
        }

        public void scale(double factor) {
            this.x_cm *= factor;
            this.y_cm *= factor;
        }

        public void scale(double xFactor, double yFactor) {
            this.x_cm *= xFactor;
            this.y_cm *= yFactor;
        }

        /**
         * @param v
         * @return le produit scalaire de `this` et du vecteur v
         * @apiNote produit scalaire
         */
        public double dot(ISU.Vector v) {
            return this.x_cm * v.x_cm + this.y_cm * v.y_cm;
        }

        public double norm() {
            return Math.sqrt(x_cm * x_cm + y_cm * y_cm);
        }

        /**
         * @apiNote rend le vecteur unitaire, ie. de norme = 1
         */
        public void unity() {
            double n = norm();
            x_cm /= n;
            y_cm /= n;
        }

        // TURN

        /**
         * @param angle_degree
         * @apiNote turn the vector itself
         * @implNote the center of the rotation is the origin of the vector
         */
        public void turn(int angle_degree) {
            double angle_rad = Math.toRadians(angle_degree);
            double nx = x_cm * Math.cos(angle_rad) - y_cm * Math.sin(angle_rad);
            double ny = x_cm * Math.sin(angle_rad) + y_cm * Math.cos(angle_rad);
            x_cm = nx;
            y_cm = ny;
        }

        public ISU.Vector mkCopy() {
            return isu.new Vector(x_cm, y_cm);
        }

    }
}
