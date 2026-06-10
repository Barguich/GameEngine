package engine.model;


public abstract class Shape implements iShape{

	// FIELD

	protected ISU isu;
	protected ISU.Coord center;

	// CONSTRUCTOR

	public Shape(ISU.Coord center) {
		this.isu = center.getIsu();
		this.center = center;
	}

	 public ISU.Coord getCenter(){
       return center;
    }

}
