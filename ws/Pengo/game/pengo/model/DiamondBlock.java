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
}