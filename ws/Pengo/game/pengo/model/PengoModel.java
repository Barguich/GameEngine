package pengo.model;

import java.util.ArrayList;
import java.util.List;

import geometry.Grid;
import model.Entity;
import model.Model;

public class PengoModel extends Model {

	/** État global de la partie, piloté par le menu et le Ticker. */
	public enum GameState { PLAYING, PAUSED, GAME_OVER, WON }

	private GameState state = GameState.PLAYING;

	/** Notifie l'extérieur (contrôleur/vue) dès que l'état de partie change. */
	public interface StateListener {
		void onStateChanged(GameState state);
	}

	private StateListener stateListener;

	private PengoPlayer player;
	private int score;
	private boolean won;
	private boolean lost;
	private boolean resolvingIceEnemyCollision;

	private boolean doubleScore;
	private long doubleScoreRemaining;
	// la vibrations des entites quand le mur vibres
	private boolean wallVibration;
	private long wallVibrationRemaining;
	private List<Entity> vibratingEntities;// liste partagée

	// Temps d'invincibilité après une collision avec un ennemi
	private long invincibleRemaining;

	public PengoModel(Grid grid) {
		super(grid);
		this.resolvingIceEnemyCollision = false;

		this.player = null;
		this.score = 0;
		this.won = false;
		this.lost = false;

		this.doubleScore = false;
		this.doubleScoreRemaining = 0;

		this.invincibleRemaining = 0;
		// vibration false par defaut
		this.wallVibration = false;
		this.wallVibrationRemaining = 0;
		this.vibratingEntities = new ArrayList<Entity>();
	}

