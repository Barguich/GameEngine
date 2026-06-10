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
}
