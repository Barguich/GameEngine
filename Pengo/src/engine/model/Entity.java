package engine.model;

// == ENTITY ==

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import engine.brain.Bot;
import engine.collision.Bounding;
import engine.collision.Box;
import engine.geometry.Grid;
import engine.geometry.ISU;
import engine.view.Avatar;
import game.pengo.model.PengoModel;
import game.pengo.model.PengoPlayer;
import game.pengo.model.Enemy;
import game.pengo.model.GoldBlock;
import game.pengo.model.FishBonus;
import game.pengo.model.IceBlock;
import engine.gal.arguments.Category;

public class Entity {

	// FIELDS
	protected Bounding bounding;
	protected Grid grid;
	protected ISU isu;
	protected String name;
	protected Avatar avatar;

	protected Model model;
	protected Stunt stunt;
	protected Bot bot;

	protected ISU.Dimension size;
	protected ISU.Dimension step;
	protected Grid.Position position;
	protected ISU.Coord center;

	protected ISU.Vector lSpeed;
	protected double aSpeed;

	protected int orientation_degree;

	// cellules occupées par l'entité
	protected Set<Grid.Cell> occupied;
	protected Category category;

	// CONSTRUCTOR
	public Entity(String name) {
		assert name != null;

		this.name = name;
		this.orientation_degree = 0;
		this.lSpeed = null;
		this.aSpeed = 0;
		this.occupied = new HashSet<>();
	}

	// SETTER

	public void setPosition(Grid.Position position) {
		retract();

		this.position = position.copy();
		this.grid = position.grid();

		this.center = position.toISUCoordCentered();
		this.isu = center.isu();

		this.step = grid.new Dimension(1, 1).toISUDimension();

		check();

		if (size != null)
			setBounding();

		deploy();
	}

	public void setCoord(ISU.Coord center) {
		retract();

		this.center = center.mkCopy();
		this.isu = center.isu();

		this.position = center.toGridPosition();
		this.grid = position.grid();

		check();

		if (size != null)
			setBounding();

		deploy();
	}

	public void setSize(Grid.Dimension dimension) {
		this.size = dimension.toISUDimension();

		if (center != null)
			setBounding();
	}

	protected void setSize(ISU.Dimension dimension) {
		this.size = dimension;

		if (center != null)
			setBounding();
	}

