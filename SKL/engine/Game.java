package engine;// == GAME ==
import java.io.PrintStream;
//import engine.Picture;
 public class Game {

  // CONSTANT

   public final boolean torusOnXaxis = false; // vrai si l'axe X est une boucle fermée
   public final boolean torusOnYaxis = false; // vrai si l'axe Y est une boucle fermée

   public final double cmPerCell = 3.7; // échelle qui relie l'unité ncell à cm
   public static final int pixelPerCm = 10; // échelle qui relie l'unité pixel à cm

  // FIELDS
private static Game instance;
 int width_ncell; // largeur du monde en nombre de cellules
 int height_ncell; // hauteur du monde en nombre de celluls

   double width_cm; // largeur du monde en cm
   double height_cm; // hauteur du monde en cm

   public static Grid grid; // permet la création de coordonnées en unités ncell
   public static ISU isu; // permet la création de coordonnées en unités cm
   //public Picture pict; // permet la création de coordonnées en unités pixel, ne sera utilisé qu'à
                             // partir de Task2

  // CONSTRUCTORS

   public Game(int w_ncell, int h_ncell) {
      assert w_ncell>0;
      assert h_ncell>0;
      this.width_ncell=w_ncell;
      this.height_ncell=h_ncell;
      this.width_cm=w_ncell*cmPerCell;
      this.height_cm=h_ncell*cmPerCell;
      isu=new ISU(this);
      grid=new Grid(this);
      isu.set(grid);
      instance=this;
       assert grid!=null;
       assert isu!=null;


   }

   public Game(double w_cm, double h_cm) {
     assert w_cm>0;
     assert h_cm>0;
     this.width_cm=w_cm;
     this.height_cm=h_cm;
     this.width_ncell=(int) Math.round(w_cm/cmPerCell);
     this.height_ncell=(int)Math.round(h_cm/cmPerCell);
     isu=new ISU(this);
     grid=new Grid(this);
     isu.set(grid);
     instance=this;

   }

    public static ISU isu() {
       return isu;
    }

    public static Grid grid() {
       return grid;
    }


    // GETTER


   public static Game game() {
      return instance;
   }
       // SHOW

   public void show(PrintStream ps) {
      ps.printf("engine.Game: %d x %d cells | %.2f x %.2f cm | cmPerCell=%.2f%n",width_ncell,height_ncell,width_cm,height_cm,cmPerCell) ;
      grid.show(ps);
   }
   public static int PixelPerCm(){
       return pixelPerCm;
   }
}
