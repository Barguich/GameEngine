package engine;

import engine.model.BasicStunt;
import engine.model.Entity;
import engine.model.Model;
import engine.model.Ticker;
import engine.view.Painter;
import engine.view.ShapeAvatar;
import engine.view.ViewPort;
import engine.view.View;
import engine.geometry.ISU;
import oop.graphics.Canvas;
import oop.graphics.VirtualKeyCodes;
import oop.tasks.Task;
import oop.tasks.Runtime;

public class MainEngine {

    private static final double SPEED_CM_S = 10.0;

    public static void main(String[] args) {
        Game game = new Game(20, 13);

        Model model = new Model(Game.grid());

        double mapW = game.width_cm;
        double mapH = game.height_cm;
        ViewPort viewPort = new ViewPort(mapW, mapH, mapW, mapH);
        View view = new View(model, viewPort);

        // Player — yellow oval at cell (5,5)
        Entity player = new Entity("Player");
        player.setPosition(Game.grid().new Position(5, 5));
        player.setSize(Game.grid().new Dimension(1, 1));
        model.add(player);
        ShapeAvatar playerAvatar = new ShapeAvatar(player, ShapeAvatar.Shape.OVAL, 255, 220, 220, 0);
        player.setAvatar(playerAvatar);
        playerAvatar.setView(view);

        // Stunt that flashes both entities on collision
        player.setStunt(new BasicStunt(model, player) {
            @Override
            public void collision(Entity other) {
                playerAvatar.flashCollision();
                if (other.avatar() instanceof ShapeAvatar sa)
                    sa.flashCollision();
                entity.stop();
            }
        });

        // Wall block 1 — grey rect at (8,5)
        Entity wall1 = new Entity("Wall1");
        wall1.setPosition(Game.grid().new Position(8, 5));
        wall1.setSize(Game.grid().new Dimension(1, 1));
        model.add(wall1);
        ShapeAvatar wall1Avatar = new ShapeAvatar(wall1, ShapeAvatar.Shape.RECT, 255, 120, 120, 120);
        wall1.setAvatar(wall1Avatar);
        wall1Avatar.setView(view);

        // Wall block 2 — blue rect at (5,8)
        Entity wall2 = new Entity("Wall2");
        wall2.setPosition(Game.grid().new Position(5, 8));
        wall2.setSize(Game.grid().new Dimension(1, 1));
        model.add(wall2);
        ShapeAvatar wall2Avatar = new ShapeAvatar(wall2, ShapeAvatar.Shape.RECT, 255, 60, 100, 200);
        wall2.setAvatar(wall2Avatar);
        wall2Avatar.setView(view);

        // Roaming obstacle — green oval at (10,3)
        Entity obstacle = new Entity("Obstacle");
        obstacle.setPosition(Game.grid().new Position(10, 3));
        obstacle.setSize(Game.grid().new Dimension(1, 1));
        model.add(obstacle);
        ShapeAvatar obstacleAvatar = new ShapeAvatar(obstacle, ShapeAvatar.Shape.OVAL, 255, 60, 180, 80);
        obstacle.setAvatar(obstacleAvatar);
        obstacleAvatar.setView(view);

        view.follow(player);

        int winW = (int) (mapW * game.pixelPerCm);
        int winH = (int) (mapH * game.pixelPerCm);

        Runtime.boot(
            new java.awt.Dimension(winW, winH),
            (oop.tasks.Runnable) () -> {
                Canvas canvas = (Canvas) Task.task().find("canvas");
                canvas.set(view);
                new Painter(canvas).run();
                new Ticker(model, canvas).run();

                canvas.set(new Canvas.KeyListener() {
                    @Override
                    public void pressed(Canvas canvas, int keyCode, char keyChar) {
                        ISU isu = Game.isu();
                        double s = SPEED_CM_S;
                        switch (keyCode) {
                            case VirtualKeyCodes.VK_UP:
                            case VirtualKeyCodes.VK_W:
                                player.setLinearSpeed(isu.new Vector(0, -s));
                                player.turnTo(270);
                                break;
                            case VirtualKeyCodes.VK_DOWN:
                            case VirtualKeyCodes.VK_S:
                                player.setLinearSpeed(isu.new Vector(0, s));
                                player.turnTo(90);
                                break;
                            case VirtualKeyCodes.VK_LEFT:
                            case VirtualKeyCodes.VK_A:
                                player.setLinearSpeed(isu.new Vector(-s, 0));
                                player.turnTo(180);
                                break;
                            case VirtualKeyCodes.VK_RIGHT:
                            case VirtualKeyCodes.VK_D:
                                player.setLinearSpeed(isu.new Vector(s, 0));
                                player.turnTo(0);
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void released(Canvas canvas, int keyCode, char keyChar) {
                        switch (keyCode) {
                            case VirtualKeyCodes.VK_UP:
                            case VirtualKeyCodes.VK_DOWN:
                            case VirtualKeyCodes.VK_LEFT:
                            case VirtualKeyCodes.VK_RIGHT:
                            case VirtualKeyCodes.VK_W:
                            case VirtualKeyCodes.VK_S:
                            case VirtualKeyCodes.VK_A:
                            case VirtualKeyCodes.VK_D:
                                player.stop();
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void typed(Canvas canvas, char keyChar) {}
                });
            }
        );
    }
}
