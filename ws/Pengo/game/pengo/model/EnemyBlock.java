package pengo.model;

import geometry.Grid;
import model.Model;
import pengo.brain.PengoBots;

/**
 * Variante d'IceBlock contenant un ennemi.
 *
 * Tant qu'il n'est pas détruit, ce bloc se comporte comme un bloc de glace
 * classique.
 *
 * Lorsqu'il casse, un SnoBee apparaît à son emplacement.
 */
public class EnemyBlock extends IceBlock {

	public EnemyBlock() {
		super();
	}

	/**
	 * Détruit le bloc puis fait apparaître un ennemi sur la case qu'il occupait.
	 *
	 * La position est sauvegardée avant l'appel à super.breakBlock() car cette
	 * méthode retire le bloc du modèle.
	 */
	@Override
	public void breakBlock() {

		Grid.Position spawnPos = null;

		if (position() != null) {
			spawnPos = copyPos(position());
		}

		Model m = this.model;

		// Comportement normal de destruction d'un IceBlock.
		super.breakBlock();

		// Impossible de créer un ennemi si la position
		// ou le modèle n'existent plus.
		if (spawnPos == null || m == null) {
			return;
		}

		// Création du SnoBee.
		Enemy enemy = new Enemy();

		enemy.setSize(spawnPos.grid().new Dimension(1, 1));

		enemy.setPosition(spawnPos);

		m.add(enemy);

		/*
		 * Si on est dans une partie Pengo, on configure également : - son automate, -
		 * son avatar graphique.
		 */
		if (m instanceof PengoModel pm) {
			PengoBots.configureEntity(pm, enemy);
			PengoBots.attachEnemyAvatar(enemy);
		}

		System.out.println("ENEMY BLOCK at " + spawnPos);
	}

	/**
	 * Crée une copie indépendante d'une position.
	 *
	 * Cela évite de conserver une référence vers un objet qui pourrait être modifié
	 * après la destruction du bloc.
	 */
	private static Grid.Position copyPos(Grid.Position p) {
		return p.grid().new Position(p.x(), p.y());
	}
}