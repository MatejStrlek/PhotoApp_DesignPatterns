package hr.algebra.photoapp_designpatterns_galic.service;

import hr.algebra.photoapp_designpatterns_galic.model.ActionType;
import hr.algebra.photoapp_designpatterns_galic.model.AuditLog;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLoggerService {
    private final AuditLogRepository auditLogRepository;

    public AuditLoggerService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(User actor, ActionType actionType, String action) {
        AuditLog auditLog = new AuditLog();

        auditLog.setActor(actor);
        auditLog.setTimestamp(LocalDateTime.now());
        auditLog.setAction(action);
        auditLog.setActionType(actionType);

        auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getUserLogs(Long userId) {
        return auditLogRepository.findByActorId(userId);
    }
}