package engine.gal.aut;

import engine.gal.GALBot;
import engine.gal.State;
import engine.model.Entity;
import engine.model.Stunt;

public class Automaton {

	private State initial;
	private State current;
	/**
	 * @apiNote represents a collection of {@code Transition}
	 * @implNote Transitions are ordered by the order in which they were added so
	 *           that the automaton processes them in that order.
	 * @implNote Choose your representation carefully to efficiently identify the
	 *           potential transitions for triggering.
	 */
	 private iTransitions transitions;
	 private String name;

	// CONSTRUCTOR

	 public Automaton(String name, State initial){
		this.name=name;
		this.initial=initial;
		this.current=initial;
		this.transitions=new Transitions();
		
	}
	// BUILDER

	/**
	 * @apiNote add a transition to the automaton after the previous ones
	 */
	 public void add(Transition t){
		transitions.add(t);	
	}

	// AUTOMATON STEP = TRIGGER A TRANSITION or FAIL and STAY IN THE SAME STATE

	/**
	 * @apiNote Try to select and execute one valid transition
	 * @param e = the Entity whose bot evaluates the condition and whose stunt
	 *          executes the action
	 * @return {@code true} if there exists a transition which can be triggered by
	 *         the {@code bot}
	 *         <LI>{@code false} if no transition can be taken.</LI>
	 */
	public boolean step(Entity e){ 
		if(current==null){
			return false;

		}
		for(Transition t:transitions.get(current)){
			if(t.exec(e)){
				current=t.target();
				return true;
			}
				
		}
		return false;
		
	}
	public State initial(){
		return initial;
	}
	public State current(){
		return current;
	}
	public String name(){
		return name;

	}

}