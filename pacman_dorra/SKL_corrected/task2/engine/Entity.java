package engine;
// == ENTITY ==

import java.io.PrintStream;

import behavior.Bot;
import behavior.Box;
import game.Model;
import intersection.Bounding;
import behavior.Stunt;
import engine.Grid.Dimension;
import engine.Grid.Position;
import engine.Grid.Vector;
import engine.ISU.Coord;
import view.Avatar;

public class Entity {

	// FIELDS
	protected Bounding bounding;
	protected Grid grid;
	protected ISU isu;
	protected String name;
	protected Avatar avatar;

	// FIELDS
	protected Model model;
	protected Stunt stunt;
	protected Bot bot;
	protected ISU.Dimension size; // dimension de l'entité
	protected ISU.Dimension step; // dimension d'un pas de déplacement
	protected Grid.Position position; // position dans la grille
	protected ISU.Coord center; // coordonnées en cm du centre de l'entité

	protected ISU.Vector lSpeed;
	protected double aSpeed;

	// FIELDS

	protected int orientation_degree; // orientation par rapport à l'axe des x

	// CONSTRUCTOR

	public Entity(String name) {
		assert (name != null);

		this.name = name;
		this.orientation_degree = 0;
	}

	// SETTER

	public void setPosition(Grid.Position position) {


		this.position = position.copy();
		this.grid = position.grid();

		this.center = position.toISUCoordCentered();
		this.isu = center.isu();

		this.step = grid.new Dimension(1, 1).toISUDimension();

		check();

	}
	

	void setCoord(ISU.Coord center) {


		this.center = center.mkCopy();
		this.isu = center.isu();

		this.position = center.toGridPosition();
		this.grid = position.grid();

		check();

	}

	public void setSize(Grid.Dimension dimension) {
		this.size = dimension.toISUDimension();
	}

	protected void setSize(ISU.Dimension dimension) {
		this.size = dimension;
	}
	public void setModel(Model model) {
	    this.model = model;
	}

	public Model model() {
	    return model;
	}

	public void setStunt(Stunt stunt) {
	    this.stunt = stunt;
	}

	public Stunt stunt() {
	    return stunt;
	}
	
	public void setLinearSpeed(ISU.Vector v) {
	    this.lSpeed = v;
	}

	public void stop() {
	    this.lSpeed = isu.new Vector(0, 0);
	    this.aSpeed = 0;
	}

	public void turnTo(int degree) {
	    orientation_degree = ((degree % 360) + 360) % 360;
	    setBounding();
	}

	public void setBot(Bot bot) {
	    this.bot = bot;
	}

	public Bot bot() {
	    return bot;
	}
	public Box box() {
	    return bounding.box();
	}

	// GETTER

	public ISU.Coord center() {
		return center;
	}

	public Grid.Position position() {
		return position;
	}

	public int orientation() {
		return orientation_degree;
	}
	public ISU.Dimension step() {
	    return step;
	}
	public ISU.Dimension size() {
	    return size;
	}

	public ISU.Vector linearSpeed() {
	    return lSpeed;
	}
	public double angularSpeed() {
		return aSpeed;
	}
	public Avatar avatar() {
	    return avatar;
	}

	public void setAvatar(Avatar avatar) {
	    this.avatar = avatar;
	}

	// TRANSLATION

	public void translate(Grid.Vector v) {


		position.translate(v);
		center = position.toISUCoordCentered();

		check();
		setBounding();
	}

	public void translate(ISU.Vector v) {
		center.translate(v);
		position = center.toGridPosition();

		check();
		setBounding();
	}

	// TURN

	/**
	 * @apiNote turn is a rotation around the center of the entity.
	 * @param angle_degree
	 */
	public void turn(int angle_degree) {
	    turnTo(orientation_degree + angle_degree);
	}

	// SHOW

	void show(PrintStream ps) {
		ps.println("Entity: " + name);
		ps.println("orientation = " + orientation_degree);

		if (position != null) {
			position.show(ps);
		}

		if (center != null) {
			center.show(ps);
		}

		if (size != null) {
			size.show(ps);
		}
	}

	// === MOVE ===

	/**
	 * @apiNote déplacement vers le nord en nombre de pas
	 * @param nStep
	 */
	public void moveNorth(int nStep) {
		double length_cm = nStep * step.y();

		translate(isu.new Vector(0, -length_cm));
	}

	public void moveSouth(int nStep) {
		double length_cm = nStep * step.y();
		translate(isu.new Vector(0, length_cm));
	}

	/**
	 * @apiNote déplacement vers l'est en cm
	 * @param length_cm
	 */
	public void moveEast(double length_cm) {
		translate(isu.new Vector(length_cm, 0));
	}

	public void moveWest(double length_cm) {
		translate(isu.new Vector(-length_cm, 0));
	}

	// CHECK

	void check() {
		assert (position.equiv(center.toGridPosition()));
	}

	public void setStep(Grid.Dimension step) {
		this.step = step.toISUDimension();
	}

	public Bounding bounding() {
		return bounding;
	}

	public boolean intersects(Entity entity) {
		return this.bounding.intersects(entity.bounding);
	}

	public void setBounding() {
		this.bounding = new Bounding();
	}
	public void collision(Entity e) {
	    stop();

	    if (stunt != null) {
	        stunt.collision(e);
	    }

	    if (bot != null) {
	        bot.collision(e);
	    }
	}

	public void done() {
	    if (stunt != null) {
	        stunt.done();
	    }

	    if (bot != null) {
	        bot.done();
	    }
	}

	

}