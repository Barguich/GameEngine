package pengoGametest;
//test diamond alignement horizontal + vertical  + condition de victoire yes

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import engine.Game;
import pengo.model.DiamondBlock;
import pengo.model.PengoModel;

public class DiamondBlockTest {

    @Test
    public void testDiamondBlocksAlignedHorizontallyWin() {

        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        DiamondBlock d1 = new DiamondBlock();
        d1.setPosition(Game.grid().new Position(2, 5));
        d1.setSize(Game.grid().new Dimension(1, 1));
        model.add(d1);

        DiamondBlock d2 = new DiamondBlock();
        d2.setPosition(Game.grid().new Position(3, 5));
        d2.setSize(Game.grid().new Dimension(1, 1));
        model.add(d2);

        DiamondBlock d3 = new DiamondBlock();
        d3.setPosition(Game.grid().new Position(4, 5));
        d3.setSize(Game.grid().new Dimension(1, 1));
        model.add(d3);

        model.checkVictory();

        assertTrue(model.won());
    }

    @Test
    public void testDiamondBlocksAlignedVerticallyWin() {

        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        DiamondBlock d1 = new DiamondBlock();
        d1.setPosition(Game.grid().new Position(5, 2));
        d1.setSize(Game.grid().new Dimension(1, 1));
        model.add(d1);

        DiamondBlock d2 = new DiamondBlock();
        d2.setPosition(Game.grid().new Position(5, 3));
        d2.setSize(Game.grid().new Dimension(1, 1));
        model.add(d2);

        DiamondBlock d3 = new DiamondBlock();
        d3.setPosition(Game.grid().new Position(5, 4));
        d3.setSize(Game.grid().new Dimension(1, 1));
        model.add(d3);

        model.checkVictory();

        assertTrue(model.won());
    }
    @Test
    public void testDiamondBlocksAlignedButNotAdjacentDoNotWin() {

        new Game(10, 10);

        PengoModel model = new PengoModel(Game.grid());

        DiamondBlock d1 = new DiamondBlock();
        d1.setPosition(Game.grid().new Position(2, 5));
        model.add(d1);

        DiamondBlock d2 = new DiamondBlock();
        d2.setPosition(Game.grid().new Position(4, 5));
        model.add(d2);

        DiamondBlock d3 = new DiamondBlock();
        d3.setPosition(Game.grid().new Position(6, 5));
        model.add(d3);

        model.checkVictory();

        assertTrue(!model.won());
    }
}