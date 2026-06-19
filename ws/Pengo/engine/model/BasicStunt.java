package model;

import java.util.List;

import geometry.Grid.Cell;
import pengo.model.PengoPlayer;

public class BasicStunt extends Stunt {
	// Le stunt est responsable de l'exécution des mouvements
	// de l'entité associée.

	public BasicStunt(Model model, Entity entity) {
		super(model, entity);
	}

	@Override
	public void set(int orientation) {
		int delta = orientation - entity.orientation();
		entity.turn(delta);

	}

	@Override
	public void set(Cell c) {
		assert c != null;
		entity.setPosition(c.position());

	}

	@Override   
	public void collision(List<Entity> entities) {
		for (Entity e : entities) {
			collision(e);
		}

	}

	@Override
	public void walk(int degree) {

	    double speed = entity.step().x();

	    if (entity instanceof PengoPlayer) {
	        speed *= ((PengoPlayer) entity).speedMultiplier();
	    }

	    if (degree == 0) {
	        entity.turnTo(0);
	        entity.setLinearSpeed(entity.center().isu().new Vector(speed, 0));
	    }

	    else if (degree == 90) {
	        entity.turnTo(90);
	        entity.setLinearSpeed(entity.center().isu().new Vector(0, -speed));
	    }

	    else if (degree == 180) {
	        entity.turnTo(180);
	        entity.setLinearSpeed(entity.center().isu().new Vector(-speed, 0));
	    }

	    else if (degree == 270) {
	        entity.turnTo(270);
	        entity.setLinearSpeed(entity.center().isu().new Vector(0, speed));
	    }
	}  

	@Override
	public void set(double x_cm, double y_cm) {
		entity.setCoord(entity.center().isu().new Coord(x_cm, y_cm));

	}

	@Override
	public void collision(Entity e) {
		if (e == null) {
			return;
		}

		System.out.println("Collision avec " + e);
		entity.stop();
	}

	@Override
	public void done() {
		entity.stop();
	}

	@Override
	public void update(long elapsed) {
		// Rien ici pour BasicStunt.
		// Le déplacement est appliqué par Entity.tick avec linearSpeed.
	}

}
