package pengo;

import engine.Game;
import model.Entity;
import model.Ticker;
import oop.graphics.Canvas;
import oop.tasks.Runtime;
import oop.tasks.Task;
import pengo.brain.PengoBots;
import pengo.model.Enemy;
import pengo.model.PengoConfig;
import pengo.model.PengoModel;
import pengo.model.Wall;
import pengo.view.EnemyAvatar;
import pengo.view.PengoHUD;
import pengo.view.PengoMenuOverlay;
import pengo.view.WallAvatar;
import view.EntityRenderHook;
import view.Painter;
import view.View;
import view.ViewPort;

/**
 * Stress test : aucun joueur, juste des spawners qui inondent la map d'ennemis.
 * Le DebugOverlay affiche le FPS — on note à quel nombre d'entités on tombe
 * sous 24 fps.
 */
public class PengoSpawnerTest {

	private static final int SPAWN_PERIOD_MS = 0;
	private static final int MAX_ENEMIES = 10000;
	private static final java.util.Random RNG = new java.util.Random();

	public static void main(String[] args) {

		Game game = new Game(20, 13);
		PengoConfig config = new PengoConfig();
		PengoModel model = new PengoModel(Game.grid(), config);

		double mapW = game.width_cm;
		double mapH = game.height_cm;

		ViewPort viewPort = new ViewPort(mapW, mapH, mapW, mapH);
		View view = new View(model, viewPort);

		view.debug().setEnabled(true);
		view.addRenderHook(new EntityRenderHook() {
			@Override
			public int offsetX(Entity e) {
				return 0;
			}

			@Override
			public int offsetY(Entity e) {
				return 0;
			}
		});

		view.addOverlay(new PengoHUD(model, view.debug()));
		view.addOverlay(new PengoMenuOverlay(model, view.menu()));

		model.setSceneBuilder(() -> {
			addBorders(model, view);
			// Ennemi initial dans le sceneBuilder pour qu'au moment où
			// setState(PLAYING) est appelé en fin de reset, enemiesRemaining > 0
			// et la victoire ne se déclenche pas instantanément.
			spawnRandomEnemy(model, view);
		});
		model.reset();

		int winW = (int) (mapW * game.pixelPerCm);
		int winH = (int) (mapH * game.pixelPerCm);

		Runtime.boot(new java.awt.Dimension(winW, winH), (oop.tasks.Runnable) () -> {
			Canvas canvas = (Canvas) Task.task().find("canvas");
			canvas.set(view);

			new Painter(canvas).run();
			new Ticker(model, view).run();

			// Pas de controller : aucune entrée clavier dans ce test.

			Task.task().post(new oop.tasks.Runnable() {
				@Override
				public void run() {
					int count = countEnemies(model);
					if (count >= MAX_ENEMIES) {
						System.out.println("[SPAWNER] Limite atteinte (" + MAX_ENEMIES + "). Arrêt.");
						return;
					}
					spawnRandomEnemy(model, view);
					System.out.println("[SPAWNER] " + (count + 1) + " ennemis");
					Task.task().post(this, SPAWN_PERIOD_MS);
				}
			}, SPAWN_PERIOD_MS);
		});
	}

	private static void spawnRandomEnemy(PengoModel model, View view) {
		int w = Game.game().width_ncell;
		int h = Game.game().height_ncell;

		for (int attempt = 0; attempt < 20; attempt++) {
			int x = 1 + RNG.nextInt(w - 2);
			int y = 1 + RNG.nextInt(h - 2);
			geometry.Grid.Position p = Game.grid().new Position(x, y);
			if (!model.isFree(p))
				continue;

			Enemy enemy = new Enemy();
			enemy.setPosition(p);
			enemy.setSize(Game.grid().new Dimension(1, 1));
			model.add(enemy);

			EnemyAvatar av = new EnemyAvatar(enemy);
			enemy.setAvatar(av);
			av.setView(view);

			PengoBots.configureEntity(model, enemy);
			return;
		}
	}

	private static int countEnemies(PengoModel model) {
		int n = 0;
		for (Entity e : model.entities()) {
			if (e instanceof Enemy)
				n++;
		}
		return n;
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
