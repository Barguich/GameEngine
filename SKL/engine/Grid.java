package engine;// = GRID =

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Grid {

    // FIELDS

    public ISU isu;
    private Axis xAxis, yAxis;

    private int width_ncell, height_ncell;
    public final double cmPerCell;
    private final int pixelPerCm;
    private Cell[][] grid;
    // CONSTRUCTOR

    public Grid(Game game) {
        assert(game!=null);

        this.width_ncell=game.width_ncell;
        this.height_ncell=game.height_ncell;
       this.cmPerCell= game.cmPerCell;
       this.pixelPerCm= game.pixelPerCm;
        this.xAxis=new Axis(game.torusOnXaxis, width_ncell);
        this.yAxis=new Axis(game.torusOnYaxis, height_ncell);
        this.isu=game.isu;

        assert(width_ncell>0);
        assert(height_ncell>0);
        assert(cmPerCell>0);
        assert(pixelPerCm>0);
        assert(xAxis!=null);
        assert(yAxis!=null);
        assert(isu!=null);
        init();

        assert(grid!=null);




    }

    // INIT

    private void init() {
        int x;
        int y;
        grid=new Cell[width_ncell][height_ncell];
        for(x=0;x<width_ncell;x++){
            for(y=0;y<height_ncell;y++){
                grid[x][y]=new Cell(new Position(x,y));
            }
        }
        assert(grid!=null);
    }

    // GETTER

    public int width() {
        return width_ncell;
    }

    public int height() {
        return height_ncell;
    }

    public Grid.Cell cellAt(Grid.Position p) {
        assert(p!=null);
        int x=xAxis.normalize(p.x());
        int y=yAxis.normalize(p.y());
        assert x>=0 && x<width_ncell;
        assert y>=0 && y<height_ncell;
        Cell c=grid[x][y];
        assert c!=null;
        assert c.position.x()==x;
        assert c.position.y()==y;
        return c;
    }

    // SHOW

    public void show(PrintStream ps) {
        ps.printf("Grid:%d x %d cells%n",width_ncell,height_ncell);
    }




    // == DIMENSION (nb cell) ==

    public class Dimension {
        protected int x_ncell, y_ncell;

        // CONSTRUCTOR

        public Dimension(int x_ncell, int y_ncell) {
           this.x_ncell=x_ncell;
           this.y_ncell=y_ncell;
           normalize();



        }
        protected Dimension(int x_ncell, int y_ncell,boolean doNotnormalize) {
            this.x_ncell=x_ncell;
            this.y_ncell=y_ncell;
            if(doNotnormalize) normalize();

        }

        // GETTER

        public int x() {
            return x_ncell;

        }

        public int y() {
            return y_ncell;
        }

        // GEOMETRY

        public void normalize() {

            x_ncell = xAxis.normalize(x_ncell);
            y_ncell = yAxis.normalize(y_ncell);
        }
        // EQUALS / EQUIV
        public boolean equals(Object o) {
            if(o==null)return false;
            if(o.getClass()!=this.getClass())return false;
            Dimension d=(Dimension) o;
            return x_ncell==d.x_ncell && y_ncell==d.y_ncell;
        }

        public boolean equiv(Dimension d) {
            assert d!=null;
                return xAxis.normalize(x_ncell)==xAxis.normalize(d.x_ncell)
                        && yAxis.normalize(y_ncell)==yAxis.normalize(d.y_ncell);
             }

        // CONVERSION

       public ISU.Dimension toISUDimension() {
            return isu.new Dimension(x_ncell*cmPerCell,y_ncell*cmPerCell);
             }

        // SHOW

        public void show(PrintStream ps) {
            ps.printf("Grid.Dimension(%d ncell, %d ncell)%n",x_ncell,y_ncell);
             }

    }

    // == VECTOR ==

    public class Vector extends Dimension {

        // CONSTRUCTOR

        public Vector(int x_ncell, int y_ncell) {
                super(x_ncell,y_ncell,false);
             }

        // OPERATION

        public void add(Vector v) {
            assert v!=null;

            x_ncell+=v.x_ncell;
            y_ncell+=v.y_ncell;

        }

        // SHOW

        public void show(PrintStream ps) {
            ps.printf("Grid.Vector(%d ncell, %d ncell)%n",x_ncell,y_ncell);
             }

    }

    // == POINT ==

    public class Position extends Dimension {
        // CONSTRUCTOR

        public Position(int x_ncell, int y_ncell) {
            super(x_ncell,y_ncell);
           // normalize();
        }

        // COPY ? if needed

        public Grid.Position copy() {
            return new Position(x_ncell,y_ncell);
         }

       public Grid enclosingGrid(){
            return Grid.this;
       }

        // EQUALS
        public boolean equals(Object o) {
            if(o==null)return false;
            if(o.getClass()!=this.getClass())return false;

            Position p=(Position) o;
            return x_ncell==p.x_ncell && y_ncell==p.y_ncell;
        }

        // TRANSLATION

        public void translate(Vector v) {
            assert v!=null;
            x_ncell=xAxis.normalize(x_ncell+v.x_ncell);
            y_ncell=yAxis.normalize(y_ncell+v.y_ncell);
        }
        public void moveNorth(int n_ncell) {
            y_ncell=yAxis.normalize(y_ncell-n_ncell);
        }

        // ROTATION ? if needed

        public void rotateAround(Grid.Position position, int angle_degree) {
            assert position!=null;
            double teta= Math.toRadians(angle_degree);
            int dx=x_ncell-position.x_ncell;
            int dy= y_ncell-position.y_ncell;
            int newX=(int)Math.round(dx*Math.cos(teta) -dy*Math.sin(teta));
            int newY=(int)Math.round(dx*Math.sin(teta) +dy*Math.cos(teta));
            x_ncell= xAxis.normalize(position.x_ncell+newX);
            y_ncell=yAxis.normalize(position.y_ncell+newY);
        }

        // DISTANCE

        public double distanceTo(Position p) {
            assert p!=null;
            double dx=xAxis.distance(x_ncell,p.x_ncell);
            double dy= yAxis.distance(y_ncell,p.y_ncell);
            return Math.sqrt(dx*dx+dy*dy);
              }

        // CONVERSION

        public ISU.Coord toISUCoordCentered() {
            return isu.new Coord((x_ncell+0.5)*cmPerCell,(y_ncell+0.5)*cmPerCell);
             }

        public ISU.Coord toISUCoord() {
            return isu.new Coord(x_ncell*cmPerCell,y_ncell*cmPerCell);
        }

      //  public Picture.Pixel toPicturePixel() { return null; }

        // SHOW

        public void show(PrintStream ps) {
            ps.printf("Grid.Position(%d, %d)%n",x_ncell,y_ncell);
             }

    }

    // === CELL ===

    public class Cell {

        Grid.Dimension size;
       public Grid.Position position;
        List<Entity> entities;

        // CONSTRUCTOR

        public Cell(Position p) {
            assert p!=null;
            this.position=p;
            this.size=new Dimension(1,1);
            this.entities=new ArrayList<>();
            assert position!=null;
            assert size!=null;
            assert entities !=null;

             }

        // ADD

        public void add(Entity e) {
            assert e!=null;
            if(!entities.contains(e)) entities.add(e);
            assert entities.contains(e);

        }

        // REMOVE

        public void remove(Entity e) {
            assert e!=null;
            entities.remove(e);
            assert !entities.contains(e);
             }

        // PREDICATE


        public boolean contains(Entity e) {
            assert e!=null;
            return entities.contains(e);
        }

        // SHOW

        public void show(PrintStream ps) {
            ps.print("Cell at");
            position.show(ps);
             }

    }
}