	public void setStep(Grid.Dimension step) {
		this.step = step.toISUDimension();
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public void setStunt(Stunt stunt) {
		this.stunt = stunt;
	}

	public void setBot(Bot bot) {
		this.bot = bot;
	}

	public void setAvatar(Avatar avatar) {
		this.avatar = avatar;
	}

	public void setLinearSpeed(ISU.Vector v) {
		this.lSpeed = v;
	}

	public void setAngularSpeed(double aSpeed) {
		this.aSpeed = aSpeed;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public void stop() {
		if (isu != null)
			this.lSpeed = isu.new Vector(0, 0);
		else
			this.lSpeed = null;

		this.aSpeed = 0;
	}

	public void turnTo(int degree) {
		orientation_degree = ((degree % 360) + 360) % 360;

		if (center != null && size != null)
			setBounding();
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

	public Model model() {
		return model;
	}

	public Stunt stunt() {
		return stunt;
	}

	public Bot bot() {
		return bot;
	}

	public Avatar avatar() {
		return avatar;
	}

	public Bounding bounding() {
		return bounding;
	}

	public Box box() {
		if (bounding == null)
			return null;

		return bounding.box();
	}

	public Category category() {
		return this.category;
	}

	// TRANSLATION

	public void translate(Grid.Vector v) {
		if (position == null)
			return;

		retract();

		position.translate(v);
		center = position.toISUCoordCentered();

		check();

		if (size != null)
			setBounding();

		deploy();
	}

	public void translate(ISU.Vector v) {
		if (center == null)
			return;

		retract();

		center.translate(v);
		position = center.toGridPosition();

		check();

		if (size != null)
			setBounding();

		deploy();
	}

	// TURN

	public void turn(int angle_degree) {
		turnTo(orientation_degree + angle_degree);
	}

	// MOVE

	public void moveNorth(int nStep) {
		if (step == null)
			throw new IllegalStateException("step non initialisé");

		double length_cm = nStep * step.y();
		translate(isu.new Vector(0, -length_cm));
	}

	public void moveSouth(int nStep) {
		if (step == null)
			throw new IllegalStateException("step non initialisé");

		double length_cm = nStep * step.y();
		translate(isu.new Vector(0, length_cm));
	}

	public void moveEast(double length_cm) {
		translate(isu.new Vector(length_cm, 0));
	}

	public void moveWest(double length_cm) {
		translate(isu.new Vector(-length_cm, 0));
	}

	// COLLISION / INTERSECTION

	public boolean intersects(Entity entity) {
		if (entity == null)
			return false;

		if (this.bounding == null || entity.bounding == null)
			return false;

		return this.bounding.intersects(entity.bounding);
	}

	public double distanceCenterToCenter(Entity e) {
		if (e == null || this.center == null || e.center == null)
			return Double.POSITIVE_INFINITY;

		return this.center.distanceTo(e.center);
	}

	public void setBounding() {
		this.bounding = new Bounding();
	}

	public void collision(Entity e) {
		stop();

		if (e == null) {
			return;
		}

		// 1) Joueur touche ennemi : perte de vie
		if (this instanceof PengoPlayer && e instanceof Enemy) {
			if (model instanceof PengoModel) {
				((PengoModel) model).loseLife();
			}
		}

		// 2) Ennemi touche GoldBlock : ennemi gelé
		if (this instanceof Enemy && e instanceof GoldBlock) {
			((Enemy) this).freeze(5000);
		}

		// 3) Joueur touche FishBonus : bonus récupéré
		if (this instanceof PengoPlayer && e instanceof FishBonus) {
			((FishBonus) e).consume((PengoPlayer) this);
		}

		// 4) Bloc de glace glissant touche ennemi : ennemi tué + score
		if (this instanceof IceBlock && e instanceof Enemy) {
			IceBlock block = (IceBlock) this;

			if (block.sliding()) {
				((Enemy) e).kill();

				if (model instanceof PengoModel) {
					((PengoModel) model).addScore(100);
				}
			}
		}

		if (stunt != null) {
			stunt.collision(e);
		}

		if (bot != null) {
			bot.collision(e);
		}
	}

	public void done() {
		if (stunt != null)
			stunt.done();

		if (bot != null)
			bot.done();
	}

	// DEPLOY / OCCUPY / RETRACT

	public void deploy() {
		if (position == null)
			return;

		occupy(position);
	}

	public void occupy(Grid.Position position) {
		if (position == null || grid == null)
			return;

		Grid.Cell cell = grid.cellAt(position);

		if (cell == null)
			return;

		if (!occupied.contains(cell)) {
			cell.add(this);
			occupied.add(cell);
		}
	}

	public void retract() {
		for (Grid.Cell cell : occupied) {
			cell.remove(this);
		}

		occupied.clear();
	}

	// CHECK

	void check() {
		if (position == null || center == null)
			return;

		assert position.equiv(center.toGridPosition());
	}

	// SHOW

	public void show(PrintStream ps) {
		ps.println("Entity: " + name);
		ps.println("orientation = " + orientation_degree);

		if (position != null)
			position.show(ps);
		else
			ps.println("position = null");

		if (center != null)
			center.show(ps);
		else
			ps.println("center = null");

		if (size != null)
			size.show(ps);
		else
			ps.println("size = null");
	}

	public void tick(long elapsed) {
		assert elapsed >= 0;

		if (stunt != null) {
			stunt.update(elapsed);
		}

		if (model == null || center == null || lSpeed == null) {
			return;
		}

		if (lSpeed.norm() == 0) {
			return;
		}

		double dt = elapsed / 1000.0;

		ISU.Vector movement = center.isu().new Vector(lSpeed.x() * dt, lSpeed.y() * dt);

		boolean moved = model.move(this, movement);

		if (!moved) {
			stop();
		}
	}
	
}