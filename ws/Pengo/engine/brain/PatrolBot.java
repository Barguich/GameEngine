package brain;

import model.Entity;
import model.Stunt;

public class PatrolBot extends Bot {

	private int[] directions = { 0, 90, 180, 270 };
	private int index;
	private boolean moving;

	public PatrolBot(Stunt stunt) {
		super(stunt);
		this.index = 0;
		this.moving = false;
	}

	@Override
	public void think() {
		if (moving)
			return;

		moving = true;
		stunt.walk(directions[index]);
		System.out.println("PatrolBot think");
	}

	@Override
	public void collision(Entity e) {
		if (e == null) {
			return;
		}

		System.out.println("PatrolBot change direction");

		moving = false;
		index = (index + 1) % directions.length;
	}

	@Override
	public void done() {
		moving = false;
	}
}
