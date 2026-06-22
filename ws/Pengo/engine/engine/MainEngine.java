package engine;

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
import model.Entity;
import pengo.view.*;
import view.EntityRenderHook;
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

		view.addRenderHook(new EntityRenderHook() {
			@Override
			public int offsetX(Entity e) {
				return model.isVibrating(e) ? jitter() : 0;
			}

			@Override
			public int offsetY(Entity e) {
				return model.isVibrating(e) ? jitter() : 0;
			}

			private int jitter() {
				return (int) (Math.random() * 5) - 2;
			}
		});

		// Overlays plein écran : HUD d'abord, menu par-dessus.
		view.addOverlay(new PengoHUD(model, view.debug()));
		view.addOverlay(new PengoMenuOverlay(model, view.menu()));

		model.setSceneBuilder(() -> {
		    buildScene(model, view);
		    // PengoBots.configure(model);
		});

		buildScene(model, view);
		// PengoBots.configure(model);
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
	     * ==========================================================
	     */
	    addIce(model, view, 4, 2);
	 

	    /*
	     * ==========================================================
	     * SCENARIO 2 : IceBlock emporte Enemy puis l'écrase
	     *
	     * IceBlock en (4,5)
	     * Enemy en (8,5)
	     * Wall en (13,5)
	     * ==========================================================
	     */
	    addIce(model, view, 4, 5);
	    addEnemy(model, view, 8, 5, true);
	  

	    /*
	     * ==========================================================
	     * SCENARIO 3 : Enemy proche d'un obstacle
	     *
	     * IceBlock en (4,8)
	     * Enemy en (8,8)
	     * Wall en (10,8)
	     * ==========================================================
	     */
	    addIce(model, view, 4, 8);
	    addEnemy(model, view, 8, 8, true);
	   

	    /*
	     * ==========================================================
	     * SCENARIO 4 : IceBlock contre IceBlock
	     *
	     * IceBlock mobile en (4,10)
	     * IceBlock obstacle en (10,10)
	     * ==========================================================
	     */
	    addIce(model, view, 4, 10);
	    addIce(model, view, 10, 10);

	    /*
	     * ==========================================================
	     * SCENARIO 5 : Vertical vers le bas
	     *
	     * IceBlock en (16,2)
	     * Enemy en (16,5)
	     * Wall en (16,10)
	     * ==========================================================
	     */
	    addIce(model, view, 16, 2);
	    addEnemy(model, view, 16, 5, true);
	   

	    /*
	     * ==========================================================
	     * SCENARIO 6 : Alignement de 3 DiamondBlock
	     *
	     * Départ :
	     * Diamond mobile en (11,3)
	     * Diamond fixe en (14,3)
	     * Diamond fixe en (15,3)
	     *
	     * Action :
	     * Pousser celui de (11,3) vers la droite.
	     *
	     * Résultat attendu :
	     * Le DiamondBlock mobile s'arrête en (13,3),
	     * donc alignement : (13,3), (14,3), (15,3)
	     * ==========================================================
	     */
	    addDiamond(model, view, 11, 3);
	    addDiamond(model, view, 14, 3);
	    addDiamond(model, view, 15, 3);
	  

	    /*
	     * ==========================================================
	     * SCENARIO 7 : GoldBlock
	     *
	     * GoldBlock en (10,6)
	     * Enemy en (9,6)
	     *
	     * Objectif :
	     * quand l'ennemi touche le GoldBlock, il freeze.
	     * ==========================================================
	     */
	    addGold(model, view, 10, 6);
	    addEnemy(model, view, 9, 6, false);

	    /*
	     * ==========================================================
	     * SCENARIO 8 : FishBonus
	     *
	     * FishBonus proche du joueur.
	     * ==========================================================
	     */
	    addFish(model, view, 3, 6);

	    /*
	     * Enemy normal en plus pour éviter que la partie se termine trop vite
	     * si tous les ennemis de test sont tués.
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

	private static void addGold(PengoModel model, View view, int x, int y) {
	    GoldBlock gold = new GoldBlock();
	    gold.setPosition(Game.grid().new Position(x, y));
	    gold.setSize(Game.grid().new Dimension(1, 1));
	    model.add(gold);

	    ShapeAvatar avatar = new ShapeAvatar(
	        gold,
	        ShapeAvatar.Shape.RECT,
	        255,
	        255,
	        215,
	        0
	    );

	    gold.setAvatar(avatar);
	    avatar.setView(view);
	}
	private static void addWall(PengoModel model, View view, int x, int y) {
	    Wall wall = new Wall();
	    wall.setPosition(Game.grid().new Position(x, y));
	    wall.setSize(Game.grid().new Dimension(1, 1));
	    model.add(wall);

	    ShapeAvatar avatar = new ShapeAvatar(
	        wall,
	        ShapeAvatar.Shape.RECT,
	        255,
	        120,
	        120,
	        120
	    );

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
	
	private static void addFish(PengoModel model, View view, int x, int y) {
	    FishBonus fish = new FishBonus();
	    fish.setPosition(Game.grid().new Position(x, y));
	    fish.setSize(Game.grid().new Dimension(1, 1));
	    model.add(fish);

	    ShapeAvatar fishAvatar = new ShapeAvatar(
	        fish,
	        ShapeAvatar.Shape.OVAL,
	        255,
	        0,
	        180,
	        255
	    );

	    fish.setAvatar(fishAvatar);
	    fishAvatar.setView(view);
	}
}