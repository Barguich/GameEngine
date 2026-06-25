package geometry_enginetest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import geometry.Vector;


/**
 * Vector (engine.geometry) est une classe pure.
 * add/unity mutent l'instance ; scaled/turned renvoient une copie.
 */
class VectorTest {

	private static final double DELTA = 1e-9;

	@Test
	void add_mute_le_vecteur() {
		Vector v = new Vector(1, 1);
		v.add(new Vector(2, 3));
		assertEquals(3, v.x(), DELTA);
		assertEquals(4, v.y(), DELTA);
	}

	@Test
	void scaled_renvoie_une_copie_mise_a_l_echelle() {
		Vector v = new Vector(2, 3);
		Vector s = v.scaled(2);
		assertEquals(4, s.x(), DELTA);
		assertEquals(6, s.y(), DELTA);
		// original inchangé
		assertEquals(2, v.x(), DELTA);
	}

	@Test
	void dot_produit_scalaire() {
		Vector a = new Vector(1, 2);
		Vector b = new Vector(3, 4);
		assertEquals(11, a.dot(b), DELTA);
	}

	@Test
	void norm_pythagore() {
		assertEquals(5, new Vector(3, 4).norm(), DELTA);
	}

	@Test
	void unity_normalise_le_vecteur() {
		Vector v = new Vector(3, 4);
		v.unity();
		assertEquals(0.6, v.x(), DELTA);
		assertEquals(0.8, v.y(), DELTA);
		assertEquals(1.0, v.norm(), DELTA);
	}

	@Test
	void turned_90_degres_renvoie_une_copie_tournee() {
		Vector v = new Vector(1, 0);
		Vector t = v.turned(90);
		assertEquals(0, t.x(), DELTA);
		assertEquals(1, t.y(), DELTA);
	}

	/**
	 * Test de caractérisation : unity() ne protège PAS contre la norme nulle
	 * (division par zéro), contrairement à ISU.Vector.unity(). On documente le
	 * comportement réel (NaN) sans corriger le moteur.
	 */
	@Test
	void unity_sur_vecteur_nul_reste_nul() {
	    Vector v = new Vector(0, 0);

	    v.unity();

	    assertEquals(0, v.x(), DELTA);
	    assertEquals(0, v.y(), DELTA);
	    assertEquals(0, v.norm(), DELTA);
	}
	@Test
	void scaled_avec_zero_donne_vecteur_nul() {
	    Vector v = new Vector(2, 3);

	    Vector s = v.scaled(0);

	    assertEquals(0, s.x(), DELTA);
	    assertEquals(0, s.y(), DELTA);
	}
	@Test
	void scaled_avec_nombre_negatif_inverse_le_sens() {
	    Vector v = new Vector(2, -3);

	    Vector s = v.scaled(-2);

	    assertEquals(-4, s.x(), DELTA);
	    assertEquals(6, s.y(), DELTA);
	}
	@Test
	void turned_180_degres() {
	    Vector v = new Vector(1, 0);

	    Vector t = v.turned(180);

	    assertEquals(-1, t.x(), DELTA);
	    assertEquals(0, t.y(), DELTA);
	}
	@Test
	void dot_de_deux_vecteurs_perpendiculaires_vaut_zero() {
	    Vector a = new Vector(1, 0);
	    Vector b = new Vector(0, 1);

	    assertEquals(0, a.dot(b), DELTA);
	}
}
