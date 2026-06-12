package engine;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import engine.collision.Box;
import engine.collision.Circle;
import engine.collision.Rect;
import engine.collision.iShape;
import engine.collision.Bounding;

/**
 * Bounding agrège des iShape. On le teste avec un faux iShape contrôlable
 * (FakeShape) plutôt qu'avec Circle/Rect, pour isoler la logique d'agrégation
 * et éviter la dépendance à Game/ISU.
 */
class BoundingTest {

	/**
	 * iShape de test : intersects(iShape) renvoie un drapeau fixé,
	 * box() renvoie une boîte fixée. Les surcharges Circle/Rect ne sont
	 * pas sollicitées par Bounding.
	 */
	private static final class FakeShape implements iShape {
		private final boolean hits;
		private final Box box;

		FakeShape(boolean hits, Box box) {
			this.hits = hits;
			this.box = box;
		}

		@Override
		public boolean intersects(iShape shape) {
			return hits;
		}

		@Override
		public boolean intersects(Circle circle) {
			return hits;
		}

		@Override
		public boolean intersects(Rect rect) {
			return hits;
		}

		@Override
		public Box box() {
			return box;
		}

	}

	@Test
	void un_bounding_vide_n_intersecte_rien() {
		Bounding empty = new Bounding();
		assertFalse(empty.intersects(new FakeShape(true, new Box(0, 0, 1, 1))));
	}

	@Test
	void intersects_iShape_vrai_si_une_forme_touche() {
		Bounding b = new Bounding();
		b.add(new FakeShape(false, new Box(0, 0, 1, 1)));
		b.add(new FakeShape(true, new Box(2, 2, 3, 3)));
		assertTrue(b.intersects(new FakeShape(true, new Box(0, 0, 1, 1))));
	}

	@Test
	void intersects_iShape_faux_si_aucune_forme_ne_touche() {
		Bounding b = new Bounding();
		b.add(new FakeShape(false, new Box(0, 0, 1, 1)));
		b.add(new FakeShape(false, new Box(2, 2, 3, 3)));
		assertFalse(b.intersects(new FakeShape(false, new Box(0, 0, 1, 1))));
	}

	@Test
	void intersects_bounding_vrai_si_un_couple_de_formes_touche() {
		Bounding b1 = new Bounding();
		b1.add(new FakeShape(true, new Box(0, 0, 1, 1)));

		Bounding b2 = new Bounding();
		b2.add(new FakeShape(false, new Box(5, 5, 6, 6)));

		assertTrue(b1.intersects(b2));
	}

	@Test
	void box_est_l_union_des_boites_des_formes() {
		Bounding b = new Bounding();
		b.add(new FakeShape(false, new Box(0, 0, 2, 2)));
		b.add(new FakeShape(false, new Box(3, 1, 5, 6)));

		Box union = b.box();
		assertEquals(0, union.xmin(), 1e-9);
		assertEquals(0, union.ymin(), 1e-9);
		assertEquals(5, union.xmax(), 1e-9);
		assertEquals(6, union.ymax(), 1e-9);
	}
}
