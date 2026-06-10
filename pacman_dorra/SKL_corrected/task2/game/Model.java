package game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.HashMap;
import java.util.Map;

import engine.Entity;
import engine.Grid;
import entities.Ghost;
import entities.Gum;
import entities.Obstacle;
import entities.PacMan;

public class Model {

    private static final String[] MAP = {
        "####################",
        "#P.................#",
        "#.####.####.####...#",
        "#..................#",
        "#.##.##.####.##.##.#",
        "#....#........#....#",
        "#.##.#.######.#.##.#",
        " ....#...GG...#.... ",
        "#.##.#.######.#.##.#",
        "#....#........#....#",
        "#.##.##.####.##.##.#",
        "#..................#",
        "#...####.####.####.#",
        "#..................#",
        "####################"
    };

    private Game game;
    private Grid grid;
    private List<Entity> entities;

    private int score = 0;
    private boolean gameOver = false;
    private boolean win = false;
    private long ghostElapsed = 0L;
    private PacMan pacman;

    private int lives = 3;
    private Grid.Position pacmanStart;
    private Map<Ghost, Grid.Position> ghostStarts = new HashMap<Ghost, Grid.Position>();
    private boolean respawnPending = false;
    private long respawnAtMs = 0L;

    public Model(Game game) {
        assert game != null;
        this.game = game;
        this.grid = game.grid();
        this.entities = new ArrayList<Entity>();
    }

    public Grid grid() {
        return grid;
    }

    public List<Entity> entities() {
        return entities;
    }

    public int score() {
        return score;
    }

    public int lives() {
        return lives;
    }

    public boolean gameOver() {
        return gameOver;
    }

    public boolean win() {
        return win;
    }

    public PacMan pacman() {
        return pacman;
    }

    public void addScore(int pts) {
        score += pts;
    }

    public void setGameOver(boolean value) {
        gameOver = value;
    }

    public void pacmanCaughtByGhost() {
        if (gameOver || win || pacman == null || pacman.isDying()) {
            return;
        }

        lives--;
        System.out.println("PACMAN MORT : vies restantes = " + lives);
        pacman.die();

        if (lives <= 0) {
            gameOver = true;
            System.out.println("GAME OVER : plus de vies");
        } else {
            respawnPending = true;
            respawnAtMs = System.currentTimeMillis() + 1300L;
        }
    }

    private void respawnAfterDeathIfNeeded() {
        if (!respawnPending || pacman == null) {
            return;
        }

        if (System.currentTimeMillis() < respawnAtMs) {
            return;
        }

        respawnPending = false;

        if (pacmanStart != null) {
            relocateEntity(pacman, pacmanStart);
            pacman.revive();
            pacman.turnTo(0);
            System.out.println("RESPAWN PACMAN : " + pacman.position().x() + "," + pacman.position().y());
        }

        for (Map.Entry<Ghost, Grid.Position> entry : ghostStarts.entrySet()) {
            Ghost ghost = entry.getKey();
            if (entities.contains(ghost)) {
                relocateEntity(ghost, entry.getValue());
                ghost.turnTo(180);
            }
        }
    }

    private void relocateEntity(Entity e, Grid.Position p) {
        if (e == null || p == null) {
            return;
        }

        if (e.position() != null) {
            grid.cellAt(e.position()).remove(e);
        }

        e.setPosition(p.copy());
        e.setBounding();
        grid.cellAt(e.position()).add(e);
    }

    public void add(Entity e) {
        assert e != null;
        assert e.position() != null;

        if (!entities.contains(e)) {
            entities.add(e);
            e.setModel(this);
            grid.cellAt(e.position()).add(e);
        }
    }

    public void remove(Entity e) {
        entities.remove(e);
        if (e.position() != null) {
            grid.cellAt(e.position()).remove(e);
        }
    }

    public boolean isBlocked(Grid.Position p) {
        if (p == null) {
            return true;
        }

        for (Entity e : entities) {
            if ((e instanceof Obstacle) && e.position() != null && e.position().equals(p)) {
                return true;
            }
        }
        return false;
    }

    public boolean moveEntityByCell(Entity e, int dx, int dy) {
        if (e == null || e.position() == null) {
            return false;
        }

        Grid.Position next = e.position().copy();
        next.translate(grid.new Vector(dx, dy));

        if (isBlocked(next)) {
            System.out.println(e.getClass().getSimpleName() + " bloque contre Obstacle en " + next.x() + "," + next.y());
            return false;
        }

        Grid.Position old = e.position().copy();
        grid.cellAt(old).remove(e);
        e.setPosition(next);
        e.setBounding();
        grid.cellAt(e.position()).add(e);
        return true;
    }

    public void tick(long elapsed) {
        if (win) {
            return;
        }

        respawnAfterDeathIfNeeded();

        if (pacman != null && !pacman.isDying()) {
            checkCollisions();
            checkWin();
        }

        if (gameOver || respawnPending || (pacman != null && pacman.isDying())) {
            return;
        }

        ghostElapsed += elapsed;
        if (ghostElapsed >= 280L) {
            ghostElapsed = 0L;
            moveGhostsTowardPacMan();
            checkCollisions();
            checkWin();
        }
    }

    private void moveGhostsTowardPacMan() {
        if (pacman == null || pacman.isDying()) {
            return;
        }

        List<Entity> snapshot = new ArrayList<Entity>(entities);
        for (Entity e : snapshot) {
            if (e instanceof Ghost && entities.contains(e)) {
                moveGhostOneStep((Ghost) e);
            }
        }
    }

    private void moveGhostOneStep(Ghost ghost) {
        int[] dir = nextStepBfs(ghost.position(), pacman.position());
        if (dir == null) {
            return;
        }

        if (dir[0] == 1) {
            ghost.turnTo(0);
        } else if (dir[0] == -1) {
            ghost.turnTo(180);
        } else if (dir[1] == -1) {
            ghost.turnTo(90);
        } else if (dir[1] == 1) {
            ghost.turnTo(270);
        }

        if (moveEntityByCell(ghost, dir[0], dir[1])) {
            System.out.println("GHOST MOVE : " + ghost.position().x() + "," + ghost.position().y());
        }
    }

    private int[] nextStepBfs(Grid.Position start, Grid.Position goal) {
        if (start == null || goal == null) {
            return null;
        }

        int w = grid.width();
        int h = grid.height();
        boolean[][] seen = new boolean[w][h];
        int[][] firstDx = new int[w][h];
        int[][] firstDy = new int[w][h];

        Queue<Grid.Position> q = new LinkedList<Grid.Position>();
        Grid.Position s = start.copy();
        seen[s.x()][s.y()] = true;
        q.add(s);

        int[][] dirs = { { 1, 0 }, { -1, 0 }, { 0, -1 }, { 0, 1 } };

        while (!q.isEmpty()) {
            Grid.Position cur = q.remove();

            if (cur.equals(goal)) {
                return new int[] { firstDx[cur.x()][cur.y()], firstDy[cur.x()][cur.y()] };
            }

            for (int i = 0; i < dirs.length; i++) {
                Grid.Position next = cur.copy();
                next.translate(grid.new Vector(dirs[i][0], dirs[i][1]));

                if (seen[next.x()][next.y()] || isBlocked(next)) {
                    continue;
                }

                seen[next.x()][next.y()] = true;
                if (cur.equals(start)) {
                    firstDx[next.x()][next.y()] = dirs[i][0];
                    firstDy[next.x()][next.y()] = dirs[i][1];
                } else {
                    firstDx[next.x()][next.y()] = firstDx[cur.x()][cur.y()];
                    firstDy[next.x()][next.y()] = firstDy[cur.x()][cur.y()];
                }
                q.add(next);
            }
        }

        return null;
    }

    private void checkCollisions() {
        List<Entity> snapshot = new ArrayList<Entity>(entities);

        for (int i = 0; i < snapshot.size(); i++) {
            Entity e1 = snapshot.get(i);
            if (!entities.contains(e1)) {
                continue;
            }

            for (int j = i + 1; j < snapshot.size(); j++) {
                Entity e2 = snapshot.get(j);
                if (!entities.contains(e2)) {
                    continue;
                }

                if (e1.box().overlaps(e2.box()) && e1.intersects(e2)) {
                    if (e1 instanceof PacMan || e2 instanceof PacMan) {
                        System.out.println("COLLISION : " + e1.getClass().getSimpleName() + " avec " + e2.getClass().getSimpleName());
                    }
                    e1.collision(e2);
                    if (entities.contains(e2)) {
                        e2.collision(e1);
                    }
                }
            }
        }
    }

    private void checkWin() {
        if (win || gameOver) {
            return;
        }

        for (Entity e : entities) {
            if (e instanceof Gum) {
                return;
            }
        }

        win = true;
        System.out.println("YOU WIN");
    }

    public void init() {
        for (int y = 0; y < MAP.length; y++) {
            for (int x = 0; x < MAP[y].length(); x++) {
                char c = MAP[y].charAt(x);
                Grid.Position p = grid.new Position(x, y);

                if (c == '#') {
                    add(new Obstacle(p));
                } else if (c == '.') {
                    add(new Gum(p));
                } else if (c == 'P') {
                    pacmanStart = p.copy();
                    pacman = new PacMan(p);
                    add(pacman);
                } else if (c == 'G') {
                    Ghost ghost = new Ghost(p);
                    ghostStarts.put(ghost, p.copy());
                    add(ghost);
                }
            }
        }
    }
}
