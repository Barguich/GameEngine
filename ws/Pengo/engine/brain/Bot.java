package brain;

import model.Entity;
import model.Stunt;

public abstract class Bot {

	protected Stunt stunt;

	public Bot(Stunt stunt) {
		this.stunt = stunt;
	}

	public abstract void think();

	public void collision(Entity e) {
	}

	public void done() {
	}

}
