package pengo.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import geometry.Grid;
import model.Entity;
import model.Model;

public class PengoModel extends Model {

	public enum GameState {
		PLAYING, PAUSED, GAME_OVER, WON
	}

	public interface StateListener {
		void onStateChanged(GameState state);
	}

	private GameState state = GameState.PLAYING;
	private StateListener stateListener;

	private PengoPlayer player;
	private int score;

	private boolean won;
	private boolean lost;

	private boolean resolvingIceEnemyCollision;

	private boolean doubleScore;
	private long doubleScoreRemaining;
	private final PengoConfig config;

	private boolean wallVibration;
	private long wallVibrationRemaining;
	private List<Entity> vibratingEntities;

	private long invincibleRemaining;

	private Runnable sceneBuilder;
	private Consumer<Enemy> enemySpawnListener;
	private Consumer<BlockRespawn> blockRespawnListener;

	// Respawns de blocs en attente : chacun réapparaît quand son délai expire.
	private final List<PendingRespawn> pendingRespawns = new ArrayList<>();

	private static final class PendingRespawn {
		final Grid.Position position;
		long remaining;

		PendingRespawn(Grid.Position position, long remaining) {
			this.position = position;
			this.remaining = remaining;
		}
	}

	public PengoModel(Grid grid) {
		this(grid, new PengoConfig());
	}

