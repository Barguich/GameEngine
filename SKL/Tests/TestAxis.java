package Tests;

import engine.Axis;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TestAxis {

	@Test
	void normalizeInt_DansLaCarte() {
		Axis axis = new Axis(true, 10);
		assertEquals(3, axis.normalize(3));
	}

	@Test
	void normalizeInt_TestModuloNegatif() {
		Axis axis = new Axis(true, 10);
		assertEquals(7, axis.normalize(-3));
	}

	@Test
	void normalizeInt_DehorsDeLaCarte() {
		Axis axis = new Axis(true, 10);
		assertEquals(2, axis.normalize(12));
	}

	@Test
	void normalizeInt_PasSurUnTore() {
		Axis axis = new Axis(false, 10);
		assertEquals(-3, axis.normalize(-3));
		assertEquals(42, axis.normalize(42));
	}

	@Test
	void normalizeDouble_DansLaCarte() {
		Axis axis = new Axis(true, 10.0);
		assertEquals(3.5, axis.normalize(3.5), 1e-9);
	}

	@Test
	void normalizeDouble_TestModuloNegatif() {
		Axis axis = new Axis(true, 10.0);
		assertEquals(7.5, axis.normalize(-2.5), 1e-9);
	}

	// === DISTANCE ===
	@Test
	void distance_Normal() {
		Axis axis = new Axis(true, 10);
		assertEquals(3.0, axis.distance(2.0, 5.0), 1e-9);
	}

	@Test
	void distance_PlusCourt() {
		Axis axis = new Axis(true, 10);
		assertEquals(2.0, axis.distance(1.0, 9.0), 1e-9);
	}

	@Test
	void distance_PlusCourtNormal() {
		Axis axis = new Axis(false, 10);
		assertEquals(8.0, axis.distance(1.0, 9.0), 1e-9);
	}
}
