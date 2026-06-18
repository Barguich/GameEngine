package pengo.model;

public class GoldBlock extends IceBlock {

    public GoldBlock() {
        super();
    }

    public void activate(PengoModel model) {
        assert model != null;

        model.freezeEnemies(5000);      // 5 secondes
        model.activateDoubleScore(5000);
    }
}