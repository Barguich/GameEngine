package engine;

public abstract class Shape implements iShape {

  // FIELD

   protected ISU isu;
   protected ISU.Coord center;

  // CONSTRUCTOR

    public Shape(ISU.Coord center){
      assert center!=null;
        this.center=center;
        this.isu=Game.isu();
    }
    public ISU.Coord getCenter(){
       return center;
    }

}
