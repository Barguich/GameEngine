package engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/**
 * Game porte un état STATIQUE (game/isu/grid) : un seul Game vivant à la fois.
 * Chaque constructeur réinitialise ces statiques, donc on reconstruit dans
 * chaque test plutôt que de partager une instance.
 */
class GameTest {

	private static final double DELTA = 1e-9;

	@Test
	void constructeur_en_cellules_calcule_les_cm() {
		Game g = new Game(20, 10);
		assertEquals(20, g.width_ncell);
		assertEquals(10, g.height_ncell);
		assertEquals(20 * 3.7, g.width_cm, DELTA);
		assertEquals(10 * 3.7, g.height_cm, DELTA);
	}

	@Test
	void constructeur_en_cm_calcule_les_cellules_par_troncature() {
		Game g = new Game(100.0, 50.0);
		assertEquals(100.0, g.width_cm, DELTA);
		assertEquals(50.0, g.height_cm, DELTA);
		// (int)(100 / 3.7) = 27 ; (int)(50 / 3.7) = 13
		assertEquals(27, g.width_ncell);
		assertEquals(13, g.height_ncell);
	}

	@Test
	void les_accesseurs_statiques_pointent_sur_la_derniere_instance() {
		Game g = new Game(20, 20);
		assertSame(g, Game.game());
		assertNotNull(Game.isu());
		assertNotNull(Game.grid());
	}

	@Test
	void les_constantes_du_moteur() {
		Game g = new Game(20, 20);
		assertEquals(3.7, g.cmPerCell, DELTA);
		assertEquals(2, g.pixelPerCm);
		assertEquals(true, g.torusOnXaxis);
		assertEquals(true, g.torusOnYaxis);
	}
}
