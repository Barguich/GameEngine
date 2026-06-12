package engine.model;

import java.util.List;

import engine.geometry.Grid.Cell;
import engine.geometry.ISU;
import engine.geometry.ISU.Vector;

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

	public void walk(int degree) {

		double speed = entity.step().x(); // 1 cellule par seconde

		if (degree == 0) {
			entity.turnTo(0);
			// Le stunt ne déplace pas directement l'entité. Il définit simplement sa
			// vitesse.
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
		System.out.println("Collision avec " + e);

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
