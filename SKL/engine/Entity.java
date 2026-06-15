package engine;// == ENTITY ==

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

public class Entity {

	// FIELDS
	protected Grid grid;
	protected ISU isu;
	protected final String name;
	protected Bounding bounding;
	protected Set<Grid.Cell> occupied=new HashSet<>();
	protected ISU.Vector lSpeed;
	// FIELDS

	protected ISU.Dimension size; // dimension de l'entité
	 protected ISU.Dimension step; // dimension d'un pas de déplacement
	 protected Grid.Position position; // position dans la grille
	 protected ISU.Coord center; // coordonnées en cm du centre de l'entité
	protected Stunt stunt;
	protected Avatar avatar;
	// FIELDS

	 public int orientation_degree; // orientation par rapport à l'axe des x

	// CONSTRUCTOR

	public Entity(String name) {
		assert name!=null;
		this.name=name;
		this.orientation_degree=0;
	}
	// SETTER

	 public void setPosition(Grid.Position position) {
		assert position!=null;
		this.position=position;
		this.grid=position.enclosingGrid();
		this.isu=grid.isu;
		 this.center=position.toISUCoordCentered();
		 if(size!=null){
			 setBounding();
		 }
		assert this.grid!=null;
		assert this.isu!=null;
		 assert this.center!=null;
	}
	 public void setCoord(ISU.Coord center) {
		assert center!=null;
		this.center=center;

		this.position=center.toGridPosition();
		this.grid=position.enclosingGrid();
				this.isu=grid.isu;
		assert this.position!=null;
		 assert this.grid!=null;
		 assert this.isu!=null;
		 if(size!=null)
			 setBounding();
	 }
	public void setSize(Grid.Dimension dimension) {
		assert dimension!=null;
		this.size=dimension.toISUDimension();
		if(center!=null)
			setBounding();
	}
	public void setSize(ISU.Dimension dimension) {
		assert dimension!=null;
		this.size=dimension;
		if(center!=null)
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
	public  Bounding bounding(){
		return bounding;
	}

	// TRANSLATION

	 public void translate(Grid.Vector v) {
		assert v!=null;
		position.translate(v);
		center=position.toISUCoordCentered();
		 if(size!=null)
			 setBounding();
	 }
	 public void translate(ISU.Vector v) {
		assert v!=null;
		center.translate(v);
		position=center.toGridPosition();
		 if(size!=null)
			 setBounding();
	 }
	// TURN

	/**
	 * @apiNote turn is a rotation around the center of the entity.
	 * @param angle_degree
	 */
	 public void turn(int angle_degree) {
		 orientation_degree=Math.floorMod(orientation_degree+angle_degree,360);
		 if(size!=null &&center!=null)
			 setBounding();
	 }
	// SHOW

	public void show(PrintStream ps) {
		 ps.printf("Entity[%s]orientation=%d%n",name,orientation_degree);
		 ps.print("position: ");
		 if(position!=null)
			 position.show(ps);
		 else
			 ps.println("null");
		 ps.print("center: ");
		 if(center!=null)
			 center.show(ps);
		 else
			 ps.println("null");
		 ps.print("size: ");
		 if(size!=null)
			 size.show(ps);
		 else ps.println("null");
	}
	// === MOVE ===

	/**
	 * @apiNote déplacement vers le nord en nombre de pas
	 * @param nStep
	 */
	public void moveNorth(int nStep) {
		assert step!=null;
		ISU.Vector v= isu.new Vector(0, -step.y()*nStep);
		translate(v);
	}
	public void moveSouth(int nStep) {
		assert step!=null;
		ISU.Vector v= isu.new Vector(0,step.y()*nStep);
		translate(v);
	}
	/**
	 * @apiNote déplacement vers l'est en cm
	 * @param length_cm
	 */
	public void moveEast(double length_cm) {
		ISU.Vector v=isu.new Vector(length_cm,0);
		translate(v);
	}
	 public void moveWest(double length_cm) {
		 ISU.Vector v=isu.new Vector(-length_cm,0);
		 translate(v);
	 }
	 public void setStep(ISU.Dimension step){
		assert step!=null;
		this.step=step;
	 }
	 public void setStep(Grid.Dimension step){
		assert step!=null;
		this.step= step.toISUDimension();
	 }
	 //@Override
	 protected void setBounding(){
		assert center!=null;
		assert size!=null;
		bounding=new Bounding();
		double radius=Math.min(size.x(),size.y())/2.0;
		bounding.add(new Circle(center,radius));
	 }
	 public boolean intersects(Entity e){
		assert e!=null;
		assert this.bounding!=null;
		assert e.bounding!=null;
		return this.bounding.intersects(e.bounding);
	 }
	 public double distanceCenterToCenter(Entity e){
		assert e!=null;
		assert this.center!=null;
		assert e.center!=null;
		return this.center.distanceTo(e.center);
	 }
	 public void deploy(){
		assert grid!=null;
		assert bounding !=null;
		assert position!=null;
		retract();
		int range=(int)Math.ceil(Math.max(size.x(),size.y())/grid.cmPerCell)+1;
		for(int dx=-range;dx<=range;dx++){
			for(int dy=-range;dy<=range;dy++) {
				int x = position.x() + dx;
				int y = position.y() + dy;

				if (x < 0 || x >= grid.width()) continue;
				if (y < 0 || y >= grid.height()) continue;

				Grid.Position candidate = grid.new Position(x, y);
				occupy(candidate);
			}

		}
	 }
	 public void occupy(Grid.Position pos){
		assert pos!=null;
		assert bounding!=null;
		ISU.Coord cellCenter=pos.toISUCoordCentered();
		ISU.Dimension cellSize=isu.new Dimension(grid.cmPerCell,grid.cmPerCell);
		Rect cellRect=new Rect(cellCenter,cellSize,0);
		if(bounding.intersects(cellRect)){
			Grid.Cell cell=grid.cellAt(pos);
			occupied.add(cell);
			cell.add(this);
		}
	 }
	 public void retract(){
		for(Grid.Cell cell:occupied){
			cell.remove(this);
		}
		occupied.clear();
	 }
	 public ISU.Vector getlSpeed(){
		return lSpeed;
	 }
	 public void setlSpeed(ISU.Vector v){
		this.lSpeed=v;
	 }
	 public ISU.Dimension size(){
		return size;
	 }
	 public Bounding getBounding(){
		return bounding;

	 }
	 public Set<Grid.Cell>getOccupied(){
		return occupied;
	 }
	public void setStunt(Stunt stunt){
		assert stunt!=null;
		this.stunt=stunt;
	}
	public Stunt getStunt(){
		return stunt;
	}

	public void setOrientation(int orientation) {
		this.orientation_degree = Math.floorMod(orientation,360);
		if(size!=null&& center!=null){
			setBounding();
		}
	}
	public void setAvatar(Avatar avatar){
		assert avatar!=null;
		this.avatar=avatar;
	}
	public Avatar avatar(){
		return avatar;
	}
}