	public PengoModel(Grid grid, PengoConfig config) {
		super(grid);
		assert config != null;
		this.config = config;
		player = null;
		score = 0;

		won = false;
		lost = false;

		resolvingIceEnemyCollision = false;

		doubleScore = false;
		doubleScoreRemaining = 0;

		wallVibration = false;
		wallVibrationRemaining = 0;
		vibratingEntities = new ArrayList<Entity>();

		invincibleRemaining = 0;
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

	public PengoConfig config() {
		return this.config;
	}

	public void setStateListener(StateListener listener) {
		this.stateListener = listener;
	}

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

	public void reset() {
		clear();

		player = null;
		score = 0;

		won = false;
		lost = false;

		resolvingIceEnemyCollision = false;

		doubleScore = false;
		doubleScoreRemaining = 0;

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

	public void addScore(int points) {
		if (points <= 0) {
			return;
		}

		if (doubleScore) {
			score += points * 2;
		} else {
			score += points;
		}

		System.out.println("Score = " + score);
	}

	public void activateDoubleScore(long duration) {
		if (duration <= 0) {
			return;
		}

		doubleScore = true;
		doubleScoreRemaining = duration;
	}

	public boolean doubleScore() {
		return doubleScore;
	}

	public void freezeEnemies(long duration) {
		if (duration <= 0) {
			return;
		}

		for (Entity e : new ArrayList<Entity>(entities())) {
			if (e instanceof Enemy) {
				Enemy enemy = (Enemy) e;

				if (!enemy.dead() && !enemy.dying() && !enemy.draggedByIce()) {
					enemy.freeze(duration);
				}
			}
		}
	}

	@Override
	public void tick(long elapsed) {
		if (elapsed < 0) {
			return;
		}

		if (state != GameState.PLAYING) {
			return;
		}

		super.tick(elapsed);

		checkPlayerEnemyHits();

		updateBlockRespawns(elapsed);

		if (invincibleRemaining > 0) {
			invincibleRemaining -= elapsed;

			if (invincibleRemaining < 0) {
				invincibleRemaining = 0;
			}
		}

		if (doubleScore) {
			doubleScoreRemaining -= elapsed;

			if (doubleScoreRemaining <= 0) {
				doubleScore = false;
				doubleScoreRemaining = 0;
			}
		}

		if (wallVibration) {
			wallVibrationRemaining -= elapsed;

			if (wallVibrationRemaining <= 0) {
				wallVibration = false;
				wallVibrationRemaining = 0;
				vibratingEntities.clear();
			}
		}

		checkVictory();

		if (player != null && player.dead()) {
			lost = true;
			setState(GameState.GAME_OVER);
		}
	}

	private void checkPlayerEnemyHits() {
		if (player == null) {
			return;
		}

		if (lost || won) {
			return;
		}

		if (invincibleRemaining > 0) {
			return;
		}

		for (Entity e : new ArrayList<Entity>(entities())) {
			if (!(e instanceof Enemy)) {
				continue;
			}

			Enemy enemy = (Enemy) e;

			if (enemy.harmlessForPlayer()) {
				continue;
			}

			if (dangerousPlayerEnemyContact(player, enemy)) {
				loseLife();
				return;
			}
		}
	}

	private boolean dangerousPlayerEnemyContact(PengoPlayer player, Enemy enemy) {
		if (player == null || enemy == null) {
			return false;
		}

		if (player.step() == null) {
			return false;
		}

		double cell = player.step().x();

		if (cell <= 0) {
			return false;
		}

		double distance = player.distanceCenterToCenter(enemy);

		return distance < cell * 0.95;
	}

	public boolean won() {
		return won;
	}

	public boolean lost() {
		return lost;
	}

	public void checkVictory() {
		if (lost || won) {
			return;
		}

		if (allEnemiesDead()) {
			won = true;
			setState(GameState.WON);
			System.out.println("YOU WIN - ALL ENEMIES DEAD");
		}
	}

	private boolean allEnemiesDead() {
		return enemiesRemaining() == 0;
	}

	public int enemiesRemaining() {
		int count = 0;

		for (Entity e : entities()) {
			if (e instanceof Enemy) {
				count++;
			} else if (e instanceof IceBlock && ((IceBlock) e).containsSnoBee()) {
				count++;
			}
		}
		return count;
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

		for (DiamondBlock d : diamonds) {
			if (d.position() == null) {
				continue;
			}

			int x = d.position().x();
			int y = d.position().y();

			if (diamondAt(diamonds, x + 1, y) && diamondAt(diamonds, x + 2, y)) {
				return true;
			}

			if (diamondAt(diamonds, x, y + 1) && diamondAt(diamonds, x, y + 2)) {
				return true;
			}
		}

		return false;
	}

	private boolean diamondAt(List<DiamondBlock> diamonds, int x, int y) {
		for (DiamondBlock d : diamonds) {
			if (d.position() == null) {
				continue;
			}

			if (d.position().x() == x && d.position().y() == y) {
				return true;
			}
		}

		return false;
	}

	public void loseLife() {
		if (player == null) {
			return;
		}

		if (resolvingIceEnemyCollision) {
			System.out.println("LOSE LIFE IGNORED DURING ICE/ENEMY COLLISION");
			return;
		}

		if (invincibleRemaining > 0) {
			return;
		}

		player.loseLife();
		invincibleRemaining = 2000;

		System.out.println("Le joueur perd une vie");

		if (player.dead()) {
			lost = true;
			setState(GameState.GAME_OVER);
			System.out.println("GAME OVER");
		} else {
			respawnPlayerNearSafePlace();
		}
	}

	public void respawnPlayerNearSafePlace() {
		if (player == null) {
			return;
		}

		int[][] positions = { { 2, 2 }, { 2, 3 }, { 3, 2 }, { 3, 3 }, { 1, 2 } };

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
				player.setBounding();
				player.stop();
				return;
			}
		}
	}

	public void startWallVibration(Entity source, long duration) {
		if (source == null || duration <= 0) {
			return;
		}

		wallVibration = true;
		wallVibrationRemaining = duration;

		vibratingEntities.clear();

		// tous les murs vibrent visuellement
		for (Entity e : entities()) {
			if (e instanceof Wall) {
				vibratingEntities.add(e);
			}
		}

		// uniquement les ennemis à N-1 du mur touché
		// ennemis à côté de n'importe quel mur
		for (Entity e : entities()) {
			if (e instanceof Enemy enemy) {

				for (Entity w : entities()) {
					if (w instanceof Wall) {

						if (adjacentToWall(enemy, w)) {
							vibratingEntities.add(enemy);
							enemy.passOut(5000);
							break;
						}
					}
				}
			}
		}
	}

