package hr.algebra.photoapp_designpatterns_galic.unit_tests;

import hr.algebra.photoapp_designpatterns_galic.model.ActionType;
import hr.algebra.photoapp_designpatterns_galic.model.AuditLog;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.AuditLogRepository;
import hr.algebra.photoapp_designpatterns_galic.service.AuditLoggerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuditLoggerServiceUnitTest {
    @Mock
    private AuditLogRepository auditLogRepository;
    @InjectMocks
    private AuditLoggerService auditLoggerService;
    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
    }

    @Test
    void shouldLogActionCorrectly() {
        auditLoggerService.logAction(testUser, ActionType.LOGIN, "User logged in");

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog log = captor.getValue();
        assertEquals(testUser, log.getActor());
        assertEquals(ActionType.LOGIN, log.getActionType());
        assertEquals("User logged in", log.getAction());
        assertNotNull(log.getTimestamp());
    }

    @Test
    void shouldReturnUserLogs() {
        when(auditLogRepository.findByActorId(1L)).thenReturn(List.of(new AuditLog()));

        List<AuditLog> logs = auditLoggerService.getUserLogs(1L);

        assertEquals(1, logs.size());
        verify(auditLogRepository).findByActorId(1L);
    }
}