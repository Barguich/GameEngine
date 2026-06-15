package engine;// = AXIS =


/**
 * @apiNote engine.Axis of a Torus with origin at 0
 * @implNote Coordinate ranges in [ -perimeter/2 ; perimeter/2 [
 * @implNote Negative coordinate are allowed
 */

 public class Axis {

	// FIELDS

	 public final boolean onTorus;
	 public final double perimeter;


	// CONSTRUCTOR

	 public Axis(boolean onTorus, double perimeter) {
		 assert(perimeter>0);
		 this.onTorus=onTorus;
		 this.perimeter=perimeter;
		  }

	// NORMALIZE INTEGER LENGTH

	/**
	 * @apiNote normalize _integer length_ according to the geometry
	 * @implNote returns positive values
	 * @return
	 *         <UL>
	 *         <LI>length % perimeter __&in; [0, perimeter-1]__ if onTorus</LI>
	 *         <LI>length if !onTorus</LI>
	 *         </UL>
	 */
	 public int normalize(int length) {
		 if(!onTorus){
			 return length;
		 }else {
			 return modp(length,(int)perimeter);
		 }

	 }

	/**
	 * @apiNote compute length modulo perimeter
	 * @return length % perimeter __&in; [0, perimeter-1]__
	 */
	 private int modp(int length, int perimeter) {
		 int r=length%(int)perimeter;
		 if(r<0){
			 r+=perimeter;
		 }
		 assert(r>=0);
		 assert(r<perimeter);
		 return r;

	 }

	// NORMALIZE REAL LENGTH

	/**
	 * @apiNote normalize _real length_ according to the geometry
	 * @implNote can return negative values
	 * @return
	 *         <UL>
	 *         <LI>length module perimeter <I>&in; [-perimeter/2 , perimeter/2[</I>
	 *         if onTorus</LI>
	 *         <LI>length if !onTorus</LI>
	 *         </UL>
	 */
	 public double normalize(double length) {
		 if(!onTorus){
			 return length;
		 }
		return modp(length,perimeter);
	 }

	/**
	 * @apiNote compute length modulo perimeter
	 * @return length % perimeter __&in; [0, perimeter[__
	 */
	 private double modp(double length, double perimeter) {
		 double r=length% perimeter;
		 if(r<0){
			 r+=perimeter;
		 }
		 assert(r>=0);
		 assert(r<perimeter);
		 return r;
	 }

	// DISTANCE

	/**
	 * @apiNote The distance on a Torus is that of the shortest path, sometimes
	 *          going in the opposite direction and across the border is shorter.
	 * @implNote Look for the detail on internet.
	 */
	 public double distance(double position1, double position2) {

		 if(!onTorus) {
			 return Math.abs(position2 - position1);
		 }
		 double d= modp(Math.abs(position2-position1),perimeter);
		 return Math.min(d,perimeter-d);
	 }
	 public double euclidean(double xmin,double xmax){
		 if(xmin>xmax){
			 xmax+=perimeter;
		 }
		 return xmax;
	 }
}
