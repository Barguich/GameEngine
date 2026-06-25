package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;

import org.junit.jupiter.api.Test;

import engine.Game;
import pengo.model.DiamondBlock;
import pengo.model.Enemy;
import pengo.model.FishBonus;
import pengo.model.GoldBlock;
import pengo.model.IceBlock;
import pengo.model.PengoMapLoader;
import pengo.model.PengoModel;
import pengo.model.PengoPlayer;
import pengo.model.Wall;

public class PengoMapLoaderTest {

    private File tempMap(String content) throws Exception {
        File file = File.createTempFile("pengo-test-map", ".txt");

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }

        return file;
    }

    @Test
    public void testReadMapSimple() throws Exception {
        File file = tempMap(
            "#####\n" +
            "#P E#\n" +
            "#####\n"
        );

        String[] map = PengoMapLoader.readMap(file.getAbsolutePath());

        assertEquals(5, PengoMapLoader.width(map));
        assertEquals(3, PengoMapLoader.height(map));
        assertEquals("#####", map[0]);
    }

    @Test
    public void testReadMapDataWithTorusMarker() throws Exception {
        File file = tempMap(
            "#TORUS\n" +
            "###\n" +
            "#P#\n" +
            "###\n"
        );

        PengoMapLoader.MapData data =
                PengoMapLoader.readMapData(file.getAbsolutePath());

        assertTrue(data.torus);
        assertEquals(3, data.lines.length);
        assertEquals("###", data.lines[0]);
    }

    @Test
    public void testLoadCreatesPlayerAndEntities() {
        new Game(7, 3);

        PengoModel model = new PengoModel(Game.grid());

        String[] map = {
            "#PIGEFD",
            ".......",
            "#######"
        };

        PengoMapLoader.load(model, map);

        assertNotNull(model.player());
        assertTrue(model.player() instanceof PengoPlayer);

        assertTrue(model.entities().stream().anyMatch(e -> e instanceof Wall));
        assertTrue(model.entities().stream().anyMatch(e -> e instanceof IceBlock));
        assertTrue(model.entities().stream().anyMatch(e -> e instanceof GoldBlock));
        assertTrue(model.entities().stream().anyMatch(e -> e instanceof Enemy));
        assertTrue(model.entities().stream().anyMatch(e -> e instanceof FishBonus));
        assertTrue(model.entities().stream().anyMatch(e -> e instanceof DiamondBlock));
    }

    @Test
    public void testLoadWithNullDoesNothing() {
        new Game(5, 5);

        PengoModel model = new PengoModel(Game.grid());

        PengoMapLoader.load(model, null);

        assertNull(model.player());
        assertTrue(model.entities().isEmpty());
    }

    @Test
    public void testReadMapRejectsEmptyMap() throws Exception {
        File file = tempMap("");

        assertThrows(
            IllegalArgumentException.class,
            () -> PengoMapLoader.readMap(file.getAbsolutePath())
        );
    }

    @Test
    public void testReadMapRejectsNonRectangularMap() throws Exception {
        File file = tempMap(
            "#####\n" +
            "###\n"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> PengoMapLoader.readMap(file.getAbsolutePath())
        );
    }

    @Test
    public void testLoadRejectsUnknownSymbol() {
        new Game(5, 5);

        PengoModel model = new PengoModel(Game.grid());

        String[] map = {
            "P?"
        };

        assertThrows(
            IllegalArgumentException.class,
            () -> PengoMapLoader.load(model, map)
        );
    }
    @Test
    public void testReadMapDataWithoutTorusMarker() throws Exception {
        File file = tempMap(
            "###\n" +
            "#P#\n" +
            "###\n"
        );

        PengoMapLoader.MapData data = PengoMapLoader.readMapData(file.getAbsolutePath());

        assertFalse(data.torus);
        assertEquals(3, data.lines.length);
    }
    @Test
    public void testReadMapNullPathThrowsException() {
        assertThrows(
            IllegalArgumentException.class,
            () -> PengoMapLoader.readMap(null)
        );
    }
    @Test
    public void testLoadWithNullModelDoesNothing() {
        String[] map = {
            "P"
        };

        PengoMapLoader.load(null, map);

        assertTrue(true);
    }
}