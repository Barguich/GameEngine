package engine;// = BOUNDING =


import java.util.HashSet;
import java.util.Set;


public class Bounding {

  // FIELDS

  private Set<iShape> boundings;

  // CONSTRUCTOR

   public Bounding(){
     boundings=new HashSet<>();
   }

  // BUILDER

   public void add(iShape shape){
     assert shape!=null;
     boundings.add(shape);
   }

  // INTERSECTION

   public boolean intersects(iShape shape){
       for(iShape s:boundings){
           if(s.intersects(shape))
               return true;
       }
       return false;
   }
   public boolean intersects(Bounding bounding) {
       assert bounding!=null;
       for (iShape s :boundings) {
           if (bounding.intersects(s))
               return true;
       }
       return false;
   }
   }
