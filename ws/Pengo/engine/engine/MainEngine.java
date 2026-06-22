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

import testSprite.EnemyAvatar;

/**
 * Point d'entrée principal.
 *
 * Modifications par rapport à la version d'origine :
 *  - Les ennemis des scénarios de test ne sont plus freezés (frozen=false) :
 *    ils peuvent maintenant être contrôlés par leur automate GAL dès le départ.
 *    (Le freeze de 600 s empêchait Enemy.tick() d'appeler super.tick(),
 *     donc les ennemis ne bougeaient jamais même quand le bot donnait un ordre.)
 *  - Un ennemi libre (17,9) est gardé en plus pour tester la patrouille.
 */
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
	     * Bordures.
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
	    addWall(model, view, 12, 2);


	    /*
	     * ==========================================================
	     * SCENARIO 2 : IceBlock emporte Enemy puis l'écrase
	     *
	     * IceBlock en (4,5)
	     * Enemy en (8,5)    ← non-freezé : bot actif dès le départ
	     * Wall en (13,5)
	     * ==========================================================
	     */
	    addIce(model, view, 4, 5);
	    addEnemy(model, view, 8, 5, false);   // FIX : false → bot actif
	    addWall(model, view, 13, 5);


	    /*
	     * ==========================================================
	     * SCENARIO 3 : Enemy collé à l'obstacle
	     *
	     * IceBlock en (4,8)
	     * Enemy en (8,8)    ← non-freezé
	     * Wall en (9,8)
	     * ==========================================================
	     */
	    addIce(model, view, 4, 8);
	    addEnemy(model, view, 8, 8, false);   // FIX : false
	    addWall(model, view, 9, 8);


	    /*
	     * ==========================================================
	     * SCENARIO 4 : IceBlock contre IceBlock
	     *
	     * IceBlock mobile en (4,10)
	     * IceBlock obstacle en (9,10)
	     * ==========================================================
	     */
	    addIce(model, view, 4, 10);
	    addIce(model, view, 9, 10);


	    /*
	     * ==========================================================
	     * SCENARIO 5 : Vertical vers le bas
	     *
	     * IceBlock en (16,3)
	     * Enemy en (16,6)   ← non-freezé
	     * Wall en (16,10)
	     * ==========================================================
	     */
	    addIce(model, view, 16, 3);
	    addEnemy(model, view, 16, 6, false);  // FIX : false
	    addWall(model, view, 16, 10);


	    /*
	     * Ennemi libre supplémentaire pour tester la patrouille GAL en continu.
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

	/**
	 * @param frozen si true : l'ennemi reste immobile (pour scénarios de test pur).
	 *               si false : le bot GAL est actif immédiatement.
	 *
	 * NOTE : dans la version précédente tous les ennemis de scénario étaient
	 * frozen 600 s → ils ne bougeaient jamais car Enemy.tick() fait return
	 * prématurément quand frozen=true, ce qui empêche super.tick() de déplacer
	 * l'entité même si le bot avait setLinearSpeed().
	 */
	private static void addEnemy(PengoModel model, View view, int x, int y, boolean frozen) {
	    Enemy enemy = new Enemy();
	    enemy.setPosition(Game.grid().new Position(x, y));
	    enemy.setSize(Game.grid().new Dimension(1, 1));
	    model.add(enemy);

	    EnemyAvatar enemyAvatar = new EnemyAvatar(enemy);
	    enemy.setAvatar(enemyAvatar);
	    enemyAvatar.setView(view);

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

		ShapeAvatar avatar = new ShapeAvatar(wall, ShapeAvatar.Shape.RECT, 100, 100, 100, 255);
		wall.setAvatar(avatar);
		avatar.setView(view);
	}

	private static void addBorders(PengoModel model, View view) {
		int W = Game.game().width_ncell;
		int H = Game.game().height_ncell;

		// Bordure haute
		for (int x = 0; x < W; x++) addWall(model, view, x, 0);
		// Bordure basse
		for (int x = 0; x < W; x++) addWall(model, view, x, H - 1);
		// Bordure gauche (sans coins)
		for (int y = 1; y < H - 1; y++) addWall(model, view, 0, y);
		// Bordure droite (sans coins)
		for (int y = 1; y < H - 1; y++) addWall(model, view, W - 1, y);
	}
}