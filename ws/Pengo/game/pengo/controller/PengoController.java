package pengo.controller;

import engine.Game;
import geometry.ISU;
import model.Entity;
import oop.graphics.Canvas;
import oop.graphics.VirtualKeyCodes;
import pengo.model.DiamondBlock;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import view.MenuOverlay;
import view.View;

public class PengoController implements Canvas.KeyListener {

    private static final double SPEED_CM_S = 10.0;

    private static final String RESUME = "Reprendre";
    private static final String RESTART = "Recommencer";
    private static final String QUIT = "Quitter";

    private final PengoModel model;
    private final View view;

    public PengoController(PengoModel model, View view) {
        this.model = model;
        this.view = view;
        refreshMenu();
    }

    @Override
    public void pressed(Canvas canvas, int keyCode, char keyChar) {

        if (keyCode == VirtualKeyCodes.VK_ESCAPE) {
            model.togglePause();
            refreshMenu();
            return;
        }

        if (model.menuVisible()) {
            handleMenuKey(keyCode);
            return;
        }

        if (!model.running()) {
            return;
        }

        PengoPlayer player = model.player();

        if (player == null) {
            return;
        }

        ISU isu = Game.isu();
        double s = SPEED_CM_S * player.speedMultiplier();

        switch (keyCode) {
        case VirtualKeyCodes.VK_UP:
        case VirtualKeyCodes.VK_Z:
            player.setLinearSpeed(isu.new Vector(0, -s));
            player.turnTo(270);
            break;

        case VirtualKeyCodes.VK_DOWN:
        case VirtualKeyCodes.VK_S:
            player.setLinearSpeed(isu.new Vector(0, s));
            player.turnTo(90);
            break;

        case VirtualKeyCodes.VK_LEFT:
        case VirtualKeyCodes.VK_Q:
            player.setLinearSpeed(isu.new Vector(-s, 0));
            player.turnTo(180);
            break;

        case VirtualKeyCodes.VK_RIGHT:
        case VirtualKeyCodes.VK_D:
            player.setLinearSpeed(isu.new Vector(s, 0));
            player.turnTo(0);
            break;

        case VirtualKeyCodes.VK_SPACE:
            damageBlockInFront(player);
            break;

        case VirtualKeyCodes.VK_R:
            model.reset();
            refreshMenu();
            break;

        default:
            break;
        }
    }

    @Override
    public void released(Canvas canvas, int keyCode, char keyChar) {

        if (model.menuVisible()) {
            return;
        }

        PengoPlayer player = model.player();

        if (player == null) {
            return;
        }

        switch (keyCode) {
        case VirtualKeyCodes.VK_UP:
        case VirtualKeyCodes.VK_DOWN:
        case VirtualKeyCodes.VK_LEFT:
        case VirtualKeyCodes.VK_RIGHT:
        case VirtualKeyCodes.VK_Z:
        case VirtualKeyCodes.VK_S:
        case VirtualKeyCodes.VK_Q:
        case VirtualKeyCodes.VK_D:
            player.stop();
            break;

        default:
            break;
        }
    }

    @Override
    public void typed(Canvas canvas, char keyChar) {
    }

    private void refreshMenu() {
        String title;
        String[] items;

        switch (model.state()) {
        case PAUSED:
            title = "PAUSE";
            items = new String[] { RESUME, RESTART, QUIT };
            break;

        case WON:
            title = "VICTOIRE !";
            items = new String[] { RESTART, QUIT };
            break;

        case GAME_OVER:
            title = "GAME OVER";
            items = new String[] { RESTART, QUIT };
            break;

        default:
            return;
        }

        view.menu().set(title, items, 0);
    }

    private void handleMenuKey(int keyCode) {
        MenuOverlay menu = view.menu();

        switch (keyCode) {
        case VirtualKeyCodes.VK_UP:
        case VirtualKeyCodes.VK_Z:
            menu.move(-1);
            break;

        case VirtualKeyCodes.VK_DOWN:
        case VirtualKeyCodes.VK_S:
            menu.move(1);
            break;

        case VirtualKeyCodes.VK_ENTER:
        case VirtualKeyCodes.VK_SPACE:
            activateMenuItem(menu.items()[menu.selected()]);
            break;

        case VirtualKeyCodes.VK_Q:
            System.exit(0);
            break;

        case VirtualKeyCodes.VK_R:
            model.reset();
            refreshMenu();
            break;

        default:
            break;
        }
    }

    private void activateMenuItem(String item) {
        switch (item) {
        case RESUME:
            model.resume();
            break;

        case RESTART:
            model.reset();
            refreshMenu();
            break;

        case QUIT:
            System.exit(0);
            break;

        default:
            break;
        }
    }

    private void damageBlockInFront(PengoPlayer player) {
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

        Entity e = model.firstAt(Game.grid().new Position(x, y));

        if (e instanceof IceBlock && !(e instanceof DiamondBlock)) {
            ((IceBlock) e).damage();
        }
    }
}