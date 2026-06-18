package engine.geometry;

import java.util.logging.Level;
import java.util.logging.Logger;

final class Log {

	static final Logger logger = Logger.getLogger("engine.geometry");

	static final boolean FINE = logger.isLoggable(Level.FINE);
	static final boolean FINER = logger.isLoggable(Level.FINER);

	private Log() {
	}
}
