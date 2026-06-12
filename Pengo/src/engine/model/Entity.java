// == ENTITY ==
package engine.model;

import java.io.PrintStream;

import engine.collision.Bounding;
import engine.geometry.Grid;
import engine.geometry.ISU;
import engine.view.Avatar;

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
		this.isu = game.Game.instance().isu();
		this.grid = game.Game.instance().grid();
		this.lSpeed = null;
		this.stunt = null;
	}

	// ─── SETTER ───────────────────────────────────────────────────────────────

	public void setPosition(Grid.Position position) {
		double x_cm = (position.x() + 0.5) * game.Game.getCmpercell();
		double y_cm = (position.y() + 0.5) * game.Game.getCmpercell();
		setCenter(isu.new Coord(x_cm, y_cm));
	}

	public void setCoord(ISU.Coord center) {
		setCenter(center);
	}

	public void setSize(Grid.Dimension d) {
		double w_cm = d.x() * game.Game.getCmpercell();
		double h_cm = d.y() * game.Game.getCmpercell();
		this.size = isu.new Dimension(w_cm, h_cm);
	}

	public void setSize(ISU.Dimension d) {
		this.size = d;
	}

	public void setStep(Grid.Dimension d) {
		this.step = d.toISUDimension();
	}

	public void setStep(ISU.Dimension d) {
		this.step = d;
	}

	public void setStunt(Stunt stunt) {
		this.stunt = stunt;
	}

	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}

	/** Vitesse linéaire — appelée par BasicStunt.walk() */
	public void setLinearSpeed(ISU.Vector v) {
		this.lSpeed = v;
	}

	/** Alias spec : setlSpeed */
	public void setlSpeed(ISU.Vector v) {
		this.lSpeed = v;
	}

	public ISU.Vector getlSpeed() {
		return this.lSpeed;
	}

	// ─── TURN ────────────────────────────────────────────────────────────────

	/**
	 * @apiNote turn is a rotation around the center of the entity.
	 */
	public void turn(int angle_degree) {
		orientation_degree = (orientation_degree + angle_degree) % 360;
		if (orientation_degree < 0)
			orientation_degree += 360;
	}

	/**
	 * @apiNote Set orientation to an absolute value.
	 *          Appelée par BasicStunt.walk().
	 */
	public void turnTo(int degree) {
		orientation_degree = ((degree % 360) + 360) % 360;
		if (bounding != null)
			setBounding();
	}

	// ─── BOUNDING — abstract ─────────────────────────────────────────────────

	protected abstract void setBounding();

	// ─── GETTER ──────────────────────────────────────────────────────────────

	public ISU.Coord center() {
		return this.center;
	}

	public Grid.Position position() {
		return this.position;
	}

	public int orientation() {
		return this.orientation_degree;
	}

	public ISU.Dimension size() {
		return this.size;
	}

	public ISU.Dimension step() {
		return this.step;
	}

	public Stunt stunt() {
		return this.stunt;
	}

	public Avatar avatar() {
		return this.avatar;
	}

	public Bounding bounding() {
		return this.bounding;
	}

	// ─── INTERSECTION ────────────────────────────────────────────────────────

	public boolean intersects(Entity e) {
		if (this.bounding == null || e.bounding == null)
			return false;
		return this.bounding.intersects(e.bounding);
	}

	public double distanceCenterToCenter(Entity e) {
		return this.center.distanceTo(e.center);
	}

	// ─── TRANSLATION ─────────────────────────────────────────────────────────

	public void translate(Grid.Vector v) {
		double dx_cm = v.x() * game.Game.getCmpercell();
		double dy_cm = v.y() * game.Game.getCmpercell();
		translate(isu.new Vector(dx_cm, dy_cm));
	}

	public void translate(ISU.Vector v) {
		ISU.Coord moved = center.mkTranslated(v);
		setCenter(moved);
	}

	// ─── setCenter — point d'entrée unique pour tout changement de position ──

	private void setCenter(ISU.Coord newCenter) {
		// retrait de l'ancienne cellule
		Grid.Cell oldCell = (position != null) ? grid.cellAt(position) : null;

		this.center = newCenter;
		this.position = newCenter.toGridPosition();

		// inscription dans la nouvelle cellule
		Grid.Cell newCell = grid.cellAt(position);
		if (oldCell != newCell) {
			if (oldCell != null)
				oldCell.remove(this);
			newCell.add(this);
		}

		// recalcul du bounding si la taille est connue
		if (this.size != null) {
			setBounding();
		}

		// notification du stunt
		if (stunt != null) {
			stunt.set(center.x(), center.y());
		}
	}

	// ─── MOVE ────────────────────────────────────────────────────────────────

	/**
	 * @apiNote déplacement vers le nord en nombre de pas
	 */
	public void moveNorth(int nStep) {
		if (step == null)
			throw new IllegalStateException("step non initialisé : " + name);
		translate(isu.new Vector(0, -nStep * step.y()));
	}

	public void moveSouth(int nStep) {
		if (step == null)
			throw new IllegalStateException("step non initialisé : " + name);
		translate(isu.new Vector(0, nStep * step.y()));
	}

	/**
	 * @apiNote déplacement vers l'est en cm
	 */
	public void moveEast(double length_cm) {
		translate(isu.new Vector(length_cm, 0));
	}

	public void moveWest(double length_cm) {
		translate(isu.new Vector(-length_cm, 0));
	}

	// ─── SHOW ────────────────────────────────────────────────────────────────

	public void show(PrintStream ps) {
		ps.printf("Entity[%s] orientation=%d%n", name, orientation_degree);
		ps.print("position: ");
		if (position != null)
			position.show(ps);
		else
			ps.println("null");
		ps.print("center: ");
		if (center != null)
			center.show(ps);
		else
			ps.println("null");
		ps.print("size: ");
		if (size != null)
			size.show(ps);
		else
			ps.println("null");
	}
}
