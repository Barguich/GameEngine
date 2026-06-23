package pengo.model;

import geometry.Grid;
import model.Model;
import pengo.brain.PengoBots;

public class EnemyBlock extends IceBlock {

	public EnemyBlock() {
		super();
	}

	@Override
	public void breakBlock() {
		Grid.Position spawnPos = null;
		if (position() != null) {
			spawnPos = copyPos(position());
		}
		Model m = this.model;

		super.breakBlock();

		if (spawnPos == null || m == null) {
			return;
		}

		Enemy enemy = new Enemy();
		enemy.setSize(spawnPos.grid().new Dimension(1, 1));
		enemy.setPosition(spawnPos);
		m.add(enemy);

		if (m instanceof PengoModel pm) {
			PengoBots.configureEntity(pm, enemy);
			PengoBots.attachEnemyAvatar(enemy);
		}

		System.out.println("ENEMY BLOCK at " + spawnPos);
	}

	private static Grid.Position copyPos(Grid.Position p) {
		return p.grid().new Position(p.x(), p.y());
	}
}
