package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.List;

import org.junit.jupiter.api.Test;

import engine.Game;
import pengo.brain.PlayerBot;
import pengo.brain.PlayerStunt;
import pengo.model.Enemy;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;

public class PlayerBotTest {

    private PengoModel newModel() {
        new Game(10, 10);
        return new PengoModel(Game.grid());
    }

    private PengoPlayer playerAt(int x, int y) {
        PengoPlayer player = new PengoPlayer();
        player.setPosition(Game.grid().new Position(x, y));
        player.setSize(Game.grid().new Dimension(1, 1));
        return player;
    }

    @SuppressWarnings("unchecked")
    private List<Integer> heldDirections(PlayerBot bot) throws Exception {
        Field field = PlayerBot.class.getDeclaredField("heldDirections");
        field.setAccessible(true);
        return (List<Integer>) field.get(bot);
    }

    @Test
    public void testPlayerBotStoresPlayerStunt() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);
        PlayerBot bot = new PlayerBot(player, stunt);

        assertSame(stunt, bot.playerStunt());
    }

    @Test
    public void testPressDirectionAddsDirection() throws Exception {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);
        PlayerBot bot = new PlayerBot(player, stunt);

        bot.pressDirection(0);

        assertTrue(heldDirections(bot).contains(0));
    }

    @Test
    public void testPressDirectionMovesDirectionToLastPosition() throws Exception {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);
        PlayerBot bot = new PlayerBot(player, stunt);

        bot.pressDirection(0);
        bot.pressDirection(90);
        bot.pressDirection(0);

        List<Integer> dirs = heldDirections(bot);

        assertEquals(2, dirs.size());
        assertEquals(Integer.valueOf(90), dirs.get(0));
        assertEquals(Integer.valueOf(0), dirs.get(1));
    }

    @Test
    public void testReleaseDirectionRemovesDirection() throws Exception {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);
        PlayerBot bot = new PlayerBot(player, stunt);

        bot.pressDirection(0);
        bot.pressDirection(90);

        assertTrue(heldDirections(bot).contains(0));
        assertTrue(heldDirections(bot).contains(90));

        bot.releaseDirection(0);

        assertFalse(heldDirections(bot).contains(0));
        assertTrue(heldDirections(bot).contains(90));
    }

    @Test
    public void testReleaseUnknownDirectionDoesNotCrash() throws Exception {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);
        PlayerBot bot = new PlayerBot(player, stunt);

        bot.releaseDirection(180);

        assertTrue(heldDirections(bot).isEmpty());
    }

    @Test
    public void testTickWithNoDirectionDoesNothing() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);
        PlayerBot bot = new PlayerBot(player, stunt);

        assertDoesNotThrow(() -> bot.tick(16));
        assertFalse(stunt.busy());
    }

    @Test
    public void testTickWhenStuntBusyDoesNotCrash() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);
        PlayerBot bot = new PlayerBot(player, stunt);

        bot.pressDirection(0);

        assertDoesNotThrow(() -> bot.tick(16));
    }

    @Test
    public void testTickWhenGameWonDoesNotCrash() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        model.checkVictory();

        assertTrue(model.won());

        PlayerStunt stunt = new PlayerStunt(model, player);
        PlayerBot bot = new PlayerBot(player, stunt);

        bot.pressDirection(0);
        stunt.done();

        assertDoesNotThrow(() -> bot.tick(16));
    }

    @Test
    public void testPressReleaseTickDoesNotCrash() {
        PengoModel model = newModel();

        PengoPlayer player = playerAt(5, 5);
        model.setPlayer(player);

        PlayerStunt stunt = new PlayerStunt(model, player);
        PlayerBot bot = new PlayerBot(player, stunt);

        assertDoesNotThrow(() -> {
            bot.pressDirection(0);
            bot.releaseDirection(0);
            bot.tick(16);
        });

        assertSame(stunt, bot.playerStunt());
    }
}