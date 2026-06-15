package game;

import engine.Game;
import engine.geometry.Grid;

import game.pengo.model.PengoModel;
import game.pengo.model.PengoPlayer;
import game.pengo.model.DiamondBlock;

public class MainGame {

    public static void main(String[] args) {

        Game game = new Game(10, 10);
        Grid grid = game.grid();

        PengoModel model = new PengoModel(grid);

        // Joueur obligatoire
        PengoPlayer player = new PengoPlayer();
        player.setPosition(grid.new Position(1, 1));
        player.setSize(grid.new Dimension(1, 1));
        model.setPlayer(player);

        // 3 DiamondBlocks alignés sur la même ligne y = 5
        DiamondBlock d1 = new DiamondBlock();
        d1.setPosition(grid.new Position(2, 5));
        d1.setSize(grid.new Dimension(1, 1));
        model.add(d1);

        DiamondBlock d2 = new DiamondBlock();
        d2.setPosition(grid.new Position(4, 5));
        d2.setSize(grid.new Dimension(1, 1));
        model.add(d2);

        DiamondBlock d3 = new DiamondBlock();
        d3.setPosition(grid.new Position(6, 5));
        d3.setSize(grid.new Dimension(1, 1));
        model.add(d3);

        // On lance un tick pour appeler checkVictory()
        model.tick(100);

        System.out.println("Won : " + model.won());
        System.out.println("Lost : " + model.lost());
    }
}