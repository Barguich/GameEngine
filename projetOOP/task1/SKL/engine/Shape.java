package engine;

public abstract class Shape implements iShape {

    // FIELD

    protected ISU isu;
    protected ISU.Coord center;

    // CONSTRUCTOR

    public Shape(ISU.Coord center) {
        this.center = center;
        this.isu = center.isu();
    }
}
