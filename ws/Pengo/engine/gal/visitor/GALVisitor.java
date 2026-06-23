package gal.visitor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import ast.*;
import gal.action.*;
import gal.aut.*;
import gal.aut.Automaton;
import gal.aut.Transition;
import gal.condition.*;

/**
 * Visiteur AST → Automaton GAL.
 *
 * FIX choix probabiliste entre actions (ex: "70%Move(F) / 15%Turn(L) /
 * 15%Turn(R)") :
 * - Le pourcentage est attaché à chaque FunCall (funcall.percent()), pas à
 * l'objet GALAction qu'on construit dans build(FunCall...).
 * - On capture donc funcall.percent() dans exit(FunCall) au fil de la visite,
 * dans une liste parallèle pendingPercents.
 * - build(Actions...) consomme ensuite cette liste pour faire le tirage
 * pondéré, puis la vide pour ne pas polluer le prochain groupe d'actions.
 */
public class GALVisitor implements iVisitor {

	// ── Cache d'états (unicité par nom) ──────────────────────────────────────
	private final Map<String, gal_engine.State> states = new HashMap<>();

	private gal_engine.State state(String name) {
		return states.computeIfAbsent(name, n -> new gal_engine.State(n, 0));
	}

	// ── Terminaux ────────────────────────────────────────────────────────────

	@Override
	public Object visit(ast.Category cat) {
		return gal.arguments.Category.canonical(cat.toString());
	}

	@Override
	public Object visit(ast.Direction dir) {
		return gal.arguments.Direction.canonical(dir.toString());
	}

	@Override
	public Object visit(ast.Key key) {
		return gal.arguments.Key.canonical(key.toString());
	}

	@Override
	public Object visit(ast.IntValue v) {
		return v.value;
	}

	@Override
	public Object visit(ast.IntPercent per) {
		return per.value;
	}

	@Override
	public Object visit(ast.IntDegree deg) {
		return deg.value;
	}

	@Override
	public Object visit(ast.Underscore u) {
		return null;
	}

	@Override
	public Object visit(ast.Variable v) {
		return v;
	}

	// ── État ─────────────────────────────────────────────────────────────────

	@Override
	public Object visit(ast.State state) {
		if (state.name == null || state.name.isEmpty()) {
			return null;
		}
		return state(state.name);
	}

	// ── FunCall ──────────────────────────────────────────────────────────────

	// Pourcentages capturés au fil des FunCall visités pour le groupe d'actions
	// courant. build(Actions...) les consomme dans l'ordre puis vide la liste.
	private final List<Integer> pendingPercents = new LinkedList<>();

	@Override
	public void enter(FunCall funcall) {
	}

	@Override
	public void visit(FunCall funcall) {
	}

	@Override
	public void exit(FunCall funcall) {
		pendingPercents.add(extractPercent(funcall));
	}

	/**
	 * Extrait le pourcentage d'un FunCall de façon défensive : selon la version
	 * du parseur, funcall.percent() peut renvoyer un int, un Integer (null si
	 * absent), ou une String ("70" / "" si absent). On normalise vers
	 * Integer (null = pas de pourcentage explicite).
	 */
	private Integer extractPercent(FunCall funcall) {
		try {
			Object p = funcall.percent();
			if (p == null)
				return null;
			if (p instanceof Integer)
				return (Integer) p;
			if (p instanceof Number)
				return ((Number) p).intValue();
			String s = p.toString().trim();
			if (s.isEmpty())
				return null;
			s = s.replace("%", "");
			return Integer.parseInt(s);
		} catch (Exception ex) {
			return null;
		}
	}

