package hr.algebra.photoapp_designpatterns_galic.strategy;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProPackageStrategyTest {
    private final ProPackageStrategy proPackageStrategy = new ProPackageStrategy();

    @Test
    void shouldReturnCorrectMaxUploadSize() {
        double expectedMaxUploadSize = 10; // MB
        double actualMaxUploadSize = proPackageStrategy.getMaxUploadSizeMb();
        assertEquals(expectedMaxUploadSize, actualMaxUploadSize);
    }

    @Test
    void shouldReturnCorrectDailyUploadLimit() {
        int expectedDailyUploadLimit = 50; // uploads
        int actualDailyUploadLimit = proPackageStrategy.getDailyUploadLimit();
        assertEquals(expectedDailyUploadLimit, actualDailyUploadLimit);
    }
}