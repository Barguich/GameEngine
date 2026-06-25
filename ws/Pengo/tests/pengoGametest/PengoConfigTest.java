package pengoGametest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import pengo.model.PengoConfig;

public class PengoConfigTest {

    @Test
    public void testDefaultConfigValuesArePositive() {
        PengoConfig config = new PengoConfig();

        assertTrue(config.iceSpeed() > 0);
        assertTrue(config.iceFriction() > 0);
        assertTrue(config.iceFriction() <= 1.0);

        assertTrue(config.scoreCrush() > 0);
        assertTrue(config.goldFreezeDuration() > 0);
        assertTrue(config.goldDoubleScoreDuration() > 0);
        assertTrue(config.fishBoostDuration() > 0);
    }

    @Test
    public void testConfigCanBeCreated() {
        PengoConfig config = new PengoConfig();

        assertNotNull(config);
    }
}