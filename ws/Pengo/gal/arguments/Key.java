package arguments;

import java.util.HashMap;
import java.util.Map;

import oop.graphics.VirtualKeyCodes;

/**
 * @apiNote les touches du langage GAL : lettres a..z, chiffres 0..9,
 *          SPACE, ENTER, et les flèches FU, FD, FR, FL.
 * @implNote même pattern canonical que Direction/Category : unicité des
 *           instances, comparaison par ==.
 */
public class Key {

	// CONSTANTS — flèches
	public static Key FU, FD, FL, FR, SPACE, ENTER;

	// STATIC
	private static Map<String, Key> keys;

	static {
		keys = new HashMap<>();
		FU = new Key("FU", VirtualKeyCodes.VK_UP);
		FD = new Key("FD", VirtualKeyCodes.VK_DOWN);
		FL = new Key("FL", VirtualKeyCodes.VK_LEFT);
		FR = new Key("FR", VirtualKeyCodes.VK_RIGHT);
		SPACE = new Key("SPACE", VirtualKeyCodes.VK_SPACE);
		ENTER = new Key("ENTER", VirtualKeyCodes.VK_ENTER);
	}

	// FACTORY
	public static Key canonical(String name) {
		Key k = keys.get(name);
		if (k != null)
			return k;
		// lettres a..z et chiffres 0..9 : keyCode = code ASCII majuscule
		if (name.length() == 1) {
			char c = Character.toUpperCase(name.charAt(0));
			if ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))
				return new Key(name, c);
		}
		throw new IllegalArgumentException("touche GAL inconnue : " + name);
	}

	// CONSTRUCTOR
	private final String name;
	private final int keyCode;

	private Key(String name, int keyCode) {
		this.name = name;
		this.keyCode = keyCode;
		keys.put(name, this);
	}

	public String name() {
		return name;
	}

	public int keyCode() {
		return keyCode;
	}
}
