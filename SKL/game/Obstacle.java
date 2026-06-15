package game;

import engine.*;

public class Obstacle extends Entity {

  // CONSTRUCTOR

   public Obstacle(int x_ncell, int y_ncell){
       super("obstacle");
       setPosition(Game.grid().new Position(x_ncell,y_ncell));
       setSize(Game.grid().new Dimension(1,1));


   }
  // === Task COLLISION ===
   protected void setBounding(){
       if(center==null|| size==null)return;
        bounding=new Bounding();
        bounding.add(new Rect(center,size,0));

   }

}
