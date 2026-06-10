package engine;

import engine.ISU;

public class Shape {
	// FIELD

	ISU isu;
	ISU.Coord center;

	// CONSTRUCTOR

	public Shape(ISU.Coord center) {
		this.center = center.mkCopy();
		this.isu = center.isu();
	}

}
