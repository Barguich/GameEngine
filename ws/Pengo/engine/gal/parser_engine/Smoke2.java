package gal.parser_engine;

import java.util.List;

import gal.aut.Automaton;

public class Smoke2 {
	public static void main(String[] args) throws Exception {
		String path = args.length > 0 ? args[0] : "Pengo/src/gal/demo/test/test.gal";
		List<Automaton> autos = GalBuilder.loadAutomata(path);
		System.out.println("Construit OK : " + autos.size() + " automate(s)");
		for (Automaton a : autos) {
			System.out.println("  - " + a.name() + " (initial: " + a.initial() + ")");
		}
	}
}
