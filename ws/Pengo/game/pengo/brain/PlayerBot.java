package pengo.brain;

import java.util.ArrayList;
import java.util.List;

import gal_engine.Bot;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;

public class PlayerBot extends Bot {

	private final PlayerStunt playerStunt;
	private final List<Integer> heldDirections;

	public PlayerBot(PengoPlayer player, PlayerStunt stunt) {
		super(player);
		this.playerStunt = stunt;
		this.heldDirections = new ArrayList<Integer>();
	}

	public PlayerStunt playerStunt() {
		return playerStunt;
	}

	public void pressDirection(int direction) {
		Integer d = Integer.valueOf(direction);
		heldDirections.remove(d);
		heldDirections.add(d);
		if (!playerStunt.busy()) {
			playerStunt.actInDirection(direction);
		}
	}

	public void releaseDirection(int direction) {
		heldDirections.remove(Integer.valueOf(direction));
	}

	@Override
	public void tick(double elapsed_ms) {
		if (playerStunt.busy())
			return;
		if (heldDirections.isEmpty())
			return;

		if (entity.model() instanceof PengoModel pm) {
			if (pm.lost() || pm.won() || pm.menuVisible())
				return;
		}

		for (int i = heldDirections.size() - 1; i >= 0; i--) {
			int dir = heldDirections.get(i);
			playerStunt.walk(dir);
			if (playerStunt.busy())
				return;
		}
	}
}
