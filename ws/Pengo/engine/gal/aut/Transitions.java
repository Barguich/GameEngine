package gal.aut;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gal_engine.State;

/**
 * Structure de stockage des transitions d'un automate.
 *
 * Les transitions sont regroupées par état source afin d'accélérer leur
 * recherche lors de l'exécution.
 *
 * Ainsi, lorsqu'un automate se trouve dans un état donné, il peut récupérer
 * directement les transitions sortantes sans parcourir l'ensemble de
 * l'automate.
 */
public class Transitions implements iTransitions {

	long serialVersionUID = 1L;

	// Associe chaque état source à la liste de ses transitions sortantes.
	private Map<State, List<Transition>> transitions;

	public Transitions() {
		transitions = new HashMap<>();
	}

	/**
	 * Ajoute une transition dans la structure.
	 *
	 * Si aucune liste n'existe encore pour l'état source, elle est créée
	 * automatiquement.
	 */
	public void add(Transition t) {
		State source = t.source();

		List<Transition> list = transitions.get(source);

		if (list == null) {
			list = new LinkedList<>();
			transitions.put(source, list);
		}

		list.add(t);
	}

	/**
	 * Retourne toutes les transitions dont l'état source correspond à l'état
	 * demandé.
	 *
	 * Une liste vide est retournée lorsqu'aucune transition n'est définie pour cet
	 * état.
	 */
	public List<Transition> get(State state) {
		List<Transition> list = transitions.get(state);

		if (list == null) {
			return new LinkedList<>();
		}

		return list;
	}
}