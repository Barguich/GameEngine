package engine;

import model.Entity;
import model.Ticker;
import oop.graphics.Canvas;
import oop.tasks.Runtime;
import oop.tasks.Task;
import pengo.brain.PengoBots;
import pengo.controller.PengoController;
import pengo.model.DiamondBlock;
import pengo.model.Enemy;
import pengo.model.FishBonus;
import pengo.model.GoldBlock;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import pengo.model.Wall;
import testSprite.IceBlockAvatar;

import view.Painter;
import view.ShapeAvatar;
import view.View;
import view.ViewPort;

import pengo.brain.PengoBots;
import testSprite.EnemyAvatar;

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
	    /*
	     * PLAYER
	     * Il commence près du scénario principal.
	     */
	    PengoPlayer player = new PengoPlayer();
	    player.setPosition(Game.grid().new Position(2, 5));
	    player.setSize(Game.grid().new Dimension(1, 1));
	    model.setPlayer(player);

	    ShapeAvatar playerAvatar = new ShapeAvatar(
	        player,
	        ShapeAvatar.Shape.OVAL,
	        255,
	        220,
	        220,
	        0
	    );
	    player.setAvatar(playerAvatar);
	    playerAvatar.setView(view);

	    /*
	     * Bordures une seule fois.
	     */
	    addBorders(model, view);

	    /*
	     * ==========================================================
	     * SCENARIO 1 : Slide simple
	     *
	     * IceBlock en (4,2)
	     * Wall en (12,2)
	     *
	     * Objectif :
	     * le bloc glisse vers la droite et s'arrête contre le mur.
	     * ==========================================================
	     */
	    addIce(model, view, 4, 2);
	    addWall(model, view, 12, 2);


	    /*
	     * ==========================================================
	     * SCENARIO 2 : IceBlock emporte Enemy puis l'écrase
	     *
	     * IceBlock en (4,5)
	     * Enemy en (8,5)
	     * Wall en (13,5)
	     *
	     * Objectif :
	     * le bloc glisse, touche l'ennemi, l'emporte,
	     * puis l'écrase contre le mur.
	     * ==========================================================
	     */
	    addIce(model, view, 4, 5);
	    addEnemy(model, view, 8, 5, true);
	    addWall(model, view, 13, 5);


	    /*
	     * ==========================================================
	     * SCENARIO 3 : Enemy collé à l'obstacle
	     *
	     * IceBlock en (4,8)
	     * Enemy en (8,8)
	     * Wall en (9,8)
	     *
	     * Objectif :
	     * le bloc glisse, touche l'ennemi,
	     * et l'ennemi est écrasé presque immédiatement.
	     * ==========================================================
	     */
	    addIce(model, view, 4, 8);
	    addEnemy(model, view, 8, 8, true);
	    addWall(model, view, 9, 8);


	    /*
	     * ==========================================================
	     * SCENARIO 4 : IceBlock contre IceBlock
	     *
	     * IceBlock mobile en (4,10)
	     * IceBlock obstacle en (9,10)
	     *
	     * Objectif :
	     * le premier bloc glisse et s'arrête contre l'autre bloc.
	     * ==========================================================
	     */
	    addIce(model, view, 4, 10);
	    addIce(model, view, 9, 10);


	    /*
	     * ==========================================================
	     * SCENARIO 5 : Vertical vers le bas
	     *
	     * IceBlock en (16,3)
	     * Enemy en (16,6)
	     * Wall en (16,10)
	     *
	     * Objectif :
	     * tu pousses le bloc vers le bas.
	     * Il emporte l'ennemi et l'écrase contre le mur.
	     * ==========================================================
	     */
	    addIce(model, view, 16, 3);
	    addEnemy(model, view, 16, 6, true);
	    addWall(model, view, 16, 10);


	    /*
	     * Enemy normal en plus, pour éviter que la partie se termine
	     * dès que tu écrases le premier ennemi.
	     */
	    addEnemy(model, view, 17, 9, false);

	    view.follow(player);
	}
	private static void addIce(PengoModel model, View view, int x, int y) {
		IceBlock ice = new IceBlock();
		ice.setPosition(Game.grid().new Position(x, y));
		ice.setSize(Game.grid().new Dimension(1, 1));
		model.add(ice);

		IceBlockAvatar avatar = new IceBlockAvatar(ice);
		ice.setAvatar(avatar);
		avatar.setView(view);
	}
	private static void addEnemy(PengoModel model, View view, int x, int y, boolean frozen) {
	    Enemy enemy = new Enemy();
	    enemy.setPosition(Game.grid().new Position(x, y));
	    enemy.setSize(Game.grid().new Dimension(1, 1));
	    model.add(enemy);

	    EnemyAvatar enemyAvatar = new EnemyAvatar(enemy);
	    enemy.setAvatar(enemyAvatar);
	    enemyAvatar.setView(view);

	    /*
	     * Pour les scénarios de test, on freeze les ennemis
	     * pour qu'ils restent en place jusqu'au contact avec le IceBlock.
	     */
	    if (frozen) {
	        enemy.freeze(600_000);
	    }
	}

	private static void addDiamond(PengoModel model, View view, int x, int y) {
		DiamondBlock d = new DiamondBlock();
		d.setPosition(Game.grid().new Position(x, y));
		d.setSize(Game.grid().new Dimension(1, 1));
		model.add(d);

		ShapeAvatar avatar = new ShapeAvatar(d, ShapeAvatar.Shape.RECT, 255, 0, 200, 255);
		d.setAvatar(avatar);
		avatar.setView(view);
	}

	private static void addWall(PengoModel model, View view, int x, int y) {
	    Wall wall = new Wall();
	    wall.setPosition(Game.grid().new Position(x, y));
	    wall.setSize(Game.grid().new Dimension(1, 1));
	    model.add(wall);

	    ShapeAvatar avatar = new ShapeAvatar(wall, ShapeAvatar.Shape.RECT, 255, 120, 120, 120);
	    wall.setAvatar(avatar);
	    avatar.setView(view);
	}
	private static void addBorders(PengoModel model, View view) {
	    int w = Game.game().width_ncell;
	    int h = Game.game().height_ncell;

	    for (int x = 0; x < w; x++) {
	        addWall(model, view, x, 0);
	        addWall(model, view, x, h - 1);
	    }

	    for (int y = 1; y < h - 1; y++) {
	        addWall(model, view, 0, y);
	        addWall(model, view, w - 1, y);
	    }
	}
}