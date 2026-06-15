package game;// == GUM ==

import engine.Bounding;
import engine.Circle;
import engine.Entity;

public class Gum extends Entity {

  // CONSTRUCTOR

   public Gum(){
       super("gum");
   }
  // === Task COLLISION ===
   protected void setBounding(){
       if(center==null|| size==null)return;
       bounding=new Bounding();
       double radius=Math.min(size.x(),size.y())/2.0;
       bounding.add(new Circle(center,radius));

   }
}
