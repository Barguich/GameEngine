package engine;

public interface iShape {

   boolean intersects(iShape shape);

   boolean intersects(Circle circle);

   boolean intersects(Rect rect);
   Box box();

}
