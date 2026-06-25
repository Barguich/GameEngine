package gal.action;

import java.util.List;
import model.Entity;

/**
 * Action composite permettant de choisir une action parmi plusieurs
 * possibilités selon des probabilités définies dans le fichier GAL.
 *
 * Exemple :
 *
 * 70% Move(F) 20% Turn(L) 10% Wait()
 *
 * Cette action permet d'introduire un comportement moins déterministe dans les
 * automates.
 */
public class WeightedChoice extends GALAction {

	// Liste des actions candidates.
	private final List<GALAction> actions;

	// Pourcentages associés aux actions.
	// Une valeur null signifie que le pourcentage sera calculé automatiquement.
	private final List<Integer> percents;

	public WeightedChoice(List<GALAction> actions, List<Integer> percents) {
		this.actions = actions;
		this.percents = percents;
	}

	@Override
	public boolean exec(Entity e) {

		// Sélection aléatoire d'une action selon les poids calculés.
		GALAction chosen = pick();

		return chosen != null && chosen.exec(e);
	}

	/**
	 * Sélectionne une action en tenant compte des poids.
	 *
	 * Plus le poids d'une action est élevé, plus elle a de chances d'être choisie.
	 */
	private GALAction pick() {
		int n = actions.size();

		if (n == 0) {
			return null;
		}

		if (n == 1) {
			return actions.get(0);
		}

		double[] weights = weights();

		double total = 0;
		for (double w : weights) {
			total += w;
		}

		/*
		 * Cas de sécurité : si tous les poids sont nuls, on effectue un choix uniforme
		 * parmi les actions.
		 */
		if (total <= 0) {
			return actions.get((int) (Math.random() * n));
		}

		/*
		 * Tirage aléatoire dans l'intervalle [0 ; total]. Chaque action occupe une
		 * portion de cet intervalle proportionnelle à son poids.
		 */
		double r = Math.random() * total;

		double acc = 0;

		for (int i = 0; i < n; i++) {
			acc += weights[i];

			if (r <= acc) {
				return actions.get(i);
			}
		}

		// Sécurité contre les erreurs d'arrondi.
		return actions.get(n - 1);
	}

	/**
	 * Calcule les poids effectifs utilisés pour le tirage.
	 *
	 * Si certains pourcentages sont absents (null), le pourcentage restant est
	 * réparti équitablement entre les actions concernées.
	 */
	private double[] weights() {
		int n = actions.size();

		double[] weights = new double[n];

		/*
		 * Aucun pourcentage fourni : toutes les actions ont la même probabilité.
		 */
		if (percents == null || percents.size() != n) {
			for (int i = 0; i < n; i++) {
				weights[i] = 1;
			}
			return weights;
		}

		int explicitSum = 0;
		int explicitCount = 0;

		// Calcul de la somme des pourcentages explicitement fournis.
		for (Integer p : percents) {
			if (p != null) {
				explicitSum += p;
				explicitCount++;
			}
		}

		int implicitCount = n - explicitCount;

		/*
		 * Répartition automatique du pourcentage restant entre les actions ne possédant
		 * pas de valeur explicite.
		 */
		double implicitShare = (implicitCount > 0) ? (100.0 - explicitSum) / implicitCount : 0;

		for (int i = 0; i < n; i++) {
			Integer p = percents.get(i);

			double w = (p != null) ? p : implicitShare;

			weights[i] = Math.max(0, w);
		}

		return weights;
	}
}