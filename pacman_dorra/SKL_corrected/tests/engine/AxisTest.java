package engine;

import org.junit.jupiter.api.Test;
import engine.Axis;

public class AxisTest {

    private static final double EPS = 1e-6;

    private void verifdouble(double expected, double result, String msg) {
        if (Math.abs(expected - result) > EPS) {
            throw new Error(msg + " expected " + expected + " but got " + result);
        }
    }

    @Test
    public void test00NormalizeTorus() {
        Axis axis = new Axis(true, 5);

        verifdouble(0, axis.normalize(0), "normalize 0");
        verifdouble(4, axis.normalize(4), "normalize 4");
        verifdouble(0, axis.normalize(5), "normalize 5");
        verifdouble(1, axis.normalize(6), "normalize 6");
        verifdouble(4, axis.normalize(-1), "normalize -1");
        verifdouble(3, axis.normalize(-2), "normalize -2");
    }

    @Test
    public void test01NormalizeNoTorus() {
        Axis axis = new Axis(false, 5);

        verifdouble(6, axis.normalize(6), "normalize 6");
        verifdouble(-1, axis.normalize(-1), "normalize -1");
    }

    @Test
    public void test02Distance() {
        Axis torus = new Axis(true, 10);

        verifdouble(1, torus.distance(0, 1), "distance 0 to 1");
        verifdouble(1, torus.distance(0, 9), "distance 0 to 9");
        verifdouble(2, torus.distance(1, 9), "distance 1 to 9");
        verifdouble(5, torus.distance(0, 5), "half distance");

        Axis line = new Axis(false, 5);

        verifdouble(4, line.distance(0, 4), "line distance");
        verifdouble(4, line.distance(4, 0), "line distance reverse");
    }

    @Test
    public void test03NormalizeDouble() {
        Axis axis = new Axis(true, 10.0);

        verifdouble(0.5, axis.normalize(10.5), "normalize 10.5");
        verifdouble(9.5, axis.normalize(-0.5), "normalize -0.5");

        Axis line = new Axis(false, 10.0);

        verifdouble(10.5, line.normalize(10.5), "line normalize 10.5");
        verifdouble(-0.5, line.normalize(-0.5), "line normalize -0.5");
    }
}