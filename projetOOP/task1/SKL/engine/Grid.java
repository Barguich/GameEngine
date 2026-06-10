package engine;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Grid {

    // FIELDS
    private Game game;
    ISU isu;
    private Axis xAxis, yAxis;
    private int width_ncell, height_ncell;
    private Cell[][] grid;

    // CONSTRUCTOR

    public Grid(Game game) {
        this.game = game;
        this.width_ncell = game.width_ncell;
        this.height_ncell = game.height_ncell;
        this.xAxis = new Axis(game.torusOnXaxis, width_ncell);
        this.yAxis = new Axis(game.torusOnYaxis, height_ncell);
        init();
    }

    // INIT

    public void init() {
        this.grid = new Cell[width_ncell][height_ncell];
        for (int i = 0; i < width_ncell; i++) {
            for (int j = 0; j < height_ncell; j++) {
                this.grid[i][j] = new Cell(new Position(i, j));
            }
        }
    }

    public void set(ISU isu) {
        this.isu = isu;
    }
    // GETTER

    public int width() {
        return width_ncell;
    }

    public int height() {
        return height_ncell;
    }

    public Grid.Cell cellAt(Grid.Position p) {
        int x = xAxis.normalize(p.x());
        int y = yAxis.normalize(p.y());
        return grid[x][y];
    }

    // SHOW

    public void show(PrintStream ps) {
        ps.println("===== GRID =====");
        ps.println("width = " + width_ncell);
        ps.println("height = " + height_ncell);
    }


    // == DIMENSION (nb cell) ==

    public class Dimension {
        protected int x_ncell, y_ncell;

        // CONSTRUCTOR

        public Dimension(int x_ncell, int y_ncell) {
            this.x_ncell = x_ncell;
            this.y_ncell = y_ncell;
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
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Dimension)) {
                return false;
            }
            Dimension d = (Dimension) o;
            return x_ncell == d.x_ncell && y_ncell == d.y_ncell;
        }

        public boolean equiv(Dimension d) {
            if (d == null)
                return false;
            return (xAxis.normalize(x_ncell) == xAxis.normalize(d.x_ncell)) && (yAxis.normalize(y_ncell) == yAxis.normalize(d.y_ncell));
        }

        // CONVERSION

        public ISU.Dimension toISUDimension() {
            return isu.new Dimension(x_ncell * game.cmPerCell, y_ncell * game.cmPerCell);
        }

        // SHOW

        public void show(PrintStream ps) {
            ps.println("(" + x_ncell + "," + y_ncell + ")");
        }

    }

    // == VECTOR ==

    public class Vector extends Position {

        // CONSTRUCTOR

        public Vector(int x_ncell, int y_ncell) {
            super(x_ncell, y_ncell);
        }

        // OPERATION

        public void add(Vector v) {
            x_ncell += v.x_ncell;
            y_ncell += v.y_ncell;
            normalize();
        }

        // SHOW
        @Override
        public void show(PrintStream ps) {
            ps.println("Vector(" + x_ncell + "," + y_ncell + ")");
        }

    }

    // == POINT ==

    public class Position extends Dimension {

        // CONSTRUCTOR

        public Position(int x_ncell, int y_ncell) {
            super(x_ncell, y_ncell);
        }

        // COPY ? if needed

        public Grid.Position copy() {
            return new Position(x_ncell, y_ncell);
        }

        // EQUALS
//        @Override
//        public boolean equals(Object o) {
//        }

        // TRANSLATION

        public void translate(Vector v) {
            this.x_ncell += v.x_ncell;
            this.y_ncell += v.y_ncell;
            this.normalize();
        }

        void moveNorth(int n_ncell) {
            this.y_ncell -= n_ncell;
            this.normalize();
        }

        // ROTATION ? if needed

        public void rotateAround(Grid.Position position, int angle_degree) {

        }

        // DISTANCE

        public double distanceTo(Position p) {
            double dx = xAxis.distance(x_ncell, p.x_ncell);
            double dy = yAxis.distance(y_ncell, p.y_ncell);
            return Math.sqrt(dx * dx + dy * dy);
        }

        // CONVERSION

        public ISU.Coord toISUCoord() {
            return isu.new Coord(x_ncell * game.cmPerCell, y_ncell * game.cmPerCell);
        }

        public ISU.Coord toISUCoordCentered() {
            return isu.new Coord((x_ncell + 0.5) * game.cmPerCell, (y_ncell + 0.5) * game.cmPerCell);
        }

//        Picture.Pixel toPicturePixel() {
//            return null;
//        }

        // SHOW
        @Override
        public void show(PrintStream ps) {
            ps.println("Position(" + x_ncell + "," + y_ncell + ")");
        }

    }

    // === CELL ===

    public class Cell {

        private Grid.Dimension size;
        private Grid.Position position;
        private List<Entity> entities;

        // CONSTRUCTOR

        public Cell(Position p) {
            this.position = p;
            this.size = new Grid.Dimension(1, 1);
            this.entities = new ArrayList<>();
        }

        // ADD

        public void add(Entity e) {
            if (!entities.contains(e)) {
                this.entities.add(e);
            }
        }

        // REMOVE

        public void remove(Entity e) {
            this.entities.remove(e);
        }

        // PREDICATE

        public boolean contains(Entity e) {
            return this.entities.contains(e);
        }

        // SHOW

        public void show(PrintStream ps) {
            ps.println("Cell : ");
            position.show(ps);
            ps.println("entities = " + entities.size());
        }

    }
}
