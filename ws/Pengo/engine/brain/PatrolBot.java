package brain;

import model.Entity;
import model.Stunt;

public class PatrolBot extends Bot {

	// Le bot suit successivement les 4 directions cardinales
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

		// Tant qu'un déplacement est en cours, le bot ne prend pas
		// de nouvelle décision.
		if (moving)
			return;

		moving = true;

		// Le bot continue sa patrouille dans la direction courante.
		stunt.walk(directions[index]);
	}

	@Override
	public void collision(Entity e) {

		if (e == null)
			return;

		// Lorsqu'un obstacle est rencontré, le bot change
		// de direction pour poursuivre sa patrouille.
		moving = false;
		index = (index + 1) % directions.length;
	}

	@Override
	public void done() {

		// Le déplacement étant terminé, le bot pourra
		// choisir une nouvelle action au prochain tick.
		moving = false;
	}
}