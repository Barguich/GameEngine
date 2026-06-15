package engine;

import engine.Rect;

public interface iShape {

   boolean intersects(iShape shape);

   boolean intersects(Circle circle);

   boolean intersects(Rect rect);

}