	private boolean adjacentToWall(Entity enemy, Entity wall) {
		if (enemy == null || wall == null) {
			return false;
		}

		if (enemy.position() == null || wall.position() == null) {
			return false;
		}

		int dx = Math.abs(enemy.position().x() - wall.position().x());
		int dy = Math.abs(enemy.position().y() - wall.position().y());

		return dx + dy == 1;
	}

	public boolean wallVibration() {
		return wallVibration;
	}

	public boolean isVibrating(Entity e) {
		return e != null && vibratingEntities.contains(e);
	}

	public void killEnemy(Enemy enemy) {
		if (enemy == null) {
			return;
		}

		if (enemy.dead() || enemy.dying()) {
			return;
		}

		enemy.kill();
		addScore(config.scoreWall());
	}

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

	public Grid.Position nextPosition(Entity e, int direction) {
		if (e == null || e.position() == null) {
			return null;
		}

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

	public boolean moveSlidingIceBlock(IceBlock ice, geometry.ISU.Vector movement) {
		if (ice == null || movement == null) {
			return false;
		}

		/*
		 * CAS 1 : Le bloc transporte déjà un ou plusieurs ennemis.
		 */
		if (ice.draggingEnemy()) {
			return slideWithEnemiesInFront(ice, movement);
		}

		/*
		 * CAS 2 : Le bloc ne transporte personne. On regarde s'il va toucher un premier
		 * ennemi.
		 */
		Enemy enemy = enemyReachedDuringThisMovement(ice, movement);

		if (enemy != null) {
			System.out.println("ICEBLOCK TOUCHES FIRST ENEMY");

			ice.attachEnemyFront(enemy);

			/*
			 * Si un obstacle est directement derrière cet ennemi, il est écrasé
			 * immédiatement.
			 */
			Grid.Position enemyPos = copyPosition(enemy.position());
			Grid.Position enemyNextCell = nextPosition(enemy, ice.direction());
			Entity obstacle = firstSolidAt(enemyNextCell, ice, null);

			if (obstacle != null) {
				System.out.println("ENEMY IMMEDIATELY CRUSHED AGAINST " + obstacle.getClass().getSimpleName());

				crushDraggedEnemiesByIce(ice, enemyPos);
				return true;
			}

			return slideWithEnemiesInFront(ice, movement);
		}

		// Aucun ennemi, glissade normale.

		boolean moved = move(ice, movement);

		if (!moved) {
			ice.stopSlide();
			return false;
		}

		return true;
	}

	private boolean slideWithEnemiesInFront(IceBlock ice, geometry.ISU.Vector movement) {
		if (ice == null || movement == null) {
			return false;
		}

		resolvingIceEnemyCollision = true;

		try {
			List<Enemy> chain = validDraggedEnemies(ice);

			if (chain.isEmpty()) {
				ice.detachEnemy();

				boolean moved = move(ice, movement);

				if (!moved) {
					ice.stopSlide();
					return false;
				}

				return true;
			}

			// Le premier de la liste est l'ennemi le plus devant.

			Enemy frontEnemy = chain.get(0);

			// si un deuxième ennemi est devant la chaîne,on l'ajoute aussi à la chaîne.

			Enemy nextEnemy = enemyReachedByFrontEnemy(ice, frontEnemy, movement);

			if (nextEnemy != null) {
				System.out.println("ICEBLOCK TOUCHES ANOTHER ENEMY");

				ice.attachEnemyFront(nextEnemy);

				chain = validDraggedEnemies(ice);
				frontEnemy = chain.get(0);
			}

			// On vérifie l'obstacle devant l'ennemi le plus devant Si obstacle : tous les
			// ennemis transportés disparaissent,et le IceBlock prend la place de l'ennemi
			// le plus devant.
			Grid.Position frontEnemyPos = copyPosition(frontEnemy.position());
			Grid.Position frontEnemyNextCell = nextPosition(frontEnemy, ice.direction());
			Entity obstacle = firstSolidAt(frontEnemyNextCell, ice, null);

			if (obstacle != null) {
				System.out.println("DRAGGED ENEMIES CRUSHED AGAINST " + obstacle.getClass().getSimpleName());

				crushDraggedEnemiesByIce(ice, frontEnemyPos);
				return true;
			}

			// Les ennemis sont ghost pendant draggedByIce,donc ils ne bloquent pas le
			// moteur.On les déplace tous avec le même movement que le IceBlock.

			boolean allEnemiesMoved = true;

			for (Enemy dragged : new ArrayList<Enemy>(chain)) {
				if (dragged == null || dragged.dead() || dragged.dying()) {
					continue;
				}

				boolean moved = move(dragged, movement);

				if (!moved) {
					allEnemiesMoved = false;
				}

				dragged.stop();
			}

			boolean iceMoved = move(ice, movement);

			if (!iceMoved) {
				ice.stopSlide();

				for (Enemy dragged : chain) {
					if (dragged != null) {
						dragged.stop();
					}
				}

				return false;
			}

			if (!allEnemiesMoved) {
				placeDraggedEnemiesInFrontOfIce(ice);
			}

			return true;

		} finally {
			resolvingIceEnemyCollision = false;
		}

	}

	private List<Enemy> validDraggedEnemies(IceBlock ice) {
		List<Enemy> result = new ArrayList<Enemy>();

		if (ice == null) {
			return result;
		}

		for (Enemy enemy : new ArrayList<Enemy>(ice.draggedEnemies())) {
			if (enemy == null) {
				continue;
			}

			if (enemy.dead() || enemy.dying()) {
				continue;
			}

			if (!entities().contains(enemy)) {
				continue;
			}

			result.add(enemy);
		}

		return result;
	}

	private Enemy enemyReachedByFrontEnemy(IceBlock ice, Enemy frontEnemy, geometry.ISU.Vector movement) {

		if (ice == null || frontEnemy == null || movement == null) {
			return null;
		}

		Enemy closestEnemy = null;
		double closestDistance = Double.MAX_VALUE;

		for (Entity e : new ArrayList<Entity>(entities())) {
			if (!(e instanceof Enemy)) {
				continue;
			}

			Enemy candidate = (Enemy) e;

			if (candidate.dead() || candidate.dying() || candidate.draggedByIce()) {
				continue;
			}

			if (!entityIsInDirection(frontEnemy, candidate, ice.direction())) {
				continue;
			}

			double distance = frontEnemy.distanceCenterToCenter(candidate);
			double movementLength = Math.abs(movement.x()) + Math.abs(movement.y());
			double contactDistance = frontEnemy.step().x();

			// pour attraper l'ennemi avant que le moteur bloque.

			double epsilon = 0.35;

			if (distance <= contactDistance + movementLength + epsilon) {
				if (distance < closestDistance) {
					closestDistance = distance;
					closestEnemy = candidate;
				}
			}
		}

		return closestEnemy;
	}

	private void placeDraggedEnemiesInFrontOfIce(IceBlock ice) {
		if (ice == null || ice.position() == null) {
			return;
		}

		List<Enemy> enemies = ice.draggedEnemies();

		if (enemies.isEmpty()) {
			return;
		}

		/*
		 * La liste est : index 0 = ennemi le plus devant dernier index = ennemi le plus
		 * proche du IceBlock
		 *
		 * Donc on place depuis l'arrière vers l'avant.
		 */
		Grid.Position pos = nextPosition(ice, ice.direction());

		for (int i = enemies.size() - 1; i >= 0; i--) {
			Enemy enemy = enemies.get(i);

			if (enemy == null) {
				continue;
			}

			enemy.setPosition(pos);
			enemy.setBounding();
			enemy.stop();

			pos = nextPosition(enemy, ice.direction());
		}
	}

	private Enemy enemyReachedDuringThisMovement(IceBlock ice, geometry.ISU.Vector movement) {
		if (ice == null || movement == null) {
			return null;
		}

		Enemy closestEnemy = null;
		double closestDistance = Double.MAX_VALUE;

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

			/*
			 * Epsilon assez large pour détecter l'ennemi avant que le moteur de collision
			 * bloque le IceBlock.
			 */
			double epsilon = 0.35;

			if (distance <= contactDistance + movementLength + epsilon) {
				if (distance < closestDistance) {
					closestDistance = distance;
					closestEnemy = enemy;
				}
			}
		}

		return closestEnemy;
	}

