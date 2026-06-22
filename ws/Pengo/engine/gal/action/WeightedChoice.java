package gal.action;

import java.util.List;
import model.Entity;

/**
 * Action GAL représentant un choix probabiliste entre plusieurs actions
 * (ex: "50%Move(F) / 25%Turn(L) / 25%Turn(R)").
 *
 * IMPORTANT : contrairement à un tirage fait une seule fois à la construction
 * de l'automate (au moment du parsing), cette classe retire un tirage à
 * CHAQUE appel de exec(), donc à chaque tick où la transition est tentée.
 * C'est le comportement attendu de GAL : le pourcentage s'applique à chaque
 * exécution de la transition, pas une fois pour toutes au chargement du .gal.
 */
public class WeightedChoice extends GALAction {

	private final List<GALAction> actions;
	private final List<Integer> percents; // même taille que actions, null = implicite

	public WeightedChoice(List<GALAction> actions, List<Integer> percents) {
		this.actions = actions;
		this.percents = percents;
	}

	@Override
	public boolean exec(Entity e) {
		GALAction chosen = pick();
		if (chosen == null)
			return false;
		return chosen.exec(e);
	}

	private GALAction pick() {
		int n = actions.size();
		if (n == 0)
			return null;
		if (n == 1)
			return actions.get(0);

		if (percents == null || percents.size() != n) {
			int i = (int) (Math.random() * n);
			return actions.get(i);
		}

		int explicitSum = 0;
		int nbExplicit = 0;
		for (Integer p : percents) {
			if (p != null) {
				explicitSum += p;
				nbExplicit++;
			}
		}

		int remaining = 100 - explicitSum;
		int nbImplicit = n - nbExplicit;
		double implicitShare = (nbImplicit > 0) ? (double) remaining / nbImplicit : 0;

		double[] weights = new double[n];
		double total = 0;
		for (int i = 0; i < n; i++) {
			Integer p = percents.get(i);
			double w = (p != null) ? p : implicitShare;
			if (w < 0) w = 0;
			weights[i] = w;
			total += w;
		}

		if (total <= 0) {
			int i = (int) (Math.random() * n);
			return actions.get(i);
		}

		double r = Math.random() * total;
		double acc = 0;
		for (int i = 0; i < n; i++) {
			acc += weights[i];
			if (r <= acc) {
				return actions.get(i);
			}
		}
		return actions.get(n - 1);
	}
}