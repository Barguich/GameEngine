package engine.gal.parser;

import gal.parser.Parser;
import gal.ast.AST;

public class Smoke {
	public static void main(String[] args) throws Exception {
		String path = args.length > 0 ? args[0] : "src/gal/demo/test/test.gal";
		AST ast = (AST) Parser.from_file(path);
		System.out.println("Parsé OK, " + ast.aut_list.size() + " automate(s)");
		System.out.println("Nom du premier: " + ast.aut_list.get(0).name);
	}
}
