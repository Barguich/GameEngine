package engine;
// = Circle =

public class Circle extends Shape {

    protected double radius;
    // CONSTRUCTOR

    public Circle(ISU.Coord center, double radius) {
        super(center);
        this.radius = radius;
    }

    // INTERSECTION
    public boolean intersects(Rect rect) {
        return rect.intersects(this);
    }

    public boolean intersects(Circle circle) {
        double d = this.center.distanceTo(circle.center);
        return d <= this.radius + circle.radius;
    }

    public boolean intersects(iShape shape) {
        return shape.intersects(this);
    }
}
