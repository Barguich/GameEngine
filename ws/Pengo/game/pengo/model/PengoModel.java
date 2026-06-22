package pengo.model;

import java.util.ArrayList;
import java.util.List;

import geometry.Grid;
import model.Entity;
import model.Model;

public class PengoModel extends Model {

	// États possibles de la partie
	public enum GameState {
		PLAYING, PAUSED, GAME_OVER, WON
	}

	private GameState state = GameState.PLAYING;

	// Permet de prévenir la vue ou le contrôleur quand l'état change
	public interface StateListener {
		void onStateChanged(GameState state);
	}

	private StateListener stateListener;

	// Données principales de la partie
	private PengoPlayer player;
	private int score;
	private boolean won;
	private boolean lost;

	// Évite les collisions normales pendant le traitement IceBlock / Enemy
	private boolean resolvingIceEnemyCollision;

	// Bonus temporaire de score doublé
	private boolean doubleScore;
	private long doubleScoreRemaining;

	// Effet de vibration du mur
	private boolean wallVibration;
	private long wallVibrationRemaining;
	private List<Entity> vibratingEntities;

	// Invincibilité temporaire après perte de vie
	private long invincibleRemaining;

	private Runnable sceneBuilder;

	public PengoModel(Grid grid) {
		super(grid);

		this.player = null;
		this.score = 0;
		this.won = false;
		this.lost = false;
		this.resolvingIceEnemyCollision = false;

		this.doubleScore = false;
		this.doubleScoreRemaining = 0;

		this.wallVibration = false;
		this.wallVibrationRemaining = 0;
		this.vibratingEntities = new ArrayList<Entity>();

		this.invincibleRemaining = 0;
	}

	public void setPlayer(PengoPlayer player) {
		if (player == null) {
			return;
		}

		this.player = player;

		if (!entities().contains(player)) {
			add(player);
		}
	}

	public PengoPlayer player() {
		return player;
	}

	public GameState state() {
		return state;
	}

	public void setStateListener(StateListener listener) {
		this.stateListener = listener;
	}

	// Change l'état du jeu et prévient la vue
	private void setState(GameState newState) {
		if (state == newState) {
			return;
		}

		state = newState;

		if (stateListener != null) {
			stateListener.onStateChanged(state);
		}
	}

	public boolean running() {
		return state == GameState.PLAYING;
	}

	public boolean menuVisible() {
		return state != GameState.PLAYING;
	}

	public void pause() {
		if (state == GameState.PLAYING) {
			setState(GameState.PAUSED);
		}
	}

	public void resume() {
		if (state == GameState.PAUSED) {
			setState(GameState.PLAYING);
		}
	}

	// Touche ESC : pause ou reprise
	public void togglePause() {
		if (state == GameState.PLAYING) {
			pause();
		} else if (state == GameState.PAUSED) {
			resume();
		}
	}

	public void setSceneBuilder(Runnable sceneBuilder) {
		this.sceneBuilder = sceneBuilder;
	}

	// Réinitialise complètement la partie
	public void reset() {
		clear();

		player = null;
		score = 0;
		won = false;
		lost = false;

		doubleScore = false;
		doubleScoreRemaining = 0;

		resolvingIceEnemyCollision = false;
		invincibleRemaining = 0;

		wallVibration = false;
		wallVibrationRemaining = 0;
		vibratingEntities.clear();

		if (sceneBuilder != null) {
			sceneBuilder.run();
		}

		setState(GameState.PLAYING);
	}

	public int score() {
		return score;
	}

	// Ajoute des points, avec prise en compte du bonus x2
	public void addScore(int points) {
		if (points < 0) {
			return;
		}

		if (doubleScore) {
			score += points * 2;
		} else {
			score += points;
		}
	}

	public void activateDoubleScore(long duration) {
		if (duration < 0) {
			return;
		}

		doubleScore = true;
		doubleScoreRemaining = duration;
	}

	public boolean doubleScore() {
		return doubleScore;
	}

	public void freezeEnemies(long duration) {
		if (duration < 0) {
			return;
		}

		for (Entity e : entities()) {
			if (e instanceof Enemy) {
				((Enemy) e).freeze(duration);
			}
		}
	}

