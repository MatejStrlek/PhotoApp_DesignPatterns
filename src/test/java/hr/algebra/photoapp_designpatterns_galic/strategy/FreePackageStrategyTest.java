package hr.algebra.photoapp_designpatterns_galic.strategy;

import hr.algebra.photoapp_designpatterns_galic.strategy.package_limit.FreePackageStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FreePackageStrategyTest {
    private final FreePackageStrategy freePackageStrategy = new FreePackageStrategy();

    @Test
    void shouldReturnCorrectMaxUploadSize() {
        double expectedMaxUploadSize = 3.0; // MB
        double actualMaxUploadSize = freePackageStrategy.getMaxUploadSizeMb();
        assertEquals(expectedMaxUploadSize, actualMaxUploadSize);
    }

    @Test
    void shouldReturnCorrectDailyUploadLimit() {
        int expectedDailyUploadLimit = 5; // uploads
        int actualDailyUploadLimit = freePackageStrategy.getDailyUploadLimit();
        assertEquals(expectedDailyUploadLimit, actualDailyUploadLimit);
    }
}