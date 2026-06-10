package game;// == GHOST ==

import engine.*;

public class Ghost extends Entity {

  // CONSTRUCTOR

   public Ghost() {
       super("ghost");


   }
  // === Task COLLISION ===
   protected void setBounding(){
       if(center==null|| size==null)return;
       bounding=new Bounding();
       double radius=Math.min(size.x(),size.y())/2.0;
       bounding.add(new Circle(center,radius));
   }
}
