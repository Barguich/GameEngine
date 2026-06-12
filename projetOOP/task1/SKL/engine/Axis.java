package engine;

/**
 * @apiNote engine.Axis of a Torus with origin at 0
 * @implNote Coordinate ranges in [ -perimeter/2 ; perimeter/2 [
 * @implNote Negative coordinate are allowed
 */

public class Axis {

    // FIELDS

    private boolean onTorus;
    private double perimeter;
    private double halfPerimeter;

    // CONSTRUCTOR

    public Axis(boolean onTorus, double perimeter) {
        this.onTorus = onTorus;
        this.perimeter = perimeter;
        this.halfPerimeter = perimeter / 2;
    }

    // NORMALIZE INTEGER LENGTH

    /**
     * @return <UL>
     * <LI>length % perimeter __&in; [0, perimeter-1]__ if onTorus</LI>
     * <LI>length if !onTorus</LI>
     * </UL>
     * @apiNote normalize _integer length_ according to the geometry
     * @implNote returns positive values
     */
    public int normalize(int length) {
        if(!onTorus) {
            return length;
        }
        return modp(length, (int) this.perimeter);
    }

    /**
     * @return length % perimeter __&in; [0, perimeter-1]__
     * @apiNote compute length modulo perimeter
     */
    public int modp(int length, int perimeter) {
        int n = length % perimeter;
        return n < 0 ? n+perimeter : n;
    }

    // NORMALIZE REAL LENGTH

    /**
     * @return <UL>
     * <LI>length module perimeter <I>&in; [-perimeter/2 , perimeter/2[</I>
     * if onTorus</LI>
     * <LI>length if !onTorus</LI>
     * </UL>
     * @apiNote normalize _real length_ according to the geometry
     * @implNote can return negative values
     */
    public double normalize(double length) {
        if(!onTorus) {
            return length;
        }
        return modp(length, this.perimeter);
    }

    /**
     * @return length % perimeter __&in; [0, perimeter[__
     * @apiNote compute length modulo perimeter
     */
    public double modp(double length, double perimeter) {
        double n = length % perimeter;
        return n < 0 ? n+perimeter : n;
    }

    // DISTANCE

    /**
     * @apiNote The distance on a Torus is that of the shortest path, sometimes
     * going in the opposite direction and across the border is shorter.
     * @implNote Look for the detail on internet.
     */
    public double distance(double position1, double position2) {
        double d = position2 - position1;
        if(!onTorus) {
            return Math.abs(d);
        }
        d = normalize(d);
        if(d > halfPerimeter) {
            d-= perimeter;
        }
        return Math.abs(d);
    }
}
