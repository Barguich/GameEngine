package engine;

import game.*;
import oop.graphics.Canvas;
import oop.tasks.Runtime;
import oop.tasks.Task;
import java.awt.Dimension;

public class Main {

    public static void main(String[] args) {
        Runtime.boot(new Dimension(760, 560), () -> {

            Game game = new Game(19, 14);
            Model model = new Model();
            View view = new View(model);
            Brain brain = new Brain(model);

            int[][] walls = {
                    // bord haut
                    {0,0},{1,0},{2,0},{3,0},{4,0},{5,0},{6,0},{7,0},{8,0},
                    {9,0},{10,0},{11,0},{12,0},{13,0},{14,0},{15,0},{16,0},{17,0},{18,0},

                    // bord bas
                    {0,13},{1,13},{2,13},{4,13},{5,13},{6,13},{7,13},{8,13},
                    {9,13},{10,13},{11,13},{12,13},{13,13},{14,13},{16,13},{17,13},{18,13},

                    // bord gauche
                    {0,1},{0,2},{0,3},{0,4},{0,6},{0,7},{0,9},{0,10},{0,11},{0,12},

                    // bord droit
                    {18,1},{18,2},{18,3},{18,4},{18,6},{18,7},{18,9},{18,10},{18,11},{18,12},

                    // blocs haut gauche / haut droit
                    {2,2},{3,2},
                    {5,2},{6,2},
                    {12,2},{13,2},
                    {15,2},{16,2},

                    // petits murs verticaux
                    {3,4},{3,5},
                    {15,4},{15,5},

                    // ligne horizontale centrale haute
                    {7,4},{8,4},{9,4},{10,4},{11,4},

                    // maison des fantômes
                    {7,6},{8,6},{10,6},{11,6},
                    {7,7},{11,7},
                    {7,8},{8,8},{9,8},{10,8},{11,8},

                    // côtés milieu
                    {2,7},{3,7},
                    {15,7},{16,7},

                    // blocs bas gauche / bas droit
                    {2,10},{3,10},
                    {5,10},{6,10},
                    {12,10},{13,10},
                    {15,10},{16,10},

                    // couloirs bas
                    {8,11},{9,11},{10,11}
            };

            for (int[] w : walls) {
                Obstacle o = new Obstacle(w[0], w[1]);
                model.add(o);
                new ObstacleAvatar(view, o);
            }

            // gums
            for (int x = 1; x < 18; x++) {
                for (int y = 1; y < 13; y++) {
                    boolean isWall = false;
                    for (int[] w : walls) {
                        if (w[0] == x && w[1] == y) { isWall = true; break; }
                    }
                    boolean isGhostZone = (x >= 7 && x <= 11 && y >= 7 && y <= 9);
                    boolean isPacmanStart = (x == 9 && y == 11);
                    if (!isWall && !isGhostZone && !isPacmanStart) {
                        Gum gum = new Gum();
                        gum.setPosition(Game.grid().new Position(x, y));
                        gum.setSize(Game.grid().new Dimension(1, 1));
                        model.add(gum);
                        new GumAvatar(view, gum);
                    }
                }
            }

            // PacMan
            PacMan pacman = new PacMan();
            pacman.setPosition(Game.grid().new Position(9, 10));
            pacman.setSize(Game.grid().new Dimension(1, 1));
            model.add(pacman);
            new PacManAvatar(view, pacman);
            BasicStunt pacStunt = new BasicStunt(model, pacman);

            // 4 fantômes
            int[][] ghostPos = {
                    {8,7},
                    {9,7},
                    {10,7},
                    {9,6}
            };
            for (int i = 0; i < 4; i++) {
                Ghost g = new Ghost();
                g.setPosition(Game.grid().new Position(ghostPos[i][0], ghostPos[i][1]));
                g.setSize(Game.grid().new Dimension(1, 1));
                model.add(g);
                new GhostAvatar(view, g);
                BasicStunt gs = new BasicStunt(model, g);
                AttackBot ab = new AttackBot(brain, gs, pacman);
                ab.setStartDelay(3000 + i * 1000); // démarrage décalé
                gs.setBot(ab);
                brain.add(ab);
            }

            Task.task().post(() -> {
                Canvas canvas = (Canvas) Task.task().find("canvas");
                new Painter(view, canvas);
                new Controller(canvas, brain, pacman, pacStunt);
            }, 100);

            startTicker(model, brain);
        });
    }

    private static void startTicker(Model model, Brain brain) {
        long[] lastTime = {System.currentTimeMillis()};
        oop.tasks.Runnable ticker = new oop.tasks.Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                long elapsed = now - lastTime[0];
                lastTime[0] = now;
                brain.tick(elapsed);
                model.tick(elapsed);
                Task.task().post(this, 33);
            }
        };
        Task.task().post(ticker, 100);
    }
}