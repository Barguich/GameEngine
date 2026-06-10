package engine;// = engine.ISU =

import java.io.PrintStream;

public class ISU {

	// FIELDS

	 protected static Axis xAxis;
    protected static Axis yAxis;
	 private static Grid grid;

	// CONSTRUCTOR

	 public ISU(Game game) {
		 assert game!=null;
		 this.xAxis=new Axis(game.torusOnXaxis, game.width_cm );
		 this.yAxis=new Axis(game.torusOnYaxis, game.height_cm );


	 }



	// SETTER

	 public void set(Grid grid) {
		 this.grid=grid;
		 assert grid!=null;

	  }

	// == DIMENSION (cm) ==

	public class Dimension {
		private double x_cm, y_cm;
		// CONSTRUCTOR

		public Dimension(double x_cm, double y_cm) {
			this.x_cm=xAxis.normalize(x_cm);
			this.y_cm=yAxis.normalize(y_cm);

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


		}

		// GETTER

		public ISU isu() {
			return ISU.this;
		}

		// EQUALS / EQUIV
		public boolean equals(Object o) {
			if (!(o instanceof Dimension d))
				return false;
			return x_cm == d.x_cm && y_cm == d.y_cm;
		}

		public boolean equiv(Dimension d) {
			return d != null && xAxis.normalize(x_cm) == xAxis.normalize(d.x_cm) && yAxis.normalize(y_cm) == yAxis.normalize(d.y_cm);
		}


		// GETTER

		public double x() {
			return x_cm;
		}

		public double y() {
			return y_cm;
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
			ps.printf("ISU.Dimension(%.3f cm, %.3f cm)%n", x_cm, y_cm);

		}
	}

	// == POINT ==

	 public class Coord  {
		private double x_cm;
		private double y_cm;
		// CONSTRUCTOR

		 public Coord(double x_cm, double y_cm) {
			this.x_cm=xAxis.normalize(x_cm);
			this.y_cm=yAxis.normalize(y_cm);

		 }
		// SHOW

		public void show(PrintStream ps) {
			ps.printf("ISU.Coord(%.2f cm, %.2f cm)%n",x_cm,y_cm);
		 }

		// EQUALS
		public boolean equals(Object o) {
			if(!(o instanceof Coord c))
				return false;
			return x_cm==c.x_cm && y_cm==c.y_cm;
		}

		// FACTORY

		public ISU.Vector mkVectorToward(Coord target) {
			 assert target!=null;
			 double dx=target.x_cm-this.x_cm;
			 double dy= target.y_cm-this.y_cm;
			 if(xAxis.onTorus){
				 if(Math.abs(dx)> xAxis.perimeter/2)
					 dx -=Math.signum(dx)* xAxis.perimeter;
			 }
			 if(yAxis.onTorus){
				 if(Math.abs(dy)> yAxis.perimeter/2)
					 dy-=Math.signum(dy)* yAxis.perimeter;
			 }
			 return new Vector(dx,dy);
		}

		// CONVERSION

		 public Grid.Position toGridPosition() {
			 assert grid!=null;
			int x_ncell=(int) Math.round(x_cm/grid.cmPerCell);
			 int y_ncell=(int) Math.round(y_cm/grid.cmPerCell);

			 return grid.new Position(x_ncell, y_ncell);
		 }

		// TRANSLATION

		 public void translate(ISU.Vector v) {
			assert v!=null;
			x_cm=xAxis.normalize(x_cm+v.x_cm);
			y_cm=yAxis.normalize(y_cm+v.y_cm);
		 }

		public ISU.Coord mkTranslated(ISU.Vector v) {
			 assert v!=null;
			 return new Coord(x_cm+v.x_cm, y_cm+v.y_cm);
			  }

		// COPY

		public ISU.Coord mkCopy() {
			 return new Coord(x_cm,y_cm);
		 }

		// ROTATION

		/**
		 * @apiNote rotation around the origin (0,0)
		 * @param angle_degree
		 */
		 public void rotation(int angle_degree) {
			double rad=Math.toRadians(angle_degree);
			double newX=x_cm*Math.cos(rad)-y_cm*Math.sin(rad);
			double newY=x_cm*Math.sin(rad)+y_cm*Math.cos(rad);
			x_cm=xAxis.normalize(newX);
			y_cm=yAxis.normalize(newY);
		 }

		/**
		 * @apiNote rotation around the given center
		 * @param center
		 * @param angle_degree
		 */
		 public void rotateAround(Coord center, int angle_degree) {
			assert center!=null;
			x_cm-= center.x_cm;
			y_cm-=center.y_cm;
			rotation(angle_degree);
			x_cm=xAxis.normalize(x_cm+center.x_cm);
			y_cm=yAxis.normalize(y_cm+center.y_cm);
		 }

		// DISTANCE

		public double distanceTo(Coord pt) {
			 assert pt!=null;
			 double dx=xAxis.distance(x_cm,pt.x_cm);
			 double dy=yAxis.distance(y_cm,pt.y_cm);
			 return Math.sqrt(dx*dx+dy*dy);
		}
		public double x(){
			 return x_cm;
		}
		public double y(){
			 return y_cm;
		}


	}

	// == VECTOR ==

	/**
	 * @apiNote The Vector class defines canonical vectors with origin in (0,0)
	 *          poiting at a target coordinate.
	 * @apiNote Canonocal vectors are defined by their target Coord.
	 */
	 public class Vector  {
		private double x_cm,y_cm;
		// CONSTRUCTOR

		 public Vector(double targetX_cm, double targetY_cm) {
			 this.x_cm=targetX_cm;
			 this.y_cm=targetY_cm;
		 }
		// OPERATOR

		public void add(Vector v) {
			 assert v!=null;
			 x_cm+=v.x_cm;
			 y_cm+=v.y_cm;
		}
		public void scale(double factor) {
			 x_cm*=factor;
			 y_cm*=factor;
		}
		 public void scale(double xFactor, double yFactor) {
			 x_cm*=xFactor;
			 y_cm*=yFactor;
		 }
		/**
		 * @apiNote produit scalaire
		 * @param v
		 * @return le produit scalaire de `this` et du vecteur v
		 */
		 public double dot(ISU.Vector v) {
			 assert v!=null;
			 return x_cm*v.x_cm+y_cm*v.y_cm;
		 }

		 public double norm() {
			 return Math.sqrt(x_cm*x_cm+y_cm*y_cm);
		 }

		/**
		 * @apiNote rend le vecteur unitaire, ie. de norme = 1
		 */
		 public void unity() {
			 double n=norm();
			if(n==0) return;
			 x_cm/=n;
			 y_cm/=n;
		 }
		// TURN

		/**
		 * @apiNote turn the vector itself
		 * @implNote the center of the rotation is the origin of the vector
		 * @param angle_degree
		 */
		public void turn(int angle_degree) {
			double rad=Math.toRadians(angle_degree);
			double newX=x_cm*Math.cos(rad)-y_cm*Math.sin(rad);
			double newY=x_cm*Math.sin(rad)+y_cm*Math.cos(rad);
			x_cm=newX;
			y_cm=newY;
		}


		public double x(){
			return x_cm;
		}
		public double y(){
			return y_cm;
		}
	}

}
