package game;

import java.io.PrintStream;

import engine.Grid;
import engine.ISU;
import engine.Picture;

public class Game {

  // CONSTANT

  private final boolean torusOnXaxis = true;
  private final boolean torusOnYaxis = true;

 private final static double cmPerCell = 3.7;
  private final static int pixelPerCm = 7;

  // FIELDS

  private int width_ncell;
  private  int height_ncell;

 private  double width_cm;
 private  double height_cm;

  private  Grid grid;
  private  ISU isu;
  private Picture pict;

  // CONSTRUCTORS

  public Game(int w_ncell, int h_ncell) {

    assert(w_ncell > 0);
    assert(h_ncell > 0);

    width_ncell = w_ncell;
    height_ncell = h_ncell;

    width_cm = width_ncell * getCmpercell();
    height_cm = height_ncell * getCmpercell();

    assert(width_ncell * getCmpercell() == width_cm);
    assert(height_ncell * getCmpercell() == height_cm);

    isu = new ISU(this);
    grid = new Grid(this);
    pict = new Picture(this);

    isu.set(grid);
  }


  public boolean isTorusOnYaxis() {
    return torusOnYaxis;
    
}

  public boolean isTorusOnXaxis() {
    return torusOnXaxis;
    
}

  public static int getPixelPerCm() {
    return pixelPerCm;
    
}

  public static double getCmpercell() {
    return cmPerCell;
    
  }

  public Game(double w_cm, double h_cm) {

    assert(w_cm > 0);
    assert(h_cm > 0);

    width_cm = w_cm;
    height_cm = h_cm;

    width_ncell = (int)(width_cm / getCmpercell());
    height_ncell = (int)(height_cm / getCmpercell());

    assert(width_ncell * getCmpercell() == width_cm);
    assert(height_ncell * getCmpercell() == height_cm);

    isu = new ISU(this);
    grid = new Grid(this);
    pict = new Picture(this);

    isu.set(grid);
  }

  // GETTERS

  public int width_ncell() {
    return width_ncell;
  }

  public int height_ncell() {
    return height_ncell;
  }

  public double width_cm() {
    return width_cm;
  }

  public double height_cm() {
    return height_cm;
  }

  public Grid grid() {
    return grid;
  }

  public ISU isu() {
    return isu;
  }

  public Picture pict() {
    return pict;
  }

  public Game game() {
    return this;
  }

  // SHOW

  public void show(PrintStream ps) {

    ps.println("Game:");

    ps.println("width_ncell = " + width_ncell);
    ps.println("height_ncell = " + height_ncell);

    ps.println("width_cm = " + width_cm);
    ps.println("height_cm = " + height_cm);

    ps.println("cmPerCell = " + getCmpercell());
    ps.println("pixelPerCm = " + getPixelPerCm());
  }


}