	public void setPlayer(PengoPlayer player) {
		assert player != null;
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

	/** Change l'état et prévient le listener (utilisé par la vue pour le menu). */
	private void setState(GameState newState) {
		if (state == newState) {
			return;
		}
		state = newState;
		if (stateListener != null) {
			stateListener.onStateChanged(state);
		}
	}

	/** True si la logique doit tourner */
	public boolean running() {
		return state == GameState.PLAYING;
	}

	/** Vrai dès qu'un menu doit s'afficher par-dessus la scène. */
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

	/** Bascule pause/jeu (utilisé par la touche ESC). Sans effet si partie finie. */
	public void togglePause() {
		if (state == GameState.PLAYING) {
			pause();
		} else if (state == GameState.PAUSED) {
			resume();
		}
	}


	private Runnable sceneBuilder;

	public void setSceneBuilder(Runnable sceneBuilder) {
		this.sceneBuilder = sceneBuilder;
	}

	/**
	 * Remet la partie à zéro : vide la scène, réinitialise les compteurs/flags,
	 * puis laisse MainEngine repeupler la grille via le sceneBuilder.
	 */
	public void reset() {
		clear();
		player = null;

		score = 0;
		won = false;
		lost = false;

		doubleScore = false;
		resolvingIceEnemyCollision = false;
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
		assert points >= 0;

		if (doubleScore) {
			score += points * 2;
		} else {
			score += points;
		}

		System.out.println("Score = " + score);
	}

	public void activateDoubleScore(long duration) {
		assert duration >= 0;

		doubleScore = true;
		doubleScoreRemaining = duration;
	}

	public boolean doubleScore() {
		return doubleScore;
	}

	public void freezeEnemies(long duration) {
		assert duration >= 0;

		for (Entity e : entities()) {
			if (e instanceof Enemy) {
				((Enemy) e).freeze(duration);
			}
		}
	}

	@Override
	public void tick(long elapsed) {
		assert elapsed >= 0;

		// ni déplacement des entités, ni décompte des timers.
		if (state != GameState.PLAYING) {
			return;
		}

		super.tick(elapsed);

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

		checkVictory();

		if (player != null && player.dead()) {
			lost = true;
			setState(GameState.GAME_OVER);
		}
		if (wallVibration) {
			wallVibrationRemaining -= elapsed;

			if (wallVibrationRemaining <= 0) {
				wallVibration = false;
				wallVibrationRemaining = 0;
				vibratingEntities.clear();
			}
		}
	}

	public void checkVictory() {

	    if (lost) {
	        return;
	    }

	    if (diamondBlocksAligned() || allEnemiesDead()) {

	        if (!won) {

	            won = true;
	            setState(GameState.WON);

	            if (diamondBlocksAligned()) {
	                System.out.println("YOU WIN - DIAMOND ALIGNMENT");
	            } else {
	                System.out.println("YOU WIN - ALL ENEMIES DEAD");
	            }
	        }
	    }
	}
	public void respawnPlayerNearSafePlace() {

	    if (player == null) {
	        return;
	    }

	    int[][] positions = {
	        {2, 2},
	        {2, 3},
	        {3, 2},
	        {3, 3},
	        {1, 2}
	    };

	    for (int[] p : positions) {

	        boolean safe = true;

	        for (Entity e : entities()) {

	            if (e instanceof Enemy && e.position() != null) {

	                if (e.position().x() == p[0]
	                        && e.position().y() == p[1]) {

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

					boolean horizontal = p1.y() == p2.y() && p2.y() == p3.y() && Math.abs(p1.x() - p2.x()) <= 1
							&& Math.abs(p2.x() - p3.x()) <= 1;

					boolean vertical = p1.x() == p2.x() && p2.x() == p3.x() && Math.abs(p1.y() - p2.y()) <= 1
							&& Math.abs(p2.y() - p3.y()) <= 1;

					if (horizontal || vertical) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public void loseLife() {
	    if (player == null) {
	        return;
	    }

	    /*
	     * Très important :
	     * Pendant qu'un IceBlock transporte ou écrase un Enemy,
	     * les collisions normales du moteur ne doivent pas tuer Pengo.
	     */
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
	public boolean won() {
		return won;
	}

	public boolean lost() {
		return lost;
	}

	// cas de la vibration du mur
	public void startWallVibration(Entity source, long duration) {
		assert source != null;
		assert duration >= 0;

		wallVibration = true;
		wallVibrationRemaining = duration;

		vibratingEntities.clear();

		for (Entity e : entities()) {// si c un ennemy pres du mur ca doit vibrer
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
    //methode killenemy
    public void killEnemy(Enemy enemy) {
        if (enemy == null) {
            return;
        }

        if (enemy.dead() || enemy.dying()) {
            return;
        }

        enemy.kill();

        /*
         * Important :
         * On ajoute le score au PengoModel, pas seulement au player.
         * C'est probablement model.score() qui est affiché dans la vue.
         */
        addScore(100);
    }
    //on detruit le block de ice in front of us 
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

    /**
     * Vrai si pousser l'entite d'une case dans cette direction la ferait
     * sortir de la map. Comme la grille est un tore, la Position serait
     * sinon "enroulee" de l'autre cote : on calcule donc la coordonnee
     * brute (avant normalisation) pour detecter le franchissement du bord.
     */
    public boolean pushesOffEdge(Entity e, int direction) {
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
         * CAS 1 :
         * Le IceBlock transporte déjà un ennemi.
         */
        if (ice.draggingEnemy()) {
            Enemy enemy = ice.draggedEnemy();

            if (enemy == null || enemy.dead() || enemy.dying()) {
                ice.detachEnemy();

                boolean iceMoved = move(ice, movement);

                if (!iceMoved) {
                    ice.stopSlide();
                    return false;
                }

                return true;
            }

            /*
             * Toute cette phase est spéciale :
             * le moteur ne doit pas déclencher de game over.
             */
            resolvingIceEnemyCollision = true;

            try {
                /*
                 * Avant de déplacer l'ennemi, on vérifie s'il va toucher
                 * un obstacle. Si oui, il est écrasé.
                 */
                if (draggedEnemyWillHitSolidObstacle(enemy, ice.direction(), movement)) {
                    crushEnemyByIce(ice, enemy);
                    return true;
                }

                /*
                 * On déplace l'ennemi devant le bloc.
                 * Même si move(enemy, movement) déclenche une collision normale,
                 * loseLife() sera ignoré grâce à resolvingIceEnemyCollision.
                 */
                boolean enemyMoved = move(enemy, movement);

                if (!enemyMoved) {
                    crushEnemyByIce(ice, enemy);
                    return true;
                }

                boolean iceMoved = move(ice, movement);

                if (!iceMoved) {
                    ice.stopSlide();
                    return false;
                }

                return true;

            } finally {
                resolvingIceEnemyCollision = false;
            }
        }

        /*
         * CAS 2 :
         * Le IceBlock ne transporte personne.
         * On attend le vrai contact avec l'ennemi.
         */
        Enemy touchedEnemy = enemyReachedDuringThisMovement(ice, movement);

        if (touchedEnemy != null) {
            resolvingIceEnemyCollision = true;

            try {
                System.out.println("ICEBLOCK TOUCHES ENEMY");

                ice.attachEnemy(touchedEnemy);

                if (draggedEnemyWillHitSolidObstacle(touchedEnemy, ice.direction(), movement)) {
                    crushEnemyByIce(ice, touchedEnemy);
                    return true;
                }

                boolean enemyMoved = move(touchedEnemy, movement);

                if (!enemyMoved) {
                    crushEnemyByIce(ice, touchedEnemy);
                    return true;
                }

                boolean iceMoved = move(ice, movement);

                if (!iceMoved) {
                    ice.stopSlide();
                    return false;
                }

                return true;

            } finally {
                resolvingIceEnemyCollision = false;
            }
        }

        /*
         * CAS 3 :
         * Aucun ennemi touché.
         * Le bloc glisse normalement.
         */
        boolean moved = move(ice, movement);

        if (!moved) {
            ice.stopSlide();
            return false;
        }

        return true;
    }
    private boolean draggedEnemyWillHitSolidObstacle(
            Enemy enemy,
            int direction,
            geometry.ISU.Vector movement
    ) {
        if (enemy == null || movement == null) {
            return false;
        }

        for (Entity e : new ArrayList<Entity>(entities())) {
            if (e == null || e == enemy) {
                continue;
            }

            if (!isCrushObstacle(e)) {
                continue;
            }

            if (!entityIsInDirection(enemy, e, direction)) {
                continue;
            }

            /*
             * Distance entre Enemy et obstacle.
             * Quand deux entités de taille 1 case se touchent,
             * la distance centre à centre est environ égale à une case.
             */
            double distance = enemy.distanceCenterToCenter(e);

            double movementLength = Math.abs(movement.x()) + Math.abs(movement.y());

            double contactDistance = enemy.step().x();

            /*
             * Marge pour éviter que le moteur déclenche la collision avant nous.
             */
            double epsilon = 0.05;

            if (distance <= contactDistance + movementLength + epsilon) {
                return true;
            }
        }

        return false;
    }
    private boolean isCrushObstacle(Entity e) {
        if (e == null) {
            return false;
        }

        /*
         * Les vrais obstacles contre lesquels un ennemi peut être écrasé.
         */
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
    private void crushEnemyByIce(IceBlock ice, Enemy enemy) {
        if (enemy == null) {
            return;
        }

        System.out.println("CRUSH ENEMY BY ICE");

        /*
         * On mémorise la position de l'ennemi avant de le supprimer.
         * Le bloc doit finir exactement ici.
         */
        Grid.Position enemyPos = null;

        if (enemy.position() != null) {
            enemyPos = grid().new Position(
                enemy.position().x(),
                enemy.position().y()
            );
        }

        /*
         * L'ennemi devient inoffensif avant suppression.
         */
        enemy.markCrushedByIce();

        /*
         * Score + mort de l'ennemi.
         */
        killEnemy(enemy);

        /*
         * On retire l'ennemi de la map.
         */
        remove(enemy);

        if (ice != null) {
            /*
             * On détache l'ennemi du bloc.
             */
            ice.detachEnemy();

            /*
             * Le bloc prend la place exacte de l'ennemi.
             */
            if (enemyPos != null) {
                ice.setPosition(enemyPos);

                /*
                 * Très important :
                 * On force le recalcul du bounding après le repositionnement.
                 * Sinon le moteur peut croire que le bloc est encore en collision
                 * avec l'ancien obstacle.
                 */
                ice.setBounding();
            }

            /*
             * On arrête seulement la glissade actuelle.
             * Le bloc reste poussable après.
             */
            ice.stopSlide();
        }

        /*
         * Si tu testes avec un seul ennemi, cette ligne mettra le jeu en WON.
         * Donc le bloc ne bougera plus parce que le jeu est terminé.
         * Avec deux ennemis, ça ne bloquera pas.
         */
        checkVictory();
    }
}
   