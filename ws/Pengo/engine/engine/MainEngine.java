package engine;

import model.Entity;
import model.Ticker;
import oop.graphics.Canvas;
import oop.tasks.Runtime;
import oop.tasks.Task;
import pengo.controller.PengoController;
import pengo.model.DiamondBlock;
import pengo.model.Enemy;
import pengo.model.FishBonus;
import pengo.model.GoldBlock;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import view.Painter;
import view.ShapeAvatar;
import view.View;
import view.ViewPort;
import pengo.brain.PengoBots;

public class MainEngine {

    public static void main(String[] args) {

        Game game = new Game(20, 13);
        PengoModel model = new PengoModel(Game.grid());

        double mapW = game.width_cm;
        double mapH = game.height_cm;

        ViewPort viewPort = new ViewPort(mapW, mapH, mapW, mapH);
        View view = new View(model, viewPort);
        view.debug().setEnabled(true);

        model.setSceneBuilder(() -> {
            buildScene(model, view);
            PengoBots.configure(model);
        });

        buildScene(model, view);
        PengoBots.configure(model);

        int winW = (int) (mapW * game.pixelPerCm);
        int winH = (int) (mapH * game.pixelPerCm);

        Runtime.boot(new java.awt.Dimension(winW, winH), (oop.tasks.Runnable) () -> {
            Canvas canvas = (Canvas) Task.task().find("canvas");

            canvas.set(view);

            new Painter(canvas).run();
            new Ticker(model, view).run();

            canvas.set(new PengoController(model, view));
        });
    }

    private static void buildScene(PengoModel model, View view) {
        PengoPlayer player = new PengoPlayer();
        player.setPosition(Game.grid().new Position(2, 5));
        player.setSize(Game.grid().new Dimension(1, 1));
        model.setPlayer(player);

        ShapeAvatar playerAvatar =
                new ShapeAvatar(player, ShapeAvatar.Shape.OVAL, 255, 220, 220, 0);
        player.setAvatar(playerAvatar);
        playerAvatar.setView(view);

        addIce(model, view, 5, 5);
        addIce(model, view, 6, 7);

        GoldBlock gold = new GoldBlock();
        gold.setPosition(Game.grid().new Position(10, 5));
        gold.setSize(Game.grid().new Dimension(1, 1));
        model.add(gold);

        ShapeAvatar goldAvatar =
                new ShapeAvatar(gold, ShapeAvatar.Shape.RECT, 255, 255, 215, 0);
        gold.setAvatar(goldAvatar);
        goldAvatar.setView(view);

        addDiamond(model, view, 13, 4);
        addDiamond(model, view, 15, 4);
        addDiamond(model, view, 17, 4);

        FishBonus fish = new FishBonus();
        fish.setPosition(Game.grid().new Position(3, 8));
        fish.setSize(Game.grid().new Dimension(1, 1));
        model.add(fish);

        ShapeAvatar fishAvatar =
                new ShapeAvatar(fish, ShapeAvatar.Shape.OVAL, 255, 0, 180, 255);
        fish.setAvatar(fishAvatar);
        fishAvatar.setView(view);

        Enemy e1 = new Enemy();
        e1.setPosition(Game.grid().new Position(8, 5));
        e1.setSize(Game.grid().new Dimension(1, 1));
        model.add(e1);

        ShapeAvatar e1Avatar =
                new ShapeAvatar(e1, ShapeAvatar.Shape.OVAL, 255, 255, 0, 0);
        e1.setAvatar(e1Avatar);
        e1Avatar.setView(view);

        Enemy e2 = new Enemy();
        e2.setPosition(Game.grid().new Position(11, 8));
        e2.setSize(Game.grid().new Dimension(1, 1));
        model.add(e2);

        ShapeAvatar e2Avatar =
                new ShapeAvatar(e2, ShapeAvatar.Shape.OVAL, 255, 255, 80, 80);
        e2.setAvatar(e2Avatar);
        e2Avatar.setView(view);

        addWall(model, view, 9, 5);
        addWall(model, view, 18, 4);

        view.follow(player);
    }

    private static void addIce(PengoModel model, View view, int x, int y) {
        IceBlock ice = new IceBlock();
        ice.setPosition(Game.grid().new Position(x, y));
        ice.setSize(Game.grid().new Dimension(1, 1));
        model.add(ice);

        ShapeAvatar avatar =
                new ShapeAvatar(ice, ShapeAvatar.Shape.RECT, 255, 120, 180, 255);
        ice.setAvatar(avatar);
        avatar.setView(view);
    }

    private static void addDiamond(PengoModel model, View view, int x, int y) {
        DiamondBlock d = new DiamondBlock();
        d.setPosition(Game.grid().new Position(x, y));
        d.setSize(Game.grid().new Dimension(1, 1));
        model.add(d);

        ShapeAvatar avatar =
                new ShapeAvatar(d, ShapeAvatar.Shape.RECT, 255, 0, 200, 255);
        d.setAvatar(avatar);
        avatar.setView(view);
    }

    private static void addWall(PengoModel model, View view, int x, int y) {
        Entity wall = new Entity("Wall");
        wall.setPosition(Game.grid().new Position(x, y));
        wall.setSize(Game.grid().new Dimension(1, 1));
        model.add(wall);

        ShapeAvatar avatar =
                new ShapeAvatar(wall, ShapeAvatar.Shape.RECT, 255, 120, 120, 120);
        wall.setAvatar(avatar);
        avatar.setView(view);
    }
}