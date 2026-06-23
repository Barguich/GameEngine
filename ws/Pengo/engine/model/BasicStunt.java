package model;

import java.util.List;

import geometry.Grid.Cell;

public class BasicStunt extends Stunt {

	// Stunt simple : il applique directement les déplacements à l'entité
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
		if (c == null)
			return;

		entity.setPosition(c.position());
	}

	@Override
	public void collision(List<Entity> entities) {

		// Traite chaque collision une par une
		for (Entity e : entities) {
			collision(e);
		}
	}

	@Override
	public void walk(int degree) {

		// Vitesse de base de l'entité, éventuellement modulée par un bonus.
		// Le multiplicateur est neutre par défaut ; un jeu peut le redéfinir
		// en surchargeant Entity.speedMultiplier().
		double speed = entity.step().x() * entity.speedMultiplier();

		// Déplacement vers la droite
		if (degree == 0) {
			entity.turnTo(0);
			entity.setLinearSpeed(entity.center().isu().new Vector(speed, 0));
		}

		// Déplacement vers le haut
		else if (degree == 90) {
			entity.turnTo(90);
			entity.setLinearSpeed(entity.center().isu().new Vector(0, -speed));
		}

		// Déplacement vers la gauche
		else if (degree == 180) {
			entity.turnTo(180);
			entity.setLinearSpeed(entity.center().isu().new Vector(-speed, 0));
		}

		// Déplacement vers le bas
		else if (degree == 270) {
			entity.turnTo(270);
			entity.setLinearSpeed(entity.center().isu().new Vector(0, speed));
		}
	}

	@Override
	public void set(double x_cm, double y_cm) {

		// Positionnement direct dans le repère continu
		entity.setCoord(entity.center().isu().new Coord(x_cm, y_cm));
	}

	@Override
	public void collision(Entity e) {
		if (e == null) {
			return;
		}

		// Comportement par défaut : l'entité s'arrête en cas de collision
		entity.stop();
	}

	@Override
	public void done() {

		// Une action terminée remet l'entité à l'arrêt
		entity.stop();
	}

	@Override
	public void update(long elapsed) {

		// Rien à faire ici :
		// le déplacement est déjà appliqué par Entity.tick avec linearSpeed.
	}
}