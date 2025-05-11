package hr.algebra.photoapp_designpatterns_galic.strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GoldPackageStrategyTest {
    private final GoldPackageStrategy goldPackageStrategy = new GoldPackageStrategy();

    @Test
    void shouldReturnCorrectMaxUploadSize() {
        double expectedMaxUploadSize = 50.0; // MB
        double actualMaxUploadSize = goldPackageStrategy.getMaxUploadSizeMb();
        assertEquals(expectedMaxUploadSize, actualMaxUploadSize);
    }

    @Test
    void shouldReturnCorrectDailyUploadLimit() {
        int expectedDailyUploadLimit = 100; // uploads
        int actualDailyUploadLimit = goldPackageStrategy.getDailyUploadLimit();
        assertEquals(expectedDailyUploadLimit, actualDailyUploadLimit);
    }
}