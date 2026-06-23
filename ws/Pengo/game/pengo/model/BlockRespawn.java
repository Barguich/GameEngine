package pengo.model;

import geometry.Grid;
import model.Model;

/*
 * Bloc de glace qui, contrairement à un IceBlock normal, ne disparaît pas
 * définitivement quand il est détruit : il réapparaît à sa position de départ
 * après un certain délai (comportement des blocs de l'arcade Pengo).
 */
public class BlockRespawn extends IceBlock {

    public static final long DEFAULT_RESPAWN_DELAY = 5_000;

    private final long respawnDelay;

    public BlockRespawn() {
        this(DEFAULT_RESPAWN_DELAY);
    }

    public BlockRespawn(long respawnDelay) {
        super();
        this.respawnDelay = respawnDelay;
    }

    public long respawnDelay() {
        return respawnDelay;
    }

    @Override
    public void breakBlock() {
        if (broken()) {
            return;
        }

        Grid.Position spawnPosition =
                (position() != null) ? position().copy() : null;

        // On capture le modèle AVANT super.breakBlock() : celui-ci appelle
        // model.remove(this), ce qui met le champ model à null.
        Model m = this.model;

        super.breakBlock();

        if (spawnPosition != null && m instanceof PengoModel) {
            ((PengoModel) m).scheduleBlockRespawn(spawnPosition, respawnDelay);
        }
    }
}
