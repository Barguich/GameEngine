package brain;

import model.Entity;
import model.Stunt;

public abstract class Bot {

	// Permet au bot de contrôler son entité
	protected Stunt stunt;

	public Bot(Stunt stunt) {
		this.stunt = stunt;
	}

	// Comportement du bot exécuté à chaque tick
	public abstract void think();

	// Réaction à une collision
	public void collision(Entity e) {
	}

	// Notification de fin d'action
	public void done() {
	}

}