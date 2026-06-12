package engine.model;

import java.util.ArrayList;
import java.util.List;

import engine.geometry.Grid;

public class Model {
    // FIELDS
  private Grid grid;
  private List<Entity> entities;

  // CONSTRUCTOR
  public Model(Grid grid) {
    assert grid != null;

    this.grid = grid;
    this.entities = new ArrayList<Entity>();
  }

  // GETTER
  public Grid grid() {
    return grid;
  }

  public List<Entity> getEntities() {
    return entities;
  }

  // ADD, REMOVE Entity
  public void add(Entity e) {
    assert e != null;

    if (!entities.contains(e)) {
      entities.add(e);
    }
  }
  public void remove(Entity e) {
	  assert e != null;
	  entities.remove(e);
  }
  
  public boolean move(Entity e,ISU.Vector v) {
	  assert e != null;
	  assert v!=null;
	  e.translate(v);
	  List<Entity> collisions=collisions(e);
	  if(!collisions.isEmpty()) {
		  v.scale(-1);
		  e.translate(v);
		  return false;
	  }
	  return true;
	  
}
  public List<Entity> collisions(Entity e) {
	    assert e != null;

	    List<Entity> result = new ArrayList<Entity>();

	    for (Entity other : entities) {
	      if (other != e && e.intersects(other)) {
	        result.add(other);
	      }
	    }

	    return result;
	  }
  public List<Entity[]> allCollisions(){
	  List<Entity[]> result = new ArrayList<Entity[]>();
	  for (int i = 0; i < entities.size(); i++) {
	      Entity e1 = entities.get(i);

	      for (int j = i + 1; j < entities.size(); j++) {
	        Entity e2 = entities.get(j);

	        if (e1.intersects(e2)) {
	          result.add(new Entity[] { e1, e2 });
	        }
	      }
	  }
	  return result;
  }
  public void tick(long elapsed) {
	  assert elapsed >=0;
	  for (Entity e : entities) {
		    e.tick(elapsed);
		
	  
	  }
  }

}
