package pengo;


import java.io.IOException;



import engine.Game;
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
import pengo.model.PengoConfig;
import pengo.model.PengoMapLoader;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import pengo.model.Wall;
import pengo.view.PengoHUD;
import pengo.view.PengoMenuOverlay;
import pengo.view.WallAvatar;
import pengo.view.EnemyAvatar;
import pengo.view.FishBonusAvatar;
import pengo.view.IceBlockAvatar;
import pengo.view.PengoAvatar;
import view.EntityRenderHook;
import view.Painter;
import view.View;
import view.ViewPort;

// Lanceur applicatif du jeu Pengo.
// Réside côté game : il assemble le modèle, la vue et les entités Pengo
// en s'appuyant sur le moteur (engine), qui lui ne connaît rien de Pengo.
public class PengoMain {

	public static void main(String[] args) {

		String[] baseMap;
		try {
			baseMap = PengoMapLoader.readMap("Asset/rsrc/maps/pengo_big_viewport.txt");
		} catch (IOException e) {
			throw new RuntimeException("Impossible de lire la map", e);
		}

		int mapHeight = baseMap.length;
		int mapWidth = baseMap[0].length();

		Game game = new Game(mapWidth, mapHeight);
		PengoConfig config = new PengoConfig();
		PengoModel model = new PengoModel(Game.grid(), config);

		double mapW = game.width_cm;
		double mapH = game.height_cm;

		double viewW = 20 * game.cmPerCell;
		double viewH = 13 * game.cmPerCell;

		ViewPort viewPort = new ViewPort(viewW, viewH, mapW, mapH);
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

		view.addOverlay(new PengoHUD(model, view.debug()));
		view.addOverlay(new PengoMenuOverlay(model, view.menu()));

		model.setSceneBuilder(() -> {
			if (model.selectedMapFile() == null) {
				return;
			}

			try {
				String[] selectedMap = PengoMapLoader.readMap(model.selectedMapFile());
				buildSceneFromMap(model, view, selectedMap);
				PengoBots.configure(model);
			} catch (IOException e) {
				throw new RuntimeException("Impossible de lire la map", e);
			}
		});

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

	private static void buildScene(PengoModel model, View view) {

		/*
		 * PLAYER Il commence près du scénario principal.
		 */
		PengoPlayer player = new PengoPlayer();
		player.setPosition(Game.grid().new Position(12, 3));
		player.setSize(Game.grid().new Dimension(1, 1));
		model.setPlayer(player);

		PengoAvatar playerAvatar = new PengoAvatar(player);
		player.setAvatar(playerAvatar);
		playerAvatar.setView(view);

		/*
		 * Bordures une seule fois.
		 */
		addBorders(model, view);

		/*
		 * ========================================================== SCENARIO 1 : Slide
		 * simple
		 *
		 * IceBlock en (4,2) Wall en (12,2)
		 *
		 * Le bloc glisse vers la droite et s'arrête contre le mur.
		 * ==========================================================
		 */
		addIce(model, view, 4, 2);
		addEnemy(model, view, 4, 3, false);
		addIce(model, view, 5, 3);

		/*
		 * ========================================================== SCENARIO 2 :
		 * IceBlock emporte Enemy puis l'écrase
		 *
		 * IceBlock en (4,5) Enemy en (8,5) Wall en (13,5)
		 *
		 * Après écrasement, le IceBlock finit près du mur, mais il reste de l'espace
		 * au-dessus et en-dessous pour le repousser.
		 * ==========================================================
		 */
		addIce(model, view, 4, 5);
		addEnemy(model, view, 8, 5, true);
		addSnoBeeEgg(model, view, 6, 8, 5_000);

		/*
		 * ========================================================== SCENARIO 3 : Enemy
		 * proche d'un obstacle, écrasement rapide
		 *
		 * IceBlock en (4,8) Enemy en (8,8) Wall en (10,8)
		 *
		 * J'ai mis le mur en (10,8), pas directement en (9,8), pour éviter que l'ennemi
		 * soit écrasé trop instantanément.
		 * ==========================================================
		 */
		addIce(model, view, 4, 8);
		addEnemy(model, view, 8, 8, true);

		/*
		 * ========================================================== SCENARIO 4 :
		 * IceBlock contre IceBlock
		 *
		 * IceBlock mobile en (4,10) IceBlock obstacle en (10,10)
		 *
		 * Il y a assez d'espace autour pour retester le bloc après.
		 * ==========================================================
		 */
		addIce(model, view, 4, 10);
		addIce(model, view, 10, 10);

		/*
		 * ========================================================== SCENARIO 5 :
		 * Vertical vers le bas
		 *
		 * IceBlock en (16,2) Enemy en (16,5) Wall en (16,10)
		 *
		 * Le bloc pousse l'ennemi vers le bas.
		 * ==========================================================
		 */
		addIce(model, view, 16, 2);
		addEnemy(model, view, 16, 5, true);
		addWall(model, view, 16, 10);
		Enemy frozenEnemy = addEnemy(model, view, 12, 8, false);
		frozenEnemy.freeze(5_000);

		/*
		 * ========================================================== SCENARIO 6 :
		 * Alignement de 3 DiamondBlock
		 *
		 * Départ : Diamond mobile en (11,3) Diamond fixe en (14,3) Diamond fixe en
		 * (15,3)
		 *
		 * Action : Pengo pousse le DiamondBlock de (11,3) vers la droite.
		 *
		 * Résultat attendu : Le DiamondBlock mobile s'arrête en (13,3), donc les
		 * diamonds sont alignés : (13,3), (14,3), (15,3)
		 *
		 * Pas de mur juste à côté, pour éviter que les DiamondBlock soient bloqués par
		 * la bordure ou par un obstacle inutile.
		 * ==========================================================
		 */
		addDiamond(model, view, 11, 3);
		addDiamond(model, view, 14, 3);
		addDiamond(model, view, 16, 3);

		/*
		 * Enemy normal en plus pour éviter que la partie se termine trop vite après
		 * avoir tué les ennemis de test.
		 */
		addEnemy(model, view, 17, 9, false);
		/*
		 * SCENARIO GOLD BLOCK :
		 *
		 * Enemy proche du GoldBlock. Quand l'ennemi le touche, il doit freeze pendant 5
		 * secondes.
		 */
		addGold(model, view, 10, 6);
		addEnemy(model, view, 9, 6, false);
		// fidh bonus
		addFish(model, view, 3, 6);

		view.follow(player);
		// Élasticité : le joueur peut bouger dans ±15 % du viewport autour du
		// centre avant que la caméra ne se déplace. Visible seulement si le
		// viewport est plus petit que la map.
		// view.setElasticZone(0.15, 0.15);

	}

	// Tous les blocs de glace sont des BlockRespawn : une fois détruits, ils
	// réapparaissent à leur position de départ (comportement de l'arcade Pengo).
	private static void addIce(PengoModel model, View view, int x, int y) {
		IceBlock ice = new IceBlock();
		ice.setPosition(Game.grid().new Position(x, y));
		ice.setSize(Game.grid().new Dimension(1, 1));
		model.add(ice);

		IceBlockAvatar avatar = new IceBlockAvatar(ice);
		ice.setAvatar(avatar);
		avatar.setView(view);
	}

	private static void addSnoBeeEgg(PengoModel model, View view, int x, int y, long hatchDelay) {

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
		 * Pour les scénarios de test, on freeze les ennemis pour qu'ils restent en
		 * place jusqu'au contact avec le IceBlock.
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

		IceBlockAvatar avatar = new IceBlockAvatar(d);
		d.setAvatar(avatar);
		avatar.setView(view);
	}

	private static void addGold(PengoModel model, View view, int x, int y) {
		GoldBlock g = new GoldBlock();
		g.setPosition(Game.grid().new Position(x, y));
		g.setSize(Game.grid().new Dimension(1, 1));
		model.add(g);

		IceBlockAvatar avatar = new IceBlockAvatar(g);
		g.setAvatar(avatar);
		avatar.setView(view);
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

	private static void addFish(PengoModel model, View view, int x, int y) {
		FishBonus fish = new FishBonus();
		fish.setPosition(Game.grid().new Position(x, y));
		fish.setSize(Game.grid().new Dimension(1, 1));
		model.add(fish);

		FishBonusAvatar fishAvatar = new FishBonusAvatar(fish);
		fish.setAvatar(fishAvatar);
		fishAvatar.setView(view);
	}

	private static void buildSceneFromMap(PengoModel model, View view, String[] map) {
		PengoMapLoader.load(model, map);

		for (Entity e : model.entities()) {
			attachAvatar(e, view);
		}

		if (model.player() != null) {
			view.follow(model.player());
		}
	}

	private static void attachAvatar(Entity e, View view) {
		if (e instanceof PengoPlayer) {
			PengoAvatar avatar = new PengoAvatar((PengoPlayer) e);
			e.setAvatar(avatar);
			avatar.setView(view);
			return;
		}

		if (e instanceof Enemy) {
			EnemyAvatar avatar = new EnemyAvatar((Enemy) e);
			e.setAvatar(avatar);
			avatar.setView(view);
			return;
		}

		if (e instanceof Wall) {
			WallAvatar avatar = new WallAvatar(e);
			e.setAvatar(avatar);
			avatar.setView(view);
			return;
		}

		if (e instanceof IceBlock) {
			IceBlockAvatar avatar = new IceBlockAvatar((IceBlock) e);
			e.setAvatar(avatar);
			avatar.setView(view);
			return;
		}

		if (e instanceof FishBonus) {
			FishBonusAvatar avatar = new FishBonusAvatar(e);
			e.setAvatar(avatar);
			avatar.setView(view);
			return;
		}
	}
}
