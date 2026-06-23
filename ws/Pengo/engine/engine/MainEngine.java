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
		model.setEnemySpawnListener(enemy -> {
			EnemyAvatar avatar = new EnemyAvatar(enemy);
			enemy.setAvatar(avatar);
			avatar.setView(view);

			PengoBots.configureEntity(model, enemy);
		});
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
player.setPosition(Game.grid().new Position(12, 3));		player.setSize(Game.grid().new Dimension(1, 1));
		model.setPlayer(player);

		ShapeAvatar playerAvatar = new ShapeAvatar(
				player,
				ShapeAvatar.Shape.OVAL,
				255,
				220,
				220,
				0);
		player.setAvatar(playerAvatar);
		playerAvatar.setView(view);

		/*
		 * Bordures une seule fois.
		 */
		addBorders(model, view);

		/*
		 * TEST 1 :
		 * Le SnoBee voit le joueur à droite.
		 * Le bloc placé devant lui doit être détruit en un coup,
		 * avec son animation de 300 ms.
		 */
		addEnemy(model, view, 4, 3, false);
		addIce(model, view, 5, 3);

		/*
		 * TEST 2 :
		 * Ce bloc contient un SnoBee.
		 * Il doit éclore après 5 secondes.
		 */
		addSnoBeeEgg(model, view, 6, 8, 5_000);

		/*
		 * TEST 3 :
		 * Ennemi gelé pendant 5 secondes.
		 */
		Enemy frozenEnemy = addEnemy(model, view, 12, 8, false);
		frozenEnemy.freeze(5_000);

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

	private static void addSnoBeeEgg(
			PengoModel model,
			View view,
			int x,
			int y,
			long hatchDelay) {

		IceBlock ice = new IceBlock(true, hatchDelay);
		ice.setPosition(Game.grid().new Position(x, y));
		ice.setSize(Game.grid().new Dimension(1, 1));
		model.add(ice);

		IceBlockAvatar avatar = new IceBlockAvatar(ice);
		ice.setAvatar(avatar);
		avatar.setView(view);
	}

	private static Enemy addEnemy(PengoModel model, View view, int x, int y, boolean frozen) {
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
		return enemy;
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
		GoldBlock g = new GoldBlock();
		g.setPosition(Game.grid().new Position(x, y));
		g.setSize(Game.grid().new Dimension(1, 1));
		model.add(g);

		ShapeAvatar avatar = new ShapeAvatar(g, ShapeAvatar.Shape.RECT, 255, 200, 0, 255);
		g.setAvatar(avatar);
		avatar.setView(view);
	}

	private static void addWall(PengoModel model, View view, int x, int y) {
		Wall wall = new Wall();
		wall.setPosition(Game.grid().new Position(x, y));
		wall.setSize(Game.grid().new Dimension(1, 1));
		model.add(wall);
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
	// private static void addGold(PengoModel model, View view, int x, int y) {
	// GoldBlock gold = new GoldBlock();
	// gold.setPosition(Game.grid().new Position(x, y));
	// gold.setSize(Game.grid().new Dimension(1, 1));
	// model.add(gold);

	// ShapeAvatar avatar = new ShapeAvatar(
	// gold,
	// ShapeAvatar.Shape.RECT,
	// 255,
	// 255,
	// 215,
	// 0
	// );

	// gold.setAvatar(avatar);
	// avatar.setView(view);
	// }
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
				255);

		fish.setAvatar(fishAvatar);
		fishAvatar.setView(view);
	}
}