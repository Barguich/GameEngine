package engine.gal.aut;

import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import engine.gal.State;

public class Transitions implements iTransitions{

	long serialVersionUID = 1L;
	private Map<State,List<Transition>> transitions;
	public Transitions(){
		transitions=new HashMap<>();
	}

	public void add(Transition t) {
		State source=t.source();
		List<Transition>list=transitions.get(source);
		if(list==null){
			list=new LinkedList<>();
			transitions.put(source,list);

		}
		list.add(t);
	}

	public List<Transition> get(State state) {
		List<Transition>list=transitions.get(state);
		if(list==null){
			return new LinkedList<>();
		}
		return list;

	}
}