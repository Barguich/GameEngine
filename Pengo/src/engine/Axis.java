package engine;

public class Axis {

  // FIELDS

  private boolean onTorus;

  private double perimeter;
  private double halfPerimeter;

  // CONSTRUCTOR

  public Axis(boolean onTorus, double perimeter) {

    assert(perimeter > 0);

    this.onTorus = onTorus;

    this.perimeter = perimeter;
    this.halfPerimeter = perimeter / 2.0;
  }

  // NORMALIZE INTEGER

  public int normalize(int length) {

    if (!onTorus) {
      return length;
    }

    return modp(length, (int)perimeter);
  }

  public int modp(int length, int perimeter) {

    int r = length % perimeter;

    if (r < 0) {
      r += perimeter;
    }

    return r;
  }

  // NORMALIZE REAL

  public double normalize(double length) {

    if (!onTorus) {
      return length;
    }

    return modp(length, perimeter);
  }

  public double modp(double length, double perimeter) {

    double r = length % perimeter;

    if (r < 0) {
      r += perimeter;
    }

    return r;
  }

  // DISTANCE

public double distance(double position1, double position2) {

    double d = Math.abs(position2 - position1);

    if (!onTorus) {
        return d;
    }

    if (d > halfPerimeter) {
        d = perimeter - d;
    }

    return d;
  }
  // Garantit que xmax > xmin en ajoutant perimeter si nécessaire
public double euclidian(double min, double max) {
  if (onTorus && max < min) {
    return max + perimeter;
  }

  return max;
}
}