	@Override
	public void tick(long elapsed) {
		if (elapsed < 0) {
			return;
		}

		// Si le jeu est en pause ou terminé, la logique ne tourne plus
		if (state != GameState.PLAYING) {
			return;
		}

		super.tick(elapsed);

		updateInvincibility(elapsed);
		updateDoubleScore(elapsed);
		updateWallVibration(elapsed);

		checkVictory();

		if (player != null && player.dead()) {
			lost = true;
			setState(GameState.GAME_OVER);
		}
	}

	private void updateInvincibility(long elapsed) {
		if (invincibleRemaining > 0) {
			invincibleRemaining -= elapsed;

			if (invincibleRemaining < 0) {
				invincibleRemaining = 0;
			}
		}
	}

	private void updateDoubleScore(long elapsed) {
		if (doubleScore) {
			doubleScoreRemaining -= elapsed;

			if (doubleScoreRemaining <= 0) {
				doubleScore = false;
				doubleScoreRemaining = 0;
			}
		}
	}

	private void updateWallVibration(long elapsed) {
		if (wallVibration) {
			wallVibrationRemaining -= elapsed;

			if (wallVibrationRemaining <= 0) {
				wallVibration = false;
				wallVibrationRemaining = 0;
				vibratingEntities.clear();
			}
		}
	}

	// Victoire si les diamants sont alignés ou si tous les ennemis sont morts
	public void checkVictory() {
		if (lost) {
			return;
		}

		if (diamondBlocksAligned() || allEnemiesDead()) {
			if (!won) {
				won = true;
				setState(GameState.WON);
			}
		}
	}

	public void respawnPlayerNearSafePlace() {
		if (player == null) {
			return;
		}

		int[][] positions = {
				{ 2, 2 },
				{ 2, 3 },
				{ 3, 2 },
				{ 3, 3 },
				{ 1, 2 }
		};

		for (int[] p : positions) {
			boolean safe = true;

			for (Entity e : entities()) {
				if (e instanceof Enemy && e.position() != null) {
					if (e.position().x() == p[0] && e.position().y() == p[1]) {
						safe = false;
						break;
					}
				}
			}

			if (safe) {
				player.setPosition(grid().new Position(p[0], p[1]));
				player.stop();
				return;
			}
		}
	}

	public int enemiesRemaining() {
		int count = 0;

		for (Entity e : entities()) {
			if (e instanceof Enemy) {
				count++;
			}
		}

		return count;
	}

	private boolean allEnemiesDead() {
		for (Entity e : entities()) {
			if (e instanceof Enemy) {
				return false;
			}
		}

		return true;
	}

	private boolean diamondBlocksAligned() {
		List<DiamondBlock> diamonds = new ArrayList<DiamondBlock>();

		for (Entity e : entities()) {
			if (e instanceof DiamondBlock) {
				diamonds.add((DiamondBlock) e);
			}
		}

		if (diamonds.size() < 3) {
			return false;
		}

		for (DiamondBlock d1 : diamonds) {
			Grid.Position p1 = d1.position();

			for (DiamondBlock d2 : diamonds) {
				Grid.Position p2 = d2.position();

				for (DiamondBlock d3 : diamonds) {
					Grid.Position p3 = d3.position();

					if (d1 == d2 || d1 == d3 || d2 == d3) {
						continue;
					}

					boolean horizontal =
							p1.y() == p2.y()
									&& p2.y() == p3.y()
									&& Math.abs(p1.x() - p2.x()) <= 1
									&& Math.abs(p2.x() - p3.x()) <= 1;

					boolean vertical =
							p1.x() == p2.x()
									&& p2.x() == p3.x()
									&& Math.abs(p1.y() - p2.y()) <= 1
									&& Math.abs(p2.y() - p3.y()) <= 1;

					if (horizontal || vertical) {
						return true;
					}
				}
			}
		}

		return false;
	}

	// Gestion de la perte de vie du joueur
	public void loseLife() {
		if (player == null) {
			return;
		}

		// Sécurité pendant le traitement spécial IceBlock / Enemy
		if (resolvingIceEnemyCollision) {
			return;
		}

		if (invincibleRemaining > 0) {
			return;
		}

		player.loseLife();
		invincibleRemaining = 2000;

		if (player.dead()) {
			lost = true;
			setState(GameState.GAME_OVER);
		} else {
			respawnPlayerNearSafePlace();
		}
	}

	public boolean won() {
		return won;
	}

	public boolean lost() {
		return lost;
	}

	// Déclenche la vibration du mur et marque les ennemis proches
	public void startWallVibration(Entity source, long duration) {
		if (source == null || duration < 0) {
			return;
		}

		wallVibration = true;
		wallVibrationRemaining = duration;

		vibratingEntities.clear();

		for (Entity e : entities()) {
			if (e instanceof Enemy) {
				if (e.distanceCenterToCenter(source) <= source.step().x() * 2) {
					vibratingEntities.add(e);
				}
			}
		}
	}

	public boolean wallVibration() {
		return wallVibration;
	}

	public boolean isVibrating(Entity e) {
		return e != null && vibratingEntities.contains(e);
	}

	// Tue un ennemi et ajoute le score associé
	public void killEnemy(Enemy enemy) {
		if (enemy == null) {
			return;
		}

		if (enemy.dead() || enemy.dying()) {
			return;
		}

		enemy.kill();
		addScore(100);
	}

	// Abîme le bloc de glace situé devant le joueur
	public void damageBlockInFront(PengoPlayer player) {
		if (player == null || player.position() == null) {
			return;
		}

		int x = player.position().x();
		int y = player.position().y();

		switch (player.orientation()) {
			case 0:
				x++;
				break;
			case 90:
				y++;
				break;
			case 180:
				x--;
				break;
			case 270:
				y--;
				break;
			default:
				return;
		}

		Entity e = firstAt(grid().new Position(x, y));

		if (e instanceof IceBlock && !(e instanceof DiamondBlock)) {
			((IceBlock) e).damage();
		}
	}

	// Position suivante d'une entité selon une direction
	public Grid.Position nextPosition(Entity e, int direction) {
		int x = e.position().x();
		int y = e.position().y();

		switch (direction) {
			case 0:
				x++;
				break;
			case 90:
				y++;
				break;
			case 180:
				x--;
				break;
			case 270:
				y--;
				break;
			default:
				break;
		}

		return grid().new Position(x, y);
	}

	public boolean blocked(Grid.Position p) {
		Entity e = firstAt(p);

		return e instanceof Wall || e instanceof IceBlock;
	}

	// Vérifie si pousser une entité la ferait sortir de la map
	public boolean pushesOffEdge(Entity e, int direction) {
		if (e == null || e.position() == null) {
			return false;
		}

		int x = e.position().x();
		int y = e.position().y();

		switch (direction) {
			case 0:
				return x + 1 >= grid().width();
			case 90:
				return y + 1 >= grid().height();
			case 180:
				return x - 1 < 0;
			case 270:
				return y - 1 < 0;
			default:
				return false;
		}
	}

	/*
	 * Gestion spéciale des blocs de glace en glissade :
	 * - le bloc peut transporter un ennemi ;
	 * - l'ennemi peut être écrasé contre un obstacle ;
	 * - sinon le bloc continue sa glissade normalement.
	 */
	public boolean moveSlidingIceBlock(IceBlock ice, geometry.ISU.Vector movement) {
		if (ice == null || movement == null) {
			return false;
		}

		if (ice.draggingEnemy()) {
			return moveIceWithDraggedEnemy(ice, movement);
		}

		Enemy touchedEnemy = enemyReachedDuringThisMovement(ice, movement);

		if (touchedEnemy != null) {
			return moveIceTouchingEnemy(ice, touchedEnemy, movement);
		}

		boolean moved = move(ice, movement);

		if (!moved) {
			ice.stopSlide();
			return false;
		}

		return true;
	}

	private boolean moveIceWithDraggedEnemy(IceBlock ice, geometry.ISU.Vector movement) {
		Enemy enemy = ice.draggedEnemy();

		if (enemy == null || enemy.dead() || enemy.dying()) {
			ice.detachEnemy();

			boolean moved = move(ice, movement);

			if (!moved) {
				ice.stopSlide();
				return false;
			}

			return true;
		}

		Grid.Position safeEnemyPosition = copyPosition(enemy.position());
		Grid.Position enemyNextCell = nextPosition(enemy, ice.direction());
		Entity obstacle = firstSolidAt(enemyNextCell, ice, enemy);

		if (obstacle != null) {
			crushEnemyByIce(ice, enemy, safeEnemyPosition);
			return true;
		}

		boolean enemyMoved = move(enemy, movement);

		if (!enemyMoved) {
			crushEnemyByIce(ice, enemy, safeEnemyPosition);
			return true;
		}

		boolean iceMoved = move(ice, movement);

		if (!iceMoved) {
			ice.stopSlide();
			return false;
		}

		return true;
	}

