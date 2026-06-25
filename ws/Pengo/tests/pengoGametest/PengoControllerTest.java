package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import engine.Game;
import oop.graphics.VirtualKeyCodes;
import pengo.controller.PengoController;
import pengo.model.DiamondBlock;
import pengo.model.IceBlock;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;

public class PengoControllerTest {

    private PengoModel newPlayingModel() {
        new Game(10, 10);
        PengoModel model = new PengoModel(Game.grid());
        model.reset();
        return model;
    }

    private PengoPlayer playerAt(int x, int y) {
        PengoPlayer player = new PengoPlayer();
        player.setPosition(Game.grid().new Position(x, y));
        player.setSize(Game.grid().new Dimension(1, 1));
        return player;
    }

    private IceBlock iceAt(int x, int y) {
        IceBlock block = new IceBlock();
        block.setPosition(Game.grid().new Position(x, y));
        block.setSize(Game.grid().new Dimension(1, 1));
        return block;
    }

    private DiamondBlock diamondAt(int x, int y) {
        DiamondBlock diamond = new DiamondBlock();
        diamond.setPosition(Game.grid().new Position(x, y));
        diamond.setSize(Game.grid().new Dimension(1, 1));
        return diamond;
    }

    private PengoController controllerWithoutView(PengoModel model) {
        try {
            Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
            Field unsafeField = unsafeClass.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);

            Object unsafe = unsafeField.get(null);

            Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);

            PengoController controller =
                (PengoController) allocateInstance.invoke(unsafe, PengoController.class);

            Field modelField = PengoController.class.getDeclaredField("model");
            modelField.setAccessible(true);
            modelField.set(controller, model);

            Field viewField = PengoController.class.getDeclaredField("view");
            viewField.setAccessible(true);
            viewField.set(controller, null);

            return controller;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int directionFor(PengoController controller, int keyCode) {
        try {
            Method method = PengoController.class.getDeclaredMethod("directionFor", int.class);
            method.setAccessible(true);
            return (int) method.invoke(controller, keyCode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void callPrivateDamageBlockInFront(PengoController controller, PengoPlayer player) {
        try {
            Method method = PengoController.class.getDeclaredMethod(
                "damageBlockInFront",
                PengoPlayer.class
            );
            method.setAccessible(true);
            method.invoke(controller, player);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testDirectionForArrowKeys() {
        PengoModel model = newPlayingModel();
        PengoController controller = controllerWithoutView(model);

        assertEquals(270, directionFor(controller, VirtualKeyCodes.VK_UP));
        assertEquals(90, directionFor(controller, VirtualKeyCodes.VK_DOWN));
        assertEquals(180, directionFor(controller, VirtualKeyCodes.VK_LEFT));
        assertEquals(0, directionFor(controller, VirtualKeyCodes.VK_RIGHT));
    }

    @Test
    public void testDirectionForZQSDKeys() {
        PengoModel model = newPlayingModel();
        PengoController controller = controllerWithoutView(model);

        assertEquals(270, directionFor(controller, VirtualKeyCodes.VK_Z));
        assertEquals(90, directionFor(controller, VirtualKeyCodes.VK_S));
        assertEquals(180, directionFor(controller, VirtualKeyCodes.VK_Q));
        assertEquals(0, directionFor(controller, VirtualKeyCodes.VK_D));
    }

    @Test
    public void testDirectionForUnknownKeyReturnsMinusOne() {
        PengoModel model = newPlayingModel();
        PengoController controller = controllerWithoutView(model);

        assertEquals(-1, directionFor(controller, -999));
    }

    @Test
    public void testPressedRightTurnsPlayerRight() {
        PengoModel model = newPlayingModel();
        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PengoController controller = controllerWithoutView(model);

        controller.pressed(null, VirtualKeyCodes.VK_RIGHT, ' ');

        assertEquals(0, player.orientation());
    }

    @Test
    public void testPressedLeftTurnsPlayerLeft() {
        PengoModel model = newPlayingModel();
        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PengoController controller = controllerWithoutView(model);

        controller.pressed(null, VirtualKeyCodes.VK_LEFT, ' ');

        assertEquals(180, player.orientation());
    }

    @Test
    public void testPressedUpTurnsPlayerUp() {
        PengoModel model = newPlayingModel();
        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PengoController controller = controllerWithoutView(model);

        controller.pressed(null, VirtualKeyCodes.VK_UP, ' ');

        assertEquals(270, player.orientation());
    }

    @Test
    public void testPressedDownTurnsPlayerDown() {
        PengoModel model = newPlayingModel();
        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PengoController controller = controllerWithoutView(model);

        controller.pressed(null, VirtualKeyCodes.VK_DOWN, ' ');

        assertEquals(90, player.orientation());
    }

    @Test
    public void testPressedSpaceDamagesIceBlockInFrontRight() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(5, 5);
        player.turnTo(0);
        model.setPlayer(player);

        IceBlock block = iceAt(6, 5);
        model.add(block);

        PengoController controller = controllerWithoutView(model);

        int hpBefore = block.hp();

        controller.pressed(null, VirtualKeyCodes.VK_SPACE, ' ');

        assertTrue(block.hp() < hpBefore);
    }

    @Test
    public void testPressedSpaceDamagesIceBlockInFrontLeft() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(5, 5);
        player.turnTo(180);
        model.setPlayer(player);

        IceBlock block = iceAt(4, 5);
        model.add(block);

        PengoController controller = controllerWithoutView(model);

        int hpBefore = block.hp();

        controller.pressed(null, VirtualKeyCodes.VK_SPACE, ' ');

        assertTrue(block.hp() < hpBefore);
    }

    @Test
    public void testPressedSpaceDamagesIceBlockInFrontDown() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(5, 5);
        player.turnTo(90);
        model.setPlayer(player);

        IceBlock block = iceAt(5, 6);
        model.add(block);

        PengoController controller = controllerWithoutView(model);

        int hpBefore = block.hp();

        controller.pressed(null, VirtualKeyCodes.VK_SPACE, ' ');

        assertTrue(block.hp() < hpBefore);
    }

    @Test
    public void testPressedSpaceDamagesIceBlockInFrontUp() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(5, 5);
        player.turnTo(270);
        model.setPlayer(player);

        IceBlock block = iceAt(5, 4);
        model.add(block);

        PengoController controller = controllerWithoutView(model);

        int hpBefore = block.hp();

        controller.pressed(null, VirtualKeyCodes.VK_SPACE, ' ');

        assertTrue(block.hp() < hpBefore);
    }

    @Test
    public void testPressedSpaceDoesNotDamageDiamondBlock() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(5, 5);
        player.turnTo(0);
        model.setPlayer(player);

        DiamondBlock diamond = diamondAt(6, 5);
        model.add(diamond);

        PengoController controller = controllerWithoutView(model);

        int hpBefore = diamond.hp();

        controller.pressed(null, VirtualKeyCodes.VK_SPACE, ' ');

        assertEquals(hpBefore, diamond.hp());
    }

    @Test
    public void testPressedSpaceWithoutPlayerDoesNotCrash() {
        PengoModel model = newPlayingModel();

        PengoController controller = controllerWithoutView(model);

        assertDoesNotThrow(() -> {
            controller.pressed(null, VirtualKeyCodes.VK_SPACE, ' ');
        });
    }

    @Test
    public void testPrivateDamageBlockInFrontWithNullDoesNotCrash() {
        PengoModel model = newPlayingModel();

        PengoController controller = controllerWithoutView(model);

        assertDoesNotThrow(() -> {
            callPrivateDamageBlockInFront(controller, null);
        });
    }

    @Test
    public void testPrivateDamageBlockInFrontWithPlayerWithoutPositionDoesNotCrash() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = new PengoPlayer();

        PengoController controller = controllerWithoutView(model);

        assertDoesNotThrow(() -> {
            callPrivateDamageBlockInFront(controller, player);
        });
    }

    @Test
    public void testReleasedDirectionDoesNotCrashWithoutBot() {
        PengoModel model = newPlayingModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PengoController controller = controllerWithoutView(model);

        assertDoesNotThrow(() -> {
            controller.released(null, VirtualKeyCodes.VK_RIGHT, ' ');
        });
    }

    @Test
    public void testReleasedUnknownKeyDoesNothing() {
        PengoModel model = newPlayingModel();

        PengoController controller = controllerWithoutView(model);

        assertDoesNotThrow(() -> {
            controller.released(null, -999, ' ');
        });
    }

    @Test
    public void testTypedDoesNothing() {
        PengoModel model = newPlayingModel();

        PengoController controller = controllerWithoutView(model);

        assertDoesNotThrow(() -> {
            controller.typed(null, 'a');
        });
    }

    @Test
    public void testPressedRResetsModel() {
        PengoModel model = newPlayingModel();

        model.addScore(100);
        assertEquals(100, model.score());

        PengoController controller = controllerWithoutView(model);

        controller.pressed(null, VirtualKeyCodes.VK_R, ' ');

        assertEquals(0, model.score());
        assertEquals(PengoModel.GameState.PLAYING, model.state());
    }
}