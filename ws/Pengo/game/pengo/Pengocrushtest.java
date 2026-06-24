package pengo;

import engine.Game;
import model.Entity;
import model.Ticker;
import oop.graphics.Canvas;
import oop.tasks.Runtime;
import oop.tasks.Task;
import pengo.brain.PengoBots;
import pengo.controller.PengoController;
import pengo.model.Enemy;
import pengo.model.IceBlock;
import pengo.model.PengoConfig;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import pengo.model.Wall;
import pengo.view.EnemyAvatar;
import pengo.view.IceBlockAvatar;
import pengo.view.PengoAvatar;
import pengo.view.PengoHUD;
import pengo.view.PengoMenuOverlay;
import pengo.view.WallAvatar;
import view.EntityRenderHook;
import view.Painter;
import view.View;
import view.ViewPort;

public class Pengocrushtest {

	public static void main(String[] args) {

		Game game = new Game(20, 13);

		PengoConfig config = new PengoConfig();
		PengoModel model = new PengoModel(Game.grid(), config);

		double mapW = game.width_cm;
		double mapH = game.height_cm;

		double viewW = 20 * game.cmPerCell;
		double viewH = 13 * game.cmPerCell;

		ViewPort viewPort = new ViewPort(viewW, viewH, mapW, mapH);
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

		view.addOverlay(new PengoHUD(model, view.debug()));
		view.addOverlay(new PengoMenuOverlay(model, view.menu()));

		model.setSceneBuilder(() -> {
			buildCrushAnimationScene(model, view);
		});

		model.reset();

		int winW = (int) (viewW * game.pixelPerCm);
		int winH = (int) (viewH * game.pixelPerCm);

		Runtime.boot(new java.awt.Dimension(winW, winH), (oop.tasks.Runnable) () -> {
			Canvas canvas = (Canvas) Task.task().find("canvas");

			canvas.set(view);

			new Painter(canvas).run();
			new Ticker(model, view).run();

			canvas.set(new PengoController(model, view));
		});
	}

	private static void buildCrushAnimationScene(PengoModel model, View view) {

		/*
		 * Player placé loin des tests.
		 */
		PengoPlayer player = new PengoPlayer();
		player.setPosition(Game.grid().new Position(1, 1));
		player.setSize(Game.grid().new Dimension(1, 1));
		model.setPlayer(player);

		PengoAvatar playerAvatar = new PengoAvatar(player);
		player.setAvatar(playerAvatar);
		playerAvatar.setView(view);

		addBorders(model, view);
		PengoBots.configureEntity(model, player);

		/*
		 * TEST 1 : écrasement vers la droite.
		 *
		 * IceBlock -> Enemy -> Enemy -> Wall
		 */
		addIce(model, view, 2, 2);
		addEnemy(model, view, 5, 2);
		addEnemy(model, view, 7, 2);
		addWall(model, view, 9, 2);

		//TEST 2 : écrasement vers la gauche.
		 
		addWall(model, view, 10, 4);
		addEnemy(model, view, 12, 4);
		addEnemy(model, view, 14, 4);
		addIce(model, view, 17, 4);

		//TEST 3 : écrasement vers le bas.
		
		 
		addIce(model, view, 5, 6);
		addEnemy(model, view, 5, 8);
		addEnemy(model, view, 5, 10);
		addWall(model, view, 5, 11);

		//TEST 4 : écrasement vers le haut.
		
		addWall(model, view, 13, 3);
		addEnemy(model, view, 13, 5);
		addEnemy(model, view, 13, 7);
		addIce(model, view, 13, 10);

		//ennemie survivant 
		addEnemy(model, view, 18, 11);

		view.follow(player);
	}

	private static void startCrushTests(PengoModel model) {
		startIceAt(model, 2, 2, 0);       // droite
		startIceAt(model, 17, 4, 180);    // gauche
		startIceAt(model, 5, 6, 90);      // bas
		startIceAt(model, 13, 10, 270);   // haut
	}

	private static void startIceAt(PengoModel model, int x, int y, int direction) {
		for (Entity e : model.entities()) {
			if (!(e instanceof IceBlock)) {
				continue;
			}

			IceBlock ice = (IceBlock) e;

			if (ice.position() == null) {
				continue;
			}

			if (ice.position().x() == x && ice.position().y() == y) {
				ice.startSlide(direction);
				return;
			}
		}
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

	private static Enemy addEnemy(PengoModel model, View view, int x, int y) {
		Enemy enemy = new Enemy();
		enemy.setPosition(Game.grid().new Position(x, y));
		enemy.setSize(Game.grid().new Dimension(1, 1));
		model.add(enemy);

		EnemyAvatar avatar = new EnemyAvatar(enemy);
		enemy.setAvatar(avatar);
		avatar.setView(view);

		return enemy;
	}

	private static void addWall(PengoModel model, View view, int x, int y) {
		Wall wall = new Wall();
		wall.setPosition(Game.grid().new Position(x, y));
		wall.setSize(Game.grid().new Dimension(1, 1));
		model.add(wall);

		WallAvatar avatar = new WallAvatar(wall);
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