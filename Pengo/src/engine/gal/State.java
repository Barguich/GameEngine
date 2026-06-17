package engine.gal;

import java.util.Objects;

public class State {

	private Mode mode;
	private int id;

	// CONSTRUCTOR

	public State(String mode, int id) {
		this.mode = Mode.canonical(mode);
		this.id = id;

	}

	public Mode mode() {
		return mode;
	}

	public int id() {
		return id;
	}

	// EQUALS
	public boolean equals(Object o) {

		if (!(o instanceof State))
			return false;
		return equals((State) o);

	}

	public boolean equals(State s) {
		return s != null && id == s.id && mode.equals(s.mode);
	}

	// HASH
	@Override
	public int hashCode() {
		return Objects.hash(mode, id);
	}

	@Override
	public String toString() {
		return mode.name() + "_" + id;
	}
}
