package engine.gal;

import java.util.HashMap;
import java.util.Map;

public class Mode {

	public static final Mode Blocking = new Mode("Blocking");
	public static final Mode Dying = new Mode("Dying");
	public static final Mode Escaping = new Mode("Escaping");
	public static final Mode Fighting = new Mode("Fighting");
	public static final Mode Resting = new Mode("Resting");
	public static final Mode Running = new Mode("Running");
	public static final Mode Searching = new Mode("Searching");
	public static final Mode Sleeping = new Mode("Sleeping");
	public static final Mode Waiting = new Mode("Waiting");
	public static final Mode Walking = new Mode("Walking");

	// Object // <-- FIXME
	private static final Map<String, Mode> modes = new HashMap<>();

	static {
		modes.put("Blocking", Blocking);
		modes.put("Dying", Dying);
		modes.put("Escaping", Escaping);
		modes.put("Fighting", Fighting);
		modes.put("Resting", Resting);
		modes.put("Running", Running);
		modes.put("Searching", Searching);
		modes.put("Sleeping", Sleeping);
		modes.put("Walking", Walking);

	}

	// CONSTRUCTOR

	private final String name;

	private Mode(String name) {
		this.name = name;
	}

	// FACTORY

	public static Mode canonical(String name) {
		Mode mode = modes.get(name);
		if (mode == null) {
			mode = new Mode(name);
			modes.put(name, mode);

		}
		return mode;
	}

	public String name() {
		return name;

	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Mode)) {
			return false;
		}
		Mode m = (Mode) o;
		return name.equals(m.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}

}