	@Override
	public Object build(FunCall funcall, List<Object> params) {

		String name = funcall.name;

		// ---- CONDITIONS ------------------------------------------------

		if (name.equals("Got")
				&& params.size() == 1
				&& params.get(0) instanceof Integer) {
			return new EnemiesLeft((Integer) params.get(0));
		}

		if (name.equals("True")) {
			return new True();
		}

		if (name.equals("Step")) {
			if (params.size() == 1) {
				return new AtStep(
						gal.arguments.Direction.H,
						(gal.arguments.Category) params.get(0),
						1);
			}
			if (params.size() == 2) {
				Object p0 = params.get(0);
				Object p1 = params.get(1);
				if (p0 instanceof gal.arguments.Direction) {
					return new AtStep(
							(gal.arguments.Direction) p0,
							(gal.arguments.Category) p1,
							1);
				} else {
					return new AtStep(
							(Integer) p0,
							(gal.arguments.Category) p1);
				}
			}
			if (params.size() == 3) {
				return new AtStep(
						(gal.arguments.Direction) params.get(0),
						(gal.arguments.Category) params.get(2),
						(Integer) params.get(1));
			}
		}

		if (name.equals("Closest")) {
			if (params.size() == 1) {
				return new Closest((gal.arguments.Category) params.get(0));
			}
			if (params.size() == 2) {
				Object p1 = params.get(1);
				if (p1 instanceof gal.arguments.Direction)
					return new Closest((gal.arguments.Category) params.get(0), (gal.arguments.Direction) p1);
				if (p1 instanceof Integer)
					return new Closest((gal.arguments.Category) params.get(0), (Integer) p1);
			}
			if (params.size() == 3)
				return new Closest((gal.arguments.Category) params.get(0),
						(gal.arguments.Direction) params.get(1),
						(Integer) params.get(2));
		}

		if (name.equals("KeyP")) {
			return new KeyP((gal.arguments.Key) params.get(0));
		}

		if (name.equals("KeyR")) {
			return new KeyR((gal.arguments.Key) params.get(0));
		}

		// ---- ACTIONS ---------------------------------------------------

		if (name.equals("Move")) {
			gal.arguments.Direction dir = params.isEmpty() ? gal.arguments.Direction.F
					: (gal.arguments.Direction) params.get(0);
			return new Move(dir);
		}

		if (name.equals("Turn")) {
			if (params.isEmpty()) {
				return new Turn(gal.arguments.Direction.R);
			}
			Object p = params.get(0);
			if (p instanceof gal.arguments.Direction) {
				return new Turn((gal.arguments.Direction) p);
			}
			if (p instanceof Integer) {
				return new Turn((int) p);
			}
			return new Turn(gal.arguments.Direction.R);
		}

		if (name.equals("Hit")) {
			gal.arguments.Direction dir = params.isEmpty()
					? gal.arguments.Direction.F
					: (gal.arguments.Direction) params.get(0);
			return new gal.action.Hit(dir);
		}

		if (name.equals("Wait") || name.equals("wait")) {
			return GALAction.NOTHING;
		}

		System.err.println("[GALVisitor] Action non implémentée : " + name
				+ " → NOTHING utilisé");
		return GALAction.NOTHING;
	}

	// ── BinaryOp ─────────────────────────────────────────────────────────────

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

		if ("&".equals(binop.operator)) {
			Conjunction c = new Conjunction();
			c.add((iGALCondition) left);
			c.add((iGALCondition) right);
			return c;
		}

		if ("/".equals(binop.operator)) {
			Disjunction d = new Disjunction();
			d.add((iGALCondition) left);
			d.add((iGALCondition) right);
			return d;
		}

		throw new RuntimeException("Opérateur binaire inconnu : " + binop.operator);
	}

	// ── UnaryOp ──────────────────────────────────────────────────────────────

	@Override
	public void enter(UnaryOp unop) {
	}

	@Override
	public void exit(UnaryOp unop) {
	}

	@Override
	public Object build(UnaryOp unop, Object expression) {
		if ("!".equals(unop.operator) || "not".equals(unop.operator)) {
			return new Not((iGALCondition) expression);
		}
		return expression;
	}

	// ── Condition ────────────────────────────────────────────────────────────

	@Override
	public void enter(Condition c) {
	}

	@Override
	public void exit(Condition c) {
	}

	@Override
	public Object build(Condition c, Object expression) {
		return expression;
	}

	// ── Actions ──────────────────────────────────────────────────────────────

	@Override
	public void enter(Actions a) {
		// Nouveau groupe d'actions : on repart d'une liste de pourcentages vide.
		pendingPercents.clear();
	}

	@Override
	public void visit(Actions a) {
	}

	@Override
	public void exit(Actions a) {
	}

	@Override
	public Object build(Actions a, String operator, List<Object> funcalls) {
		if (funcalls == null || funcalls.isEmpty()) {
			pendingPercents.clear();
			return null;
		}

		// Cas normal : une seule action (";" ou liste à 1 élément) → pas de tirage.
		if (funcalls.size() == 1) {
			pendingPercents.clear();
			return funcalls.get(0);
		}

		// Cas choix probabiliste : "/" avec plusieurs actions.
		// ⚠ FIX : on ne tire PAS le hasard maintenant (ce serait figé pour
		// toute la durée du jeu, car build(Actions...) n'est appelé qu'une
		// seule fois au parsing). On construit à la place un WeightedChoice
		// qui retirera le hasard à CHAQUE exec(), donc à chaque tick où la
		// transition est tentée — c'est le comportement attendu de GAL.
		if ("/".equals(operator)) {
			List<GALAction> actions = new LinkedList<>();
			for (Object o : funcalls) {
				actions.add((GALAction) o);
			}
			List<Integer> percentsCopy = new LinkedList<>(pendingPercents);
			pendingPercents.clear();
			return new WeightedChoice(actions, percentsCopy);
		}

		// Cas ";" avec plusieurs actions (combinaison non supportée cette année) :
		// on prend la première par défaut.
		pendingPercents.clear();
		return funcalls.get(0);
	}

	// ── Transition ───────────────────────────────────────────────────────────

	@Override
	public void enter(ast.Transition t) {
	}

	@Override
	public void exit(ast.Transition t) {
	}

	@Override
	public Object build(ast.Transition t,
			Object condition,
			Object action,
			Object target) {

		GALAction gAction = (action instanceof GALAction) ? (GALAction) action : null;

		return new RuntimeTransitionData(
				(iGALCondition) condition,
				gAction,
				(gal_engine.State) target);
	}

	// ── Mode ─────────────────────────────────────────────────────────────────

	@Override
	public void enter(ast.Mode m) {
	}

	@Override
	public void visit(ast.Mode m) {
	}

	@Override
	public void exit(ast.Mode m) {
	}

	@Override
	public Object build(ast.Mode m, Object source, Object behaviour) {
		return new RuntimeModeData(
				(gal_engine.State) source,
				(List<?>) behaviour);
	}

	// ── Behaviour ────────────────────────────────────────────────────────────

	@Override
	public Object visit(ast.Behaviour behaviour, List<Object> transitions) {
		return transitions;
	}

	// ── Automaton ────────────────────────────────────────────────────────────

	@Override
	public void enter(ast.Automaton a) {
	}

	@Override
	public void exit(ast.Automaton a) {
	}

	@Override
	public Object build(ast.Automaton a,
			Object initialState,
			List<Object> modes) {

		gal_engine.State init = (gal_engine.State) initialState;
		Automaton aut = new Automaton(a.name, init);

		for (Object o : modes) {
			RuntimeModeData mode = (RuntimeModeData) o;
			if (mode.transitions == null)
				continue;
			for (Object tr : mode.transitions) {
				RuntimeTransitionData rt = (RuntimeTransitionData) tr;
				aut.add(new Transition(
						mode.source,
						rt.condition,
						rt.action,
						rt.target));
			}
		}

		return aut;
	}

	// ── AST ──────────────────────────────────────────────────────────────────

	@Override
	public void enter(AST ast) {
	}

	@Override
	public void exit(AST ast) {
	}

	@Override
	public Object build(AST ast, List<Object> automata) {
		return automata;
	}

	// ── Données intermédiaires ───────────────────────────────────────────────

	static class RuntimeTransitionData {
		iGALCondition condition;
		GALAction action;
		gal_engine.State target;

		RuntimeTransitionData(iGALCondition c, GALAction a, gal_engine.State t) {
			condition = c;
			action = a;
			target = t;
		}
	}

	static class RuntimeModeData {
		gal_engine.State source;
		List<?> transitions;

		RuntimeModeData(gal_engine.State source, List<?> transitions) {
			this.source = source;
			this.transitions = transitions;
		}
	}
}
