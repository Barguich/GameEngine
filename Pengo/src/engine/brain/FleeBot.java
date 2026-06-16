package engine.brain;

import engine.model.BasicStunt;
import engine.model.Entity;

public class FleeBot extends Bot {

	private boolean moving;
	private Entity enemy;

	public FleeBot(BasicStunt stunt, Entity enemy) {
		super(stunt);

		assert enemy != null;

		this.enemy = enemy;
		this.moving = false;
	}

	@Override
	public void think() {
		if (moving) {
			return;
		}

		Entity me = stunt.entity();

		int ex = me.position().x();
		int ey = me.position().y();

		int tx = enemy.position().x();
		int ty = enemy.position().y();

		moving = true;

		if (tx > ex) {
			stunt.walk(180);
		} else if (tx < ex) {
			stunt.walk(0);
		} else if (ty > ey) {
			stunt.walk(270);
		} else if (ty < ey) {
			stunt.walk(90);
		} else {
			moving = false;
		}
	}

	@Override
	public void done() {
		moving = false;
	}

	@Override
	public void collision(Entity e) {
		moving = false;
	}
}
