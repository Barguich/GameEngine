package behavior;

import engine.Entity;

public class FleeBot extends Bot {
	private boolean moving;
	private Entity enemy;

	public FleeBot(BasicStunt stunt, Entity enemy) {
		super(stunt);
		this.enemy = enemy;
		 this.moving = false;
	}

	@Override
	public void think() {

		int ex = stunt.entity.position().x();
		int ey = stunt.entity.position().y();

		int tx = enemy.position().x();
		int ty = enemy.position().y();
		moving = true;

		if (tx > ex)
			stunt.walk(180);

		else if (tx < ex)
			stunt.walk(0);

		else if (ty > ey)
			stunt.walk(90);

		else if (ty < ey)
			stunt.walk(270);
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