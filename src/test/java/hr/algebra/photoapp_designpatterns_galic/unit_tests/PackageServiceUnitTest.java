package hr.algebra.photoapp_designpatterns_galic.unit_tests;

import hr.algebra.photoapp_designpatterns_galic.model.ActionType;
import hr.algebra.photoapp_designpatterns_galic.model.PackageChangeRequest;
import hr.algebra.photoapp_designpatterns_galic.model.PackageType;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.PackageChangeRequestRepository;
import hr.algebra.photoapp_designpatterns_galic.service.AuditLoggerService;
import hr.algebra.photoapp_designpatterns_galic.service.PackageService;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PackageServiceUnitTest {
    @Mock
    private UserService userService;
    @Mock
    private PackageChangeRequestRepository requestRepository;
    @Mock
    private AuditLoggerService auditLoggerService;
    @InjectMocks
    private PackageService packageService;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        when(userService.getCurrentUser()).thenReturn(testUser);
    }

    @Test
    void shouldSaveNewPackageRequest() {
        when(requestRepository.findByUser(testUser)).thenReturn(Optional.empty());

        packageService.requestPackageChange(PackageType.PRO);

        verify(requestRepository).save(any(PackageChangeRequest.class));
        verify(auditLoggerService).logAction(eq(testUser), eq(ActionType.CHANGE_PACKAGE), contains("PRO"));
    }

    @Test
    void shouldThrowIfRequestAlreadyMadeToday() {
        LocalDate today = LocalDate.now();
        PackageChangeRequest existing = new PackageChangeRequest();
        existing.setRequestDate(today);
        existing.setUser(testUser);
        when(requestRepository.findByUser(testUser)).thenReturn(Optional.of(existing));

        assertThrows(IllegalStateException.class, () ->
                packageService.requestPackageChange(PackageType.PRO));
    }

    @Test
    void shouldReturnAllPackageTypes() {
        assertTrue(packageService.getAllPackages().contains(PackageType.FREE));
        assertTrue(packageService.getAllPackages().contains(PackageType.PRO));
        assertTrue(packageService.getAllPackages().contains(PackageType.GOLD));
    }
}