package engine.gal.action;

import java.util.HashMap;
import java.util.Map;

public class Mode {

	public static Mode Blocking, Dying, Escaping, Fighting, Resting, Running, Searching, Sleeping, Waiting,
			Walking;

	public static Map<String, Mode> modes;

	static {
		modes = new HashMap<>();

		Blocking = new Mode("Blocking");
		Dying = new Mode("Dying");
		Escaping = new Mode("Escaping");
		Fighting = new Mode("Fighting");
		Resting = new Mode("Resting");
		Running = new Mode("Running");
		Searching = new Mode("Searching");
		Sleeping = new Mode("Sleeping");
		Waiting = new Mode("Waiting");
		Walking = new Mode("Walking");
	}

	// CONSTRUCTOR

	private String name;

	public Mode(String name) {
		this.name = name;
		modes.put(name, this);
	}

	// FACTORY

	public Mode canonical(String name) {
		return modes.get(name);
	}

	public String name(){
		return name;
	}

}