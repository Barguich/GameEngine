package geometry;

import java.util.logging.Level;

public class Axis {
	// FIELDS
	final boolean onTorus;
	final double perimeter;
	private final double halfPerimeter;

	// CONSTRUCTOR
	public Axis(boolean onTorus, double perimeter) {
		assert (perimeter > 0);
		this.onTorus = onTorus;
		this.perimeter = perimeter;
		this.halfPerimeter = perimeter / 2.0;
	}

	// NORMALIZE INTEGER LENGTH
	/**
	 * @return
	 *         <UL>
	 *         <LI>length % perimeter &in; [0, perimeter-1] if onTorus</LI>
	 *         <LI>length if !onTorus</LI>
	 *         </UL>
	 * @apiNote normalize _integer length_ according to the geometry
	 * @implNote returns positive values
	 */
	public int normalize(int length) {
		if (!onTorus) {
			return length;
		} else {
			return modp(length, (int) this.perimeter);
		}
	}

	/**
	 * @return length % perimeter &in; [0, perimeter-1]
	 * @apiNote compute length modulo perimeter
	 */
	private int modp(int length, int perimeter) {
		assert perimeter > 0;
		int res = length % perimeter;
		if (res < 0) {
			return res + perimeter;
		}
		return res;
	}

	// NORMALIZE REAL LENGTH
	/**
	 * @return
	 *         <UL>
	 *         <LI>length module perimeter <I>&in; [0 , perimeter[</I>
	 *         if onTorus</LI>
	 *         <LI>length if !onTorus</LI>
	 *         </UL>
	 * @apiNote normalize _real length_ according to the geometry
	 * @implNote returns positive values
	 */
	public double normalize(double length) {
		if (!onTorus)
			return length;
		return modp(length, this.perimeter);
	}

	/**
	 * @return length % perimeter &in; [0, perimeter[
	 * @apiNote compute length modulo perimeter
	 */
	private double modp(double length, double perimeter) {
		assert perimeter > 0;
		double res = length % perimeter;
		if (res < 0) {
			return res + perimeter;
		}
		return res;
	}

	// DISTANCE
	/**
	 * @apiNote The distance on a Torus is that of the shortest path, sometimes
	 *          going in the opposite direction and across the border is shorter.
	 * @implNote Look for the detail on internet.
	 */
	public double distance(double position1, double position2) {
		if (Log.FINER)
			Log.logger.log(Level.FINER,
					"Distance: p1={0} p2={1} perimeter={2} torus={3}",
					new Object[] { position1, position2, perimeter, onTorus });

		double dist = Math.abs(position1 - position2);
		if (onTorus && (dist >= this.halfPerimeter)) {
			dist = this.perimeter - dist;
		}

		if (Log.FINER)
			Log.logger.log(Level.FINER, "	d={0}", dist);

		return dist;
	}

	// Garantit que xmax > xmin en ajoutant perimeter si nécessaire
	public double euclidian(double min, double max) {
		if (onTorus && max < min) {
			return max + perimeter;
		}
		return max;
	}
}
