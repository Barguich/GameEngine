package engine.gal.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import gal.ast.*;
import gal.parser.Parser;

import engine.gal.action.GALAction;
import engine.gal.action.Move;
import engine.gal.aut.iGALCondition;
import engine.gal.condition.True;

public class GalBuilder implements iVisitor {
	public static List<engine.gal.aut.Automaton> loadAutomata(String filename) throws Exception {
		AST ast = (AST) Parser.from_file(filename);
		GalBuilder builder = new GalBuilder();
		ast.accept(builder);
		return builder.result;
	}

	private List<engine.gal.aut.Automaton> result; // liste des aut
	private List<engine.gal.aut.Transition> attTransitions; // transition en cours
	private Map<String, engine.gal.State> states;
	private engine.gal.State currSrc; // src de la transition
	private boolean inActionContext;

	private engine.gal.State resolveState(String name) {
		engine.gal.State mem = states.get(name);
		if (mem != null)
			return mem;
		String mode = name;
		int id = 0;
		int us = name.lastIndexOf('_');
		if (us > 0 && us < name.length() - 1) {
			try {
				id = Integer.parseInt(name.substring(us + 1));
				mode = name.substring(0, us);
			} catch (NumberFormatException err) {
			}
		}
		engine.gal.State s = new engine.gal.State(mode, id);
		states.put(name, s);
		return s;
	}

	@Override
	public void enter(AST ast) {
		result = new LinkedList<>();
	}

	@Override
	public void exit(AST ast) {
	}

	@Override
	public Object build(AST ast, List<Object> automata) {
		return result;
	}

	// === AUTOMATON ===
	@Override
	public void enter(Automaton automaton) {
		states = new HashMap<>();
		attTransitions = new LinkedList<>();
	}

	@Override
	public void exit(Automaton automaton) {
	}

	@Override
	public Object build(Automaton automaton, Object initial_state, List<Object> modes) {
		engine.gal.aut.Automaton aut = new engine.gal.aut.Automaton(
				automaton.name, (engine.gal.State) initial_state);
		for (engine.gal.aut.Transition t : attTransitions)
			aut.add(t);
		result.add(aut);
		return aut;
	}

	// === MODE ===
	@Override
	public void enter(Mode mode) {
		currSrc = resolveState(mode.state.name);
	}

	@Override
	public void visit(Mode mode) {
	}

	@Override
	public void exit(Mode mode) {
	}

	@Override
	public Object build(Mode mode, Object source_state, Object behaviour) {
		return null;
	}

	// === BEHAVIOUR ===
	@Override
	public Object visit(Behaviour behaviour, List<Object> transitions) {
		return null;
	}

	// === TRANSITION ===
	@Override
	public void enter(Transition transition) {
	}

	@Override
	public void exit(Transition transition) {
	}

	@Override
	public Object build(Transition transition, Object condition, Object action, Object target_state) {
		engine.gal.aut.Transition aut = new engine.gal.aut.Transition(
				currSrc,
				(iGALCondition) condition,
				(GALAction) action,
				(engine.gal.State) target_state);
		attTransitions.add(aut);
		return aut;
	}

	// === STATE ===
	@Override
	public Object visit(State state) {
		return resolveState(state.name);
	}

	// === CONDITION ===
	@Override
	public void enter(Condition condition) {
	}

	@Override
	public void exit(Condition condition) {
	}

	@Override
	public Object build(Condition condition, Object expression) {
		return expression;
	}

	// === ACTIONS ===
	@Override
	public void enter(Actions action) {
		inActionContext = true;
	}

	@Override
	public void visit(Actions action) {
	}

	@Override
	public void exit(Actions action) {
		inActionContext = false;
	}

	@Override
	public Object build(Actions action, String operator, List<Object> funcalls) {
		if (funcalls.isEmpty())
			return null;
		if (funcalls.size() == 1)
			return funcalls.get(0);
		throw new UnsupportedOperationException(
				"Actions multiples non encore supportées (opérateur: " + operator + ")");
	}

	// === FUNCALL ===
	@Override
	public void enter(FunCall funcall) {
	}

	@Override
	public void visit(FunCall funcall) {
	}

	@Override
	public void exit(FunCall funcall) {
	}

	@Override
	public Object build(FunCall funcall, List<Object> parameters) {
		if (inActionContext)
			return buildAction(funcall, parameters);
		return buildCondition(funcall, parameters);
	}

	private iGALCondition buildCondition(FunCall fc, List<Object> parameters) {
		switch (fc.name) {
			case "True":
				return new True();
			default:
				throw new UnsupportedOperationException(
						"Condition GAL non encore supportée : " + fc.name);
		}
	}

	private GALAction buildAction(FunCall fc, List<Object> parameters) {
		switch (fc.name) {
			case "Move": {
				engine.gal.arguments.Direction dir = engine.gal.arguments.Direction.F;
				for (Object p : parameters) {
					if (p instanceof engine.gal.arguments.Direction)
						dir = (engine.gal.arguments.Direction) p;
				}
				return new Move(dir);
			}
			default:
				throw new UnsupportedOperationException(
						"Action GAL non encore supportée : " + fc.name);
		}
	}

	// === BINOP ===

	@Override
	public void enter(BinaryOp binop) {
	}

	@Override
	public void visit(BinaryOp binop) {
	}

	@Override
	public void exit(BinaryOp binop) {
	}

	@Override
	public Object build(BinaryOp binop, Object left, Object right) {
		throw new UnsupportedOperationException(
				"Opérateur binaire non encore supporté : " + binop.operator);
	}

	// === UNOP ===

	@Override
	public void enter(UnaryOp unop) {
	}

	@Override
	public void exit(UnaryOp unop) {
	}

	@Override
	public Object build(UnaryOp unop, Object expression) {
		throw new UnsupportedOperationException(
				"Opérateur unaire non encore supporté : " + unop.operator);
	}

	// === TERMINAUX ===

	@Override
	public Object visit(Direction dir) {
		return engine.gal.arguments.Direction.canonical(dir.terminal.content);
	}

	@Override
	public Object visit(Category cat) {
		throw new UnsupportedOperationException("Category non encore supportée");
	}

	@Override
	public Object visit(Key key) {
		throw new UnsupportedOperationException("Key non encore supportée");
	}

	@Override
	public Object visit(IntValue v) {
		throw new UnsupportedOperationException("IntValue non encore supporté");
	}

	@Override
	public Object visit(IntPercent per) {
		throw new UnsupportedOperationException("IntPercent non encore supporté");
	}

	@Override
	public Object visit(IntDegree deg) {
		throw new UnsupportedOperationException("IntDegree non encore supporté");
	}

	@Override
	public Object visit(Underscore u) {
		throw new UnsupportedOperationException("Underscore non encore supporté");
	}

	@Override
	public Object visit(Variable v) {
		throw new UnsupportedOperationException("Variable non encore supportée");
	}
}
