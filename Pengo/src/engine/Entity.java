// == ENTITY ==
package engine;

import java.io.PrintStream;

public abstract class Entity {

	// FIELDS
	protected final Grid grid;
	protected final ISU isu;
	protected final String name;

	// FIELDS
	protected ISU.Dimension size; // dimension de l'entité
	protected ISU.Dimension step; // dimension d'un pas de déplacement
	protected Grid.Position position; // position dans la grille
	protected ISU.Coord center; // coordonnées en cm du centre de l'entité
	protected Bounding bounding;
	protected ISU.Vector lSpeed;
	protected Stunt stunt;
	protected Avatar avatar;

	// FIELDS
	protected int orientation_degree; // orientation par rapport à l'axe des x

	// CONSTRUCTOR
	protected Entity(String name) {
		this.name = name;
		this.orientation_degree = 0;
		this.isu = game.Game.isu();
		this.grid = game.Game.grid();
		this.lSpeed = null;
		this.stunt = null;
	}

	// SETTER
	public void setPosition(Grid.Position position) {
		double x_cm = (position.x() + 0.5) * Game.game().cmPerCell;
		double y_cm = (position.y() + 0.5) * Game.game().cmPerCell;
		setCenter(isu.new Coord(x_cm, y_cm));
	}

	public void setCoord(ISU.Coord center) {
		setCenter(center);
	}

	public void setStunt(Stunt stunt) {
		this.stunt = stunt;
	}

	public ISU.Dimension size() {
		return this.size;
	}

	public Stunt stunt() {
		return this.stunt;
	}

	public void setlSpeed(ISU.Vector v) {
		this.lSpeed = v;
	}

	public ISU.Vector getlSpeed() {
		return this.lSpeed;
	}

	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}

	public Avatar avatar() {
		return this.avatar;
	}

	public Bounding boundingBox() {
		if (bounding == null)
			return null;
		return bounding.Bounding(orientation_degree);
	}

	protected abstract void setBounding();

	public boolean intersects(Entity e) {
		return this.bounding.intersects(e.bounding);
	}

	public double distanceCenterToCenter(Entity e) {
		return this.center.distanceTo(e.center);
	}

	private void setCenter(ISU.Coord newCenter) {
		Grid.Cell oldCell = (position != null) ? grid.cellAt(position) : null;
		this.center = newCenter;
		this.position = newCenter.toGridPosition();
		Grid.Cell newCell = grid.cellAt(position);
		if (oldCell != newCell) {
			if (oldCell != null)
				oldCell.remove(this);
			newCell.add(this);
		}

		if (this.size != null && this.center != null) {
			setBounding();
		}

		if (stunt != null) {
			stunt.set(center.x(), center.y());
		}
	}

	public void setSize(Grid.Dimension d) {
		double w_cm = d.x() * Game.game().cmPerCell;
		double h_cm = d.y() * Game.game().cmPerCell;
		this.size = isu.new Dimension(w_cm, h_cm);
	}

	public void setSize(ISU.Dimension d) {
		this.size = d;
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
		double dx_cm = v.x() * Game.game().cmPerCell;
		double dy_cm = v.y() * Game.game().cmPerCell;
		translate(isu.new Vector(dx_cm, dy_cm));
	}

	public void translate(ISU.Vector v) {
		ISU.Coord moved = center.mkTranslated(v);
		setCenter(moved);
	}

	// TURN
	/**
	 * @apiNote turn is a rotation around the center of the entity.
	 * @param angle_degree
	 */
	public void turn(int angle_degree) {
		orientation_degree = (orientation_degree + angle_degree) % 360;
		if (orientation_degree < 0)
			orientation_degree += 360;
	}

	// SHOW
	void show(PrintStream ps) {
		System.out.println("entity");
	}

	// === MOVE ===
	/**
	 * @apiNote déplacement vers le nord en nombre de pas
	 * @param nStep
	 */
	public void moveNorth(int nStep) {
		if (step == null)
			throw new IllegalStateException("step n'est pas initialisé : " + name);
		translate(isu.new Vector(0, -nStep * step.y()));
	}

	public void moveSouth(int nStep) {
		if (step == null)
			throw new IllegalStateException("step n'est pas initialisé : " + name);
		translate(isu.new Vector(0, nStep * step.y()));
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

}
