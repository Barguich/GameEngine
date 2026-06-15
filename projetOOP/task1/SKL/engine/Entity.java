package engine;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

public class Entity {

    // FIELDS

    protected Grid grid;
    protected ISU isu;
    private String name;
    protected Bounding bounding;
    private Set<Grid.Cell> occupied;
    protected Stunt stunt;
    protected ISU.Vector linearSpeed;
    protected double angularSpeed;
    protected Avatar avatar;
    protected Bot bot;

    // FIELDS

    private ISU.Dimension size; // dimension de l'entité
    private ISU.Dimension step; // dimension d'un pas de déplacement
    protected Grid.Position position; // position dans la grille
    private ISU.Coord center; // coordonnées en cm du centre de l'entité

    // FIELDS

    private int orientation_degree; // orientation par rapport à l'axe des x

    // CONSTRUCTOR

    public Entity(String name) {
        this.name = name;
        Game game = Game.game();
        this.grid = game.grid;
        this.isu = game.isu;

        this.position = grid.new Position(0, 0);
        this.center = position.toISUCoordCentered();
        this.orientation_degree = 0;
        this.occupied = new HashSet<Grid.Cell>();
        this.linearSpeed = isu.new Vector(0, 0);
        this.angularSpeed = 0;
    }

    // SETTER

    public void setPosition(Grid.Position position) {
        this.position = position;
        this.center = position.toISUCoordCentered();
    }

    public void setCoord(ISU.Coord center) {
        this.center = center;
        this.position = center.toGridPosition();
    }

    public void setSize(Grid.Dimension dimension) {
        this.size = dimension.toISUDimension();
    }

    public void setSize(ISU.Dimension dimension) {
        this.size = dimension;
    }

    protected void setBounding() {
        this.bounding = new Bounding();
    }

    // GETTER

    public ISU.Coord center() {
        return this.center;
    }

    public Grid.Position position() {
        return this.position;
    }

    public int orientation() {
        return this.orientation_degree;
    }

    // TRANSLATION

    public void translate(Grid.Vector v) {
        this.position.translate(v);
        center = this.position.toISUCoordCentered();
    }

    public void translate(ISU.Vector v) {
        this.center.translate(v);
        position = this.center.toGridPosition();
    }

    // TURN

    /**
     * @param angle_degree
     * @apiNote turn is a rotation around the center of the entity.
     */
    public void turn(int angle_degree) {
        this.orientation_degree += angle_degree;
        this.orientation_degree %= 360;
        if (orientation_degree < 0) {
            orientation_degree += 360;
        }
    }

    // SHOW

    void show(PrintStream ps) {
        ps.println("===== ENTITY =====");
        ps.println("name = " + name);
        ps.println("orientation = " + orientation_degree);
        ps.print("position = ");
        position.show(ps);
        ps.print("center = ");
        center.show(ps);
    }

    // === MOVE ===

    /**
     * @param nStep
     * @apiNote déplacement vers le nord en nombre de pas
     */
    public void moveNorth(int nStep) {
        Grid.Vector v = grid.new Vector(0, -nStep);
        translate(v);
    }

    public void moveSouth(int nStep) {
        Grid.Vector v = grid.new Vector(0, nStep);
        translate(v);
    }

    /**
     * @param length_cm
     * @apiNote déplacement vers l'est en cm
     */
    public void moveEast(double length_cm) {
        ISU.Vector v = isu.new Vector(length_cm, 0);
        translate(v);
    }

    public void moveWest(double length_cm) {
        ISU.Vector v = isu.new Vector(-length_cm, 0);
        translate(v);
    }


    public boolean intersects(Entity e) {
        return this.bounding.intersects(e.bounding);
    }

    public double distanceCenterToCenter(Entity e) {
        return this.center.distanceTo(e.center);
    }

    // DEPLOY in the Grid according to the BOUNDING

    public void deploy() {
        retract();
        occupy(position);
    }

    public void occupy(Grid.Position position) {
        Grid.Cell cell = grid.cellAt(position);
        occupied.add(cell);
        cell.add(this);
    }

    public void retract() {
        for (Grid.Cell cell : occupied) {
            cell.remove(this);
        }
        occupied.clear();
    }

    public void stunt(Stunt stunt) {
        this.stunt = stunt;
    }

    public Stunt stunt() {
        return stunt;
    }


    public ISU.Vector linearSpeed() {
        return linearSpeed;
    }

    public void setLinearSpeed(ISU.Vector speed) {
        this.linearSpeed = speed;
    }

    public double angularSpeed() {
        return angularSpeed;
    }

    public void setAngularSpeed(double speed) {
        this.angularSpeed = speed;
    }

    public Avatar avatar() {
        return avatar;
    }

    public void avatar(Avatar avatar) {
        this.avatar = avatar;
    }

    public int x(){
        return position.x();
    }
    public int y(){
        return position.y();
    }

    public Bot bot() {
        return bot;
    }

    public void bot(Bot bot) {
        this.bot = bot;
    }
}