	private boolean moveIceTouchingEnemy(IceBlock ice, Enemy enemy, geometry.ISU.Vector movement) {
		ice.attachEnemy(enemy);

		Grid.Position safeEnemyPosition = copyPosition(enemy.position());
		Grid.Position enemyNextCell = nextPosition(enemy, ice.direction());
		Entity obstacle = firstSolidAt(enemyNextCell, ice, enemy);

		if (obstacle != null) {
			crushEnemyByIce(ice, enemy, safeEnemyPosition);
			return true;
		}

		boolean enemyMoved = move(enemy, movement);

		if (!enemyMoved) {
			crushEnemyByIce(ice, enemy, safeEnemyPosition);
			return true;
		}

		boolean iceMoved = move(ice, movement);

		if (!iceMoved) {
			ice.stopSlide();
			return false;
		}

		return true;
	}

	private Entity firstSolidAt(Grid.Position p, Entity ignoreA, Entity ignoreB) {
		if (p == null) {
			return null;
		}

		for (Entity e : new ArrayList<Entity>(entities())) {
			if (e == null || e == ignoreA || e == ignoreB) {
				continue;
			}

			if (e.position() == null) {
				continue;
			}

			if (e.position().x() != p.x() || e.position().y() != p.y()) {
				continue;
			}

			if (isCrushObstacle(e)) {
				return e;
			}
		}

		return null;
	}

	// Obstacles contre lesquels un ennemi peut être écrasé
	private boolean isCrushObstacle(Entity e) {
		return e instanceof Wall
				|| e instanceof IceBlock
				|| e instanceof DiamondBlock
				|| e instanceof GoldBlock;
	}

	private boolean entityIsInDirection(Entity from, Entity target, int direction) {
		if (from == null || target == null) {
			return false;
		}

		if (from.position() == null || target.position() == null) {
			return false;
		}

		int fx = from.position().x();
		int fy = from.position().y();

		int tx = target.position().x();
		int ty = target.position().y();

		switch (direction) {
			case 0:
				return ty == fy && tx > fx;
			case 90:
				return tx == fx && ty > fy;
			case 180:
				return ty == fy && tx < fx;
			case 270:
				return tx == fx && ty < fy;
			default:
				return false;
		}
	}

	private Enemy enemyReachedDuringThisMovement(IceBlock ice, geometry.ISU.Vector movement) {
		if (ice == null || movement == null) {
			return null;
		}

		for (Entity e : new ArrayList<Entity>(entities())) {
			if (!(e instanceof Enemy)) {
				continue;
			}

			Enemy enemy = (Enemy) e;

			if (enemy.dead() || enemy.dying() || enemy.draggedByIce()) {
				continue;
			}

			if (!entityIsInDirection(ice, enemy, ice.direction())) {
				continue;
			}

			double distance = ice.distanceCenterToCenter(enemy);
			double movementLength = Math.abs(movement.x()) + Math.abs(movement.y());
			double contactDistance = ice.step().x();
			double epsilon = 0.05;

			if (distance <= contactDistance + movementLength + epsilon) {
				return enemy;
			}
		}

		return null;
	}

	// Écrase un ennemi avec un IceBlock et ajoute le score
	private void crushEnemyByIce(IceBlock ice, Enemy enemy, Grid.Position finalIcePosition) {
		if (ice == null || enemy == null) {
			return;
		}

		enemy.markCrushedByIce();
		addScore(100);
		remove(enemy);

		ice.detachEnemy();
		ice.stopSlide();

		if (finalIcePosition != null) {
			ice.setPosition(finalIcePosition);
			ice.setBounding();
		}
	}

	private Grid.Position copyPosition(Grid.Position p) {
		if (p == null) {
			return null;
		}

		return grid().new Position(p.x(), p.y());
	}
}