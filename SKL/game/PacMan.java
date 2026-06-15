package game;// == PAC MAN ==

import engine.Bounding;
import engine.Circle;
import engine.Entity;

public class PacMan extends Entity {

  // CONSTRUCTOR

   public PacMan(){
       super("PacMan");


   }
  // === Task COLLISION ===
     @Override
   protected void setBounding(){
       assert center!=null;
       assert size!=null;
       bounding=new Bounding();
       double radius=Math.min(size.x(),size.y())/2.0;
       bounding.add(new Circle(center,radius));
   }
}
