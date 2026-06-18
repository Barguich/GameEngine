package game.pengo.model;

import java.util.ArrayList;
import java.util.List;

import geometry.Grid;
import model.Entity;
import model.Model;

public class PengoModel extends Model {

    private PengoPlayer player;
    private int score;
    private boolean won;
    private boolean lost;

    private boolean doubleScore;
    private long doubleScoreRemaining;
    //la vibrations des entites quand le mur vibres
    private boolean wallVibration;
    private long wallVibrationRemaining;
    private List<Entity> vibratingEntities;//liste partagée

    // Temps d'invincibilité après une collision avec un ennemi
    private long invincibleRemaining;

    public PengoModel(Grid grid) {
        super(grid);

        this.player = null;
        this.score = 0;
        this.won = false;
        this.lost = false;

        this.doubleScore = false;
        this.doubleScoreRemaining = 0;

        this.invincibleRemaining = 0;
        //vibration false par defaut 
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
        if (allEnemiesDead()) {
            won = true;
            return;
        }

        if (diamondBlocksAligned()) {
            won = true;
        }
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

        Grid.Position p0 = diamonds.get(0).position();
        Grid.Position p1 = diamonds.get(1).position();
        Grid.Position p2 = diamonds.get(2).position();

        boolean sameX =
            p0.x() == p1.x()
         && p1.x() == p2.x();

        boolean sameY =
            p0.y() == p1.y()
         && p1.y() == p2.y();

        return sameX || sameY;
    }

    public void loseLife() {
        if (player == null) {
            return;
        }

        // Si le joueur est encore invincible, il ne perd pas de vie
        if (invincibleRemaining > 0) {
            return;
        }

        player.loseLife();

        // 2 secondes d'invincibilité
        invincibleRemaining = 2000;

        System.out.println("Le joueur perd une vie");

        if (player.dead()) {
            lost = true;
            System.out.println("GAME OVER");
        }
    }

    public boolean won() {
        return won;
    }

    public boolean lost() {
        return lost;
    }
    //cas de la vibration du mur 
    public void startWallVibration(Entity source, long duration) {
        assert source != null;
        assert duration >= 0;

        wallVibration = true;
        wallVibrationRemaining = duration;

        vibratingEntities.clear();

        for (Entity e : entities()) {//si c un ennemy pres du mur ca doit vibrer 
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
    
}