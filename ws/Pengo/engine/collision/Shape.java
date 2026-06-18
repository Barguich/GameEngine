package collision;

import geometry.ISU;

public class Shape {
	// FIELD
	public ISU isu;
	public ISU.Coord center;

	// CONSTRUCTOR
	public Shape(ISU.Coord center) {
		this.center = center.mkCopy();
		this.isu = center.isu();
	}

}