	public boolean tryEnterCellForPlayer(PengoPlayer player, int direction, boolean allowPush) {
		if (player == null || player.position() == null) {
			return false;
		}
		if (lost() || won() || menuVisible()) {
			return false;
		}

		Grid.Position next = nextPosition(player, direction);
		if (next == null) {
			return false;
		}

		Entity front = firstAt(next);

		if (front == null) {
			return true;
		}

		if (front instanceof Wall) {
			startWallVibration(front, 1500);
			return false;
		}

		if (front instanceof IceBlock) {
			IceBlock block = (IceBlock) front;

			// HP = 1 : Pengo peut traverser, mais ne pousse pas
			if (block.passableByPlayer()) {
				player.crossIceBlock(block);
				return true;
			}

			if (block.hp() <= 0 || block.broken()) {
				return true;
			}

			// HP = 2 : Pengo ne traverse pas et ne pousse pas
			if (block.hp() == 2 || block.cracked()) {
				return false;
			}

			// HP = 3 : Pengo peut pousser
			if (allowPush && !block.sliding()) {
				block.startSlide(direction);
			}

			return false;
		}

		if (front instanceof Enemy) {
			Enemy enemy = (Enemy) front;
			if (enemy.harmlessForPlayer()) {
				return true;
			}
			loseLife();
			return false;
		}

		if (front instanceof FishBonus) {
			return true;
		}

		return true;
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

	private Entity firstSolidAt(Grid.Position p, Entity ignoreA, Entity ignoreB) {
		if (p == null) {
			return null;
		}

		for (Entity e : new ArrayList<Entity>(entities())) {
			if (e == null) {
				continue;
			}

			if (e == ignoreA || e == ignoreB) {
				continue;
			}
			// les ennemis transportés ne sont pas des obstacles.

			if (e instanceof Enemy) {
				Enemy enemy = (Enemy) e;

				if (enemy.draggedByIce() || enemy.crushedByIce() || enemy.dead() || enemy.dying()) {
					continue;
				}
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

	private boolean isCrushObstacle(Entity e) {
		if (e == null) {
			return false;
		}

		// diamondBlock et GoldBlock héritent de IceBlock, donc ils sont inclus ici.

		return e instanceof Wall || e instanceof IceBlock;
	}

	@Override
	protected boolean collisionBlocks(Entity mover, Entity other) {
		// Pengo / Enemy est géré manuellement par PengoModel.
		// On ne veut pas que le simple contact des bounding boxes tue Pengo.
		if (isPengoEnemyPair(mover, other)) {
			return false;
		}

		// Pengo est un personnage case-par-case.
		// Les murs et blocs devant lui sont déjà gérés dans startGridMove().
		if (isPengoStaticSolidPair(mover, other)) {
			return false;
		}

		// Un glaçon qui glisse ne doit pas être bloqué par la box d'un ennemi.
		// La logique spéciale est dans moveSlidingIceBlock().
		if (isSlidingIceEnemyPair(mover, other)) {
			return false;
		}
		if (isSlidingIcePengoPair(mover, other)) {
			return false;
		}

		return true;
	}

	private boolean isSlidingIcePengoPair(Entity a, Entity b) {
		if (a instanceof IceBlock) {
			IceBlock ice = (IceBlock) a;

			if (ice.sliding() && b instanceof PengoPlayer) {
				return true;
			}
		}

		if (b instanceof IceBlock) {
			IceBlock ice = (IceBlock) b;

			if (ice.sliding() && a instanceof PengoPlayer) {
				return true;
			}
		}

		return false;
	}

	private boolean isPengoEnemyPair(Entity a, Entity b) {
		return (a instanceof PengoPlayer && b instanceof Enemy) || (a instanceof Enemy && b instanceof PengoPlayer);
	}

	private boolean isPengoStaticSolidPair(Entity a, Entity b) {
		if (a instanceof PengoPlayer) {
			return isStaticSolidForPlayer(b);
		}

		if (b instanceof PengoPlayer) {
			return isStaticSolidForPlayer(a);
		}

		return false;
	}

	private boolean isSlidingIceEnemyPair(Entity a, Entity b) {
		if (a instanceof IceBlock) {
			IceBlock ice = (IceBlock) a;

			if (ice.sliding() && b instanceof Enemy) {
				return true;
			}
		}

		if (b instanceof IceBlock) {
			IceBlock ice = (IceBlock) b;

			if (ice.sliding() && a instanceof Enemy) {
				return true;
			}
		}

		return false;
	}

	private boolean isStaticSolidForPlayer(Entity e) {
		if (e instanceof Wall) {
			return true;
		}

		if (e instanceof IceBlock) {
			IceBlock ice = (IceBlock) e;
			return !ice.sliding();
		}

		return false;
	}

	private void crushDraggedEnemiesByIce(IceBlock ice, Grid.Position finalIcePosition) {
		if (ice == null) {
			return;
		}

		List<Enemy> crushedEnemies = new ArrayList<Enemy>(ice.draggedEnemies());

		if (crushedEnemies.isEmpty()) {
			return;
		}

		System.out.println("CRUSH " + crushedEnemies.size() + " ENEMY/ENEMIES BY ICE");

		for (Enemy enemy : crushedEnemies) {
			if (enemy == null) {
				continue;
			}

			enemy.markCrushedByIce();
			remove(enemy);
			addScore(config.scoreCrush());
		}

		// on arrête le bloc et on vide la liste des ennemis transportés.

		ice.stopSlide();
		// Le IceBlock prend la place de l'ennemi le plus devant.

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

	public void setEnemySpawnListener(Consumer<Enemy> listener) {
		this.enemySpawnListener = listener;
	}

	public void setBlockRespawnListener(Consumer<BlockRespawn> listener) {
		this.blockRespawnListener = listener;
	}

	public void scheduleBlockRespawn(Grid.Position position, long delay) {
		if (position == null || delay <= 0) {
			return;
		}
		pendingRespawns.add(new PendingRespawn(position.copy(), delay));
	}

	private void updateBlockRespawns(long elapsed) {
		if (pendingRespawns.isEmpty()) {
			return;
		}

		for (PendingRespawn pending : new ArrayList<>(pendingRespawns)) {
			pending.remaining -= elapsed;

			if (pending.remaining <= 0) {
				pendingRespawns.remove(pending);
				respawnBlock(pending.position);
			}
		}
	}

	private void respawnBlock(Grid.Position position) {
		if (position == null) {
			return;
		}

		// Le bloc réapparaît sur une case libre tirée au hasard ; si aucune
		// n'est trouvée, on retombe sur sa position d'origine.
		Grid.Position target = randomFreeCell();
		if (target == null) {
			target = position;
		}

		BlockRespawn block = new BlockRespawn();
		block.setPosition(target.copy());
		block.setSize(grid().new Dimension(1, 1));
		add(block);

		if (blockRespawnListener != null) {
			blockRespawnListener.accept(block);
		}
	}

	private Grid.Position randomFreeCell() {
		int w = grid().width();
		int h = grid().height();

		int minX = 1;
		int maxX = w - 2;
		int minY = 1;
		int maxY = h - 2;

		if (maxX < minX || maxY < minY) {
			return null;
		}

		for (int y = minY; y <= maxY; y++) {
			for (int x = minX; x <= maxX; x++) {
				Grid.Position p = grid().new Position(x, y);

				if (isFree(p)) {
					return p;
				}
			}
		}

		return null;
	}

	public void hatchSnoBee(IceBlock block) {
		if (block == null || block.position() == null) {
			return;
		}

		Grid.Position spawnPosition = block.position().copy();

		remove(block);

		Enemy enemy = new Enemy();
		enemy.setPosition(spawnPosition);
		enemy.setSize(grid().new Dimension(1, 1));
		add(enemy);

		if (enemySpawnListener != null) {
			enemySpawnListener.accept(enemy);
		}
	}

	public void winByDiamondAlignment() {
		if (lost || won) {
			return;
		}
		won = true;
		setState(GameState.WON);
		for (Entity entity : entities()) {
			entity.stop();
		}
	}

	@Override
	public boolean got(Entity observer, int maximum) {
		return enemiesRemaining() <= maximum;
	}
}
