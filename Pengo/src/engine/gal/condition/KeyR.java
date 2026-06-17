package engine.gal.condition;

import engine.gal.Keyboard;
import engine.gal.arguments.Key;
import engine.gal.aut.iGALCondition;
import engine.model.Entity;

/**
 * @apiNote KeyR(K) : vraie si un événement « K relâchée » s'est produit.
 * @implNote consommé à l'évaluation : un seul relâchement déclenche une
 *           seule transition. Si plusieurs bots écoutent la même touche,
 *           le premier évalué consomme l'événement — limitation assumée,
 *           à revoir si le besoin apparaît.
 */
public class KeyR implements iGALCondition {

	private final Key key;

	public KeyR(Key key) {
		if (key == null)
			throw new IllegalArgumentException();
		this.key = key;
	}

	@Override
	public boolean eval(Entity e) {
		return Keyboard.self().consumeRelease(key);
	}
}
