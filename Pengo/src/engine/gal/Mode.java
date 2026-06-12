package engine.gal;

public class Mode {

	Mode Blocking, Dying, Escaping, Fighting, Resting, Running, Searching, Sleeping, Waiting,
			Walking;

	Object // <-- FIXME
	modes;

	static {
	}

	// CONSTRUCTOR

	String name;

	Mode(String name) {
		throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `Mode`");
	}

	// FACTORY

	Mode canonical(String name) {
		throw new UnsupportedOperationException("UNIMPLEMENTED METHOD `canonical`");
	}

}
