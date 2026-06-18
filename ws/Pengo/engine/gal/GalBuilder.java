package gal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ast.*;
import ast.Automaton;
import ast.Transition;
import gal.action.*;
import gal.aut.*;
import gal.condition.*;
import parser.Parser;

public class GalBuilder implements iVisitor {
	public static List<gal.aut.Automaton> loadAutomata(String filename) throws Exception {
		AST ast = (AST) Parser.from_file(filename);
		GalBuilder builder = new GalBuilder();
		ast.accept(builder);
		return builder.result;
	}

	private List<gal.aut.Automaton> result; // liste des aut
	private List<gal.aut.Transition> attTransitions; // transition en cours
	private Map<String, gal_engine.State> states;
	private gal_engine.State currSrc; // src de la transition
	private boolean inActionContext;

	private gal_engine.State resolveState(String name) {
		gal_engine.State mem = states.get(name);
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
		gal_engine.State s = new gal_engine.State(mode, id);
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
		gal.aut.Automaton aut = new gal.aut.Automaton(
				automaton.name, (gal_engine.State) initial_state);
		for (gal.aut.Transition t : attTransitions)
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
		gal.aut.Transition aut = new gal.aut.Transition(
				currSrc,
				(iGALCondition) condition,
				(GALAction) action,
				(gal_engine.State) target_state);
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

			case "Step": {
				gal.arguments.Direction dir = gal.arguments.Direction.F;
				gal.arguments.Category cat = gal.arguments.Category.ANY;
				int nbStep = 1;

				for (Object p : parameters) {
					if (p instanceof gal.arguments.Direction) {
						dir = (gal.arguments.Direction) p;
					} else if (p instanceof gal.arguments.Category) {
						cat = (gal.arguments.Category) p;
					} else if (p instanceof Integer) {
						nbStep = (Integer) p;
					}
				}
				return new AtStep(dir, cat, nbStep);
			}

			case "Closest": {
				gal.arguments.Category cat = gal.arguments.Category.ANY;
				gal.arguments.Direction dir = null;

				for (Object p : parameters) {
					if (p instanceof gal.arguments.Category) {
						cat = (gal.arguments.Category) p;
					} else if (p instanceof gal.arguments.Direction) {
						dir = (gal.arguments.Direction) p;
					}
				}

				if (dir != null) {
					return new Closest(cat, dir);
				}
				return new Closest(cat);
			}

			case "Key": {
				gal.arguments.Key key = null;
				for (Object p : parameters) {
					if (p instanceof gal.arguments.Key) {
						key = (gal.arguments.Key) p;
					}
				}
				if (key != null) {
					return new KeyP(key);
				}
				throw new IllegalArgumentException("Condition Key sans paramètre Key valide");
			}

			default:
				throw new UnsupportedOperationException(
						"Condition GAL non encore supportée : " + fc.name);
		}
	}

	private GALAction buildAction(FunCall fc, List<Object> parameters) {
		switch (fc.name) {
			case "Move": {
				gal.arguments.Direction dir = gal.arguments.Direction.F;
				for (Object p : parameters) {
					if (p instanceof gal.arguments.Direction)
						dir = (gal.arguments.Direction) p;
				}
				return new Move(dir);
			}

			case "Turn": {
				gal.arguments.Direction dir = null;
				int angle = 90;
				for (Object p : parameters) {
					if (p instanceof gal.arguments.Direction) {
						dir = (gal.arguments.Direction) p;
					} else if (p instanceof Integer) {
						angle = (Integer) p;
					}
				}
				if (dir != null)
					return new Turn(dir);
				return new Turn(angle);
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
		if (binop.operator.equals("&")) {
			Conjunction conj = new Conjunction();
			conj.add((iGALCondition) left);
			conj.add((iGALCondition) right);
			return conj;
		} else if (binop.operator.equals("/")) {
			Disjunction disj = new Disjunction();
			disj.add((iGALCondition) left);
			disj.add((iGALCondition) right);
			return disj;
		}

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
		if (unop.operator.equals("!")) {
			return new Not((iGALCondition) expression);
		}

		throw new UnsupportedOperationException(
				"Opérateur unaire non encore supporté : " + unop.operator);
	}

	// === TERMINAUX ===

	@Override
	public Object visit(Direction dir) {
		return gal.arguments.Direction.canonical(dir.terminal.content);
	}

	@Override
	public Object visit(Category cat) {
		return gal.arguments.Category.canonical(cat.terminal.content);
	}

	@Override
	public Object visit(Key key) {
		return gal.arguments.Key.canonical(key.terminal.content);
	}

	@Override
	public Object visit(IntValue v) {
		return v.value;
	}

	@Override
	public Object visit(Underscore u) {
		return gal.arguments.Category.ANY;
	}

	@Override
	public Object visit(IntPercent per) {
		return per.value / 100.0;
	}

	@Override
	public Object visit(IntDegree deg) {
		return deg.value;
	}

	@Override
	public Object visit(Variable v) {
		return new UnsupportedOperationException("Variable non encore supportée");
	}
}
