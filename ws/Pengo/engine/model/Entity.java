package model;

// == ENTITY ==

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import gal.arguments.Category;
import collision.Bounding;
import collision.Box;
import gal_engine.Bot;
import geometry.Grid;
import geometry.ISU;
import view.Avatar;

public class Entity {

	// Données principales de l'entité
	protected Bounding bounding;
	protected Grid grid;
	protected ISU isu;
	protected String name;
	protected Avatar avatar;

	// Liens avec le modèle, le comportement et éventuellement un bot GAL
	protected Model model;
	protected Stunt stunt;
	protected Bot bot;

	// Position, taille et déplacement dans les deux repères du moteur
	protected ISU.Dimension size;
	protected ISU.Dimension step;
	protected Grid.Position position;
	protected ISU.Coord center;

	// Vitesses linéaire et angulaire
	protected ISU.Vector lSpeed;
	protected double aSpeed;

	protected int orientation_degree;

	// Cellules actuellement occupées par l'entité
	protected Set<Grid.Cell> occupied;

	// Catégorie utilisée par les automates GAL
	protected Category category;

	public Entity(String name) {
		this.name = name;
		this.orientation_degree = 0;
		this.lSpeed = null;
		this.aSpeed = 0;
		this.occupied = new HashSet<>();
	}

	// Positionnement dans la grille discrète
	public void setPosition(Grid.Position position) {
		if (position == null)
			return;

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

	// Positionnement dans le repère continu en centimètres
	public void setCoord(ISU.Coord center) {
		if (center == null)
			return;

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
		if (dimension == null)
			return;

		this.size = dimension.toISUDimension();

		if (center != null)
			setBounding();
	}

	protected void setSize(ISU.Dimension dimension) {
		if (dimension == null)
			return;

		this.size = dimension;

		if (center != null)
			setBounding();
	}

	public void setStep(Grid.Dimension step) {
		if (step == null)
			return;

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

		if (center != null && size != null)
			setBounding();
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

	// Arrête tous les mouvements de l'entité
	public void stop() {
		if (isu != null)
			this.lSpeed = isu.new Vector(0, 0);
		else
			this.lSpeed = null;

		this.aSpeed = 0;
	}

	// Oriente l'entité avec un angle normalisé entre 0 et 359
	public void turnTo(int degree) {
		orientation_degree = ((degree % 360) + 360) % 360;

		if (center != null && size != null)
			setBounding();
	}

	public ISU.Coord center() {
		return center;
	}

	public Grid.Position position() {
		return position;
	}

	public void snapToGrid() {
		if (position != null) {
			setPosition(position);
		}
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
		if (bounding == null) {
			return null;
		}

		return bounding.box();
	}

	public Category category() {
		return this.category;
	}

	// Déplacement dans le repère grille
	public void translate(Grid.Vector v) {
		if (position == null || v == null)
			return;

		retract();

		position.translate(v);
		center = position.toISUCoordCentered();

		check();

		if (size != null)
			setBounding();

		deploy();
	}

	// Déplacement dans le repère continu
	public void translate(ISU.Vector v) {
		if (center == null || v == null)
			return;

		retract();

		center.translate(v);
		position = center.toGridPosition();

		check();

		if (size != null)
			setBounding();

		deploy();
	}

	public void turn(int angle_degree) {
		turnTo(orientation_degree + angle_degree);
	}

	// Déplacements pratiques selon les directions cardinales
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

	// Test de collision avec une autre entité
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

	// Reconstruit la zone de collision à partir de l'avatar
	public void setBounding() {
		if (center == null || size == null) {
			return;
		}

		this.bounding = new Bounding();

		if (avatar != null) {
			avatar.buildBounding(bounding, center, size);
		}
	}

	// Réaction générale lorsqu'une collision est détectée.
	public void collision(Entity e) {
		stop();

		if (e == null) {
			return;
		}

		// Transmission de l'information de collision au comportement
		if (stunt != null) {
			stunt.collision(e);
		}

		// Transmission de l'information de collision au bot GAL
		if (bot != null) {
			bot.collision(e, 0);
		}
	}

	public void done() {
		if (stunt != null)
			stunt.done();

		if (bot != null)
			bot.completed();
	}

	// Ajoute l'entité dans la cellule correspondant à sa position
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

	// Retire l'entité de toutes les cellules qu'elle occupait
	public void retract() {
		for (Grid.Cell cell : occupied) {
			cell.remove(this);
		}

		occupied.clear();
	}

	// Vérifie la cohérence entre position grille et position continue
	void check() {
		if (position == null || center == null)
			return;

		if (!position.equiv(center.toGridPosition())) {
			throw new IllegalStateException("position incohérente avec center");
		}
	}

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

	// Mise à jour de l'entité à chaque tick du jeu
	public void tick(long elapsed) {
		if (elapsed < 0)
			return;

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

		// Déplacement = vitesse * temps
		ISU.Vector movement = center.isu().new Vector(lSpeed.x() * dt, lSpeed.y() * dt);

		boolean moved = model.move(this, movement);

		if (!moved) {
			stop();
		}
	}

	public String name() {
		return name;
	}

	// Multiplicateur de vitesse appliqué aux déplacements.
	// Valeur neutre par défaut côté moteur ; un jeu peut le surcharger
	// (ex. bonus de vitesse) sans que le moteur connaisse les types concrets.
	public double speedMultiplier() {
		return 1.0;
	}

	public boolean wizz() {
		return false;
	}

	public boolean canRunBot() {
		return true;
	}

	public boolean canShareCellWith(Entity other) {
		return false;
	}

	public boolean receiveGalHit(Entity attacker) {
		return false;
	}
}
