package game;// = BOSS =

/**
//  X
//  *XX   |--X--X--*--|
//  X
//  X
*/

import engine.*;

/**
 * @implNote Le game.Boss a la forme d'un `t`
 * @implNote il occupe 1 cellule au dessus de son
 *           centre, 2 au dessous de son centre, et
 *           2 cellules à droite de son centre.
 * @implNote Le game.Boss a donc une dimension (x=3Cell,y=4Cell)
 * @implNote Cette forme a été choisie pour pouvoir tester les rotations.
 */
 public class Boss extends Entity {

  // CONSTRUCTOR

   public Boss(){
       super("boss");
       setSize(Game.grid().new Dimension(3,4));
       setPosition(Game.grid().new Position(0,0));
   }
  // === Task COLLISION ===
   protected void setBounding(){
       assert center!=null;
       assert size!=null;
       bounding=new Bounding();
       double cellW=size.x()/3.0;
       double cellH=size.y()/4.0;
       //centre boss+1cellule à droite
       ISU.Coord centerH=center.mkTranslated(isu.new Vector(cellW,0));
       //3 cell large, 1 cell haut
       ISU.Dimension sizeH=isu.new Dimension(cellW*3,cellH);
       bounding.add(new Rect(centerH,sizeH,orientation_degree));
       //centre boss+0.5 cellule en bas
       ISU.Coord centerV=center.mkTranslated(isu.new Vector(0,cellH*0.5));
       //1 cell large, 4 cell haut
       ISU.Dimension sizeV=isu.new Dimension(cellW,cellH*4);
       bounding.add(new Rect(centerV,sizeV,orientation_degree));
   }
}
