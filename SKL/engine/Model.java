package engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Model {
    protected Grid grid;
    protected List<Entity> entities;

    private boolean gameOver = false;
    private boolean win = false;
    private int score = 0;
    private int lives = 3;
    public Model() {
        this.grid = Game.grid();
        this.entities = new ArrayList<>();
    }

    public void add(Entity e) {
        if (e == null)
            throw new IllegalArgumentException("entity null");

        if (!entities.contains(e)) {
            entities.add(e);
            if (e.size() != null && e.center() != null) {
                e.deploy();
            }
        }
    }

    public void remove(Entity e) {
        if (e == null) return;
        if (!entities.contains(e)) return;

        if (e.size() != null && e.center() != null) {
            e.retract();
        }

        entities.remove(e);
        if (e.avatar() != null) {
            // optionnel : il faudra retirer l'avatar de la View si besoin
        }
    }

    public Grid grid() {
        return this.grid;
    }

    public List<Entity> entities() {
        return Collections.unmodifiableList(entities);
    }

    public void tick(long elapsed) {
        if (gameOver || win) return;
        for (Entity e : new ArrayList<>(entities)) {
            if (e.getlSpeed() != null) {
                double dt = elapsed / 1000.0;

                ISU.Vector move = Game.isu().new Vector(
                        e.getlSpeed().x() * dt,
                        e.getlSpeed().y() * dt
                );
                ISU.Coord next = e.center().mkTranslated(move);

                // déplacer via le stunt ou directement
                if (e.getStunt() != null) {
                    e.getStunt().set(next.x(), next.y());
                } else {
                    e.retract();
                    e.setCoord(next);
                    e.deploy();
                }

                // vérifier collisions non-mur (gums, fantômes)
                checkCollisions(e);
            }
        }
    }

    private void checkCollisions(Entity e) {
        for (Entity other : new ArrayList<>(entities)) {
            if (other == e) continue;
            if (!entities.contains(other)) continue;

            if (e.intersects(other)) {

                if (e instanceof game.PacMan && other instanceof game.Gum) {
                    remove(other);
                    score++;
                    checkWin();
                    continue;
                }

                if (e instanceof game.PacMan && other instanceof game.Ghost) {
                    lives--;
                    System.out.println("Vie perdue ! Vies restantes : " + lives);
                    if (lives <= 0) {
                        gameOver = true;
                        System.out.println("GAME OVER !");
                    } else {
                        // reset PacMan à sa position de départ
                        e.retract();
                        e.setPosition(Game.grid().new Position(9, 11));
                        e.deploy();
                        e.setlSpeed(null);
                        if (e.getStunt() != null) e.getStunt().collision(other);
                    }
                    return;
                }


                if (e instanceof game.Ghost && other instanceof game.PacMan) {
                    gameOver = true;
                    e.setlSpeed(Game.isu().new Vector(0, 0));
                    System.out.println("PERDU !");
                    continue;
                }

                if (other instanceof game.Obstacle) {
                    if (e.getStunt() != null) {
                        e.getStunt().collision(other);
                    }
                }
            }
        }
    }

    private void checkWin() {
        for (Entity e : entities) {
            if (e instanceof game.Gum) {
                return;
            }
        }

        win = true;
        System.out.println("GAGNÉ !");
    }

    public boolean gameOver() {
        return gameOver;
    }

    public boolean win() {
        return win;
    }

    public int score() {
        return score;
    }
    public int lives() { return lives; }
}