package gal_engine;

import gal.arguments.Key;

/**
 * Gestionnaire centralisé du clavier utilisé par les automates GAL.
 *
 * Cette classe mémorise l'état courant des touches du clavier afin que les
 * conditions GAL puissent interroger les entrées utilisateur indépendamment de
 * la couche graphique.
 */
public class Keyboard {

	// Instance unique partagée par tout le moteur.
	private static Keyboard self;

	public static Keyboard self() {
		if (self == null) {
			self = new Keyboard();
		}
		return self;
	}

	// down[i] indique si la touche i est actuellement maintenue enfoncée.
	private final boolean[] down = new boolean[65536];

	// releasedPending[i] indique qu'un relâchement de touche
	// a été détecté mais pas encore consommé par un automate.
	private final boolean[] releasedPending = new boolean[65536];

	/**
	 * Constructeur privé : l'accès se fait uniquement via Keyboard.self().
	 */
	private Keyboard() {
	}

	/**
	 * Signale qu'une touche vient d'être enfoncée.
	 *
	 * La touche reste considérée comme active tant qu'un événement de relâchement
	 * n'est pas reçu.
	 */
	public void pressed(int keyCode) {
		if (keyCode >= 0 && keyCode < down.length) {
			down[keyCode] = true;
		}
	}

	/**
	 * Signale qu'une touche vient d'être relâchée.
	 *
	 * L'information est mémorisée afin qu'un automate GAL puisse détecter
	 * l'événement une seule fois.
	 */
	public void released(int keyCode) {
		if (keyCode >= 0 && keyCode < down.length) {
			down[keyCode] = false;
			releasedPending[keyCode] = true;
		}
	}

	/**
	 * Indique si une touche est actuellement maintenue enfoncée.
	 *
	 * Cette méthode est utilisée par les conditions du type : Key(A), Key(Space),
	 * etc.
	 */
	public boolean isDown(Key k) {
		return down[k.keyCode()];
	}

	/**
	 * Consomme un événement de relâchement.
	 *
	 * Si la touche a été relâchée depuis le dernier appel, la méthode retourne true
	 * puis efface l'événement.
	 *
	 * Cela permet d'éviter de traiter plusieurs fois le même relâchement de touche.
	 */
	public boolean consumeRelease(Key k) {
		boolean released = releasedPending[k.keyCode()];
		releasedPending[k.keyCode()] = false;
		return released;
	}
}