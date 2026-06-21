package testSprite;

import engine.Game;
import model.Entity;
import oop.graphics.BufferedImage;
import oop.graphics.Graphics;
import pengo.model.Enemy;
import view.Avatar;

public class EnemyAvatar extends Avatar {

    private int frame = 0;
    private long lastSwitch = System.currentTimeMillis();

    public EnemyAvatar(Entity entity) {
        super(entity);
    }

    @Override
    public void paint(Graphics g, int xPix, int yPix, double scale) {
        if (!(entity instanceof Enemy)) {
            return;
        }

        Enemy enemy = (Enemy) entity;
        String path;

        if (enemy.dying()) {
            int deathFrame = (int) ((1000 - enemy.dyingRemaining()) / 55);

            if (deathFrame < 0) {
                deathFrame = 0;
            }
            if (deathFrame > 18) {
                deathFrame = 18;
            }

            path = "Asset/ennemie/sprite_enemie_" + (21 + deathFrame) + ".png";

        } else if (enemy.spawning()) {
            int spawnFrame = (int) ((800 - enemy.spawnAnimationRemaining()) / 160);

            if (spawnFrame < 0) {
                spawnFrame = 0;
            }
            if (spawnFrame > 4) {
                spawnFrame = 4;
            }

            path = "Asset/ennemie/sprite_enemie_" + spawnFrame + ".png";

        } else if (enemy.frozen()) {
            path = "Asset/ennemie/sprite_enemie_5.png";

        } else {
            long now = System.currentTimeMillis();

            if (now - lastSwitch >= 80) {
                frame = (frame + 1) % 16;
                lastSwitch = now;
            }

            path = "Asset/ennemie/sprite_enemie_" + (frame + 5) + ".png";
        }

        BufferedImage img = Sprites.get(g, path);

        double cm = Game.game().cmPerCell;
        int side = (int) Math.round(cm * scale);

        g.drawImage(img, xPix - side / 2, yPix - side / 2, side, side);
    }
}