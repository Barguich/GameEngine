package engine;// = Circle =


public class Circle extends Shape {

   private double radius;
  // CONSTRUCTOR

  public Circle(ISU.Coord center, double radius){

      super(center);
      this.radius=radius;
      assert center!=null;
      assert radius>0;
  }
  public double radius(){
      return radius;
  }
  // INTERSECTION
   @Override
    public boolean intersects(Rect rect){
        return rect.intersects(this);
   }
    @Override

    public boolean intersects(Circle circle){
      double d= this.getCenter().distanceTo(circle.getCenter());
      return d< this.radius +circle.radius;
    }
    @Override
    public boolean intersects(iShape o){
        return o.intersects(this);
    }

}
