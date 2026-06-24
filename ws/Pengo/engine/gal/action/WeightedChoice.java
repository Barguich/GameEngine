package gal.action;

import java.util.List;
import model.Entity;

public class WeightedChoice extends GALAction {

	private final List<GALAction> actions;
	private final List<Integer> percents;

	public WeightedChoice(List<GALAction> actions, List<Integer> percents) {
		this.actions = actions;
		this.percents = percents;
	}

	@Override
	public boolean exec(Entity e) {
		GALAction chosen = pick();
		return chosen != null && chosen.exec(e);
	}

	private GALAction pick() {
		int n = actions.size();
		if (n == 0)
			return null;
		if (n == 1)
			return actions.get(0);

		double[] weights = weights();
		double total = 0;
		for (double w : weights)
			total += w;

		if (total <= 0) {
			return actions.get((int) (Math.random() * n));
		}

		double r = Math.random() * total;
		double acc = 0;
		for (int i = 0; i < n; i++) {
			acc += weights[i];
			if (r <= acc)
				return actions.get(i);
		}
		return actions.get(n - 1);
	}

	private double[] weights() {
		int n = actions.size();
		double[] weights = new double[n];

		if (percents == null || percents.size() != n) {
			for (int i = 0; i < n; i++)
				weights[i] = 1;
			return weights;
		}

		int explicitSum = 0, explicitCount = 0;
		for (Integer p : percents) {
			if (p != null) {
				explicitSum += p;
				explicitCount++;
			}
		}
		int implicitCount = n - explicitCount;
		double implicitShare = (implicitCount > 0)
				? (100.0 - explicitSum) / implicitCount
				: 0;

		for (int i = 0; i < n; i++) {
			Integer p = percents.get(i);
			double w = (p != null) ? p : implicitShare;
			weights[i] = Math.max(0, w);
		}
		return weights;
	}
}
