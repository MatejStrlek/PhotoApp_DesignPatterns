package hr.algebra.photoapp_designpatterns_galic.unit_tests;

import hr.algebra.photoapp_designpatterns_galic.model.Consumption;
import hr.algebra.photoapp_designpatterns_galic.model.PackageType;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.ConsumptionRepository;
import hr.algebra.photoapp_designpatterns_galic.service.AuditLoggerService;
import hr.algebra.photoapp_designpatterns_galic.service.ConsumptionService;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import hr.algebra.photoapp_designpatterns_galic.strategy.package_limit.PackageLimitStrategy;
import hr.algebra.photoapp_designpatterns_galic.strategy.package_limit.PackageLimitStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConsumptionServiceUnitTest {
    @Mock
    private ConsumptionRepository consumptionRepository;

    @Mock
    private UserService userService;

    @Mock
    private PackageLimitStrategyFactory strategyFactory;

    @Mock
    private PackageLimitStrategy packageLimitStrategy;

    @Mock
    private AuditLoggerService auditLoggerService;

    @InjectMocks
    private ConsumptionService consumptionService;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setEmail("user2@example.com");
        testUser.setPackageType(PackageType.FREE);

        when(userService.getCurrentUser()).thenReturn(testUser);
        when(strategyFactory.getPackageLimitStrategy(PackageType.FREE)).thenReturn(packageLimitStrategy);
    }

    @Test
    void shouldRecordUploadWhenWithinLimits() {
        when(packageLimitStrategy.getMaxUploadSizeMb()).thenReturn(3.0);
        when(packageLimitStrategy.getDailyUploadLimit()).thenReturn(5);
        when(consumptionRepository.findByUserAndDate(eq(testUser), any()))
                .thenReturn(Optional.empty());

        consumptionService.recordUpload(2.5);

        verify(consumptionRepository).save(argThat(c ->
                c.getMaxUploadSizeMb() == 2.5 &&
                        c.getDailyUploadCount() == 1 &&
                        c.getUser().equals(testUser)
        ));
    }

    @Test
    void shouldThrowExceptionIfUploadSizeTooLarge() {
        when(packageLimitStrategy.getMaxUploadSizeMb()).thenReturn(5.0);
        when(consumptionRepository.findByUserAndDate(eq(testUser), any()))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                consumptionService.recordUpload(6.0)
        );

        assertEquals("Single file exceeds max upload size allowed by your package.", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionIfDailyUploadLimitExceeded() {
        Consumption existing = new Consumption(testUser, LocalDate.now(), 4.0, 5);

        when(packageLimitStrategy.getMaxUploadSizeMb()).thenReturn(5.0);
        when(packageLimitStrategy.getDailyUploadLimit()).thenReturn(5);
        when(consumptionRepository.findByUserAndDate(eq(testUser), any()))
                .thenReturn(Optional.of(existing));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                consumptionService.recordUpload(1.0)
        );

        assertEquals("Daily upload limit exceeded.", ex.getMessage());
    }
}