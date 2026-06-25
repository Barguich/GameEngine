package pengo.model;

// Bloc spécial utilisé pour former la combinaison gagnante
public class DiamondBlock extends IceBlock {

	public DiamondBlock() {
		super();
	}

	// Permet d'identifier un DiamondBlock
	public boolean isDiamond() {
		return true;
	}

	// Un diamant ne doit jamais être détruit par un ennemi.
	@Override
	public boolean destructibleByEnemy() {
		return false;
	}

	@Override
	public boolean wizz() {
		if (model instanceof PengoModel pm) {
			pm.winByDiamondAlignment();
			return true;
		}
		return false;
	}
}