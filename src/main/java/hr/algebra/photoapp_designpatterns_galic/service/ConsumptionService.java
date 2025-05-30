package hr.algebra.photoapp_designpatterns_galic.service;

import hr.algebra.photoapp_designpatterns_galic.model.ActionType;
import hr.algebra.photoapp_designpatterns_galic.model.Consumption;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.ConsumptionRepository;
import hr.algebra.photoapp_designpatterns_galic.strategy.package_limit.PackageLimitStrategy;
import hr.algebra.photoapp_designpatterns_galic.strategy.package_limit.PackageLimitStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ConsumptionService {
    private final ConsumptionRepository consumptionRepository;
    private final UserService userService;
    private final PackageLimitStrategyFactory strategyFactory;
    private final AuditLoggerService auditLoggerService;

    @Autowired
    public ConsumptionService(ConsumptionRepository consumptionRepository, UserService userService, PackageLimitStrategyFactory strategyFactory, AuditLoggerService auditLoggerService) {
        this.consumptionRepository = consumptionRepository;
        this.userService = userService;
        this.strategyFactory = strategyFactory;
        this.auditLoggerService = auditLoggerService;
    }

    public void recordUpload(double uploadSizeMb) {
        User user = userService.getCurrentUser();
        PackageLimitStrategy limits = strategyFactory.getPackageLimitStrategy(user.getPackageType());

        LocalDate today = LocalDate.now();
        Consumption consumption = consumptionRepository.findByUserAndDate(user, today)
                .orElseGet(() -> new Consumption(user , today, 0, 0));

        if (uploadSizeMb > limits.getMaxUploadSizeMb()) {
            auditLoggerService.logAction(
                    user,
                    ActionType.FAILED_UPLOAD,
                    "User's upload size exceeded the maximum allowed by their package: "
                            + uploadSizeMb + " MB. Max allowed: " + limits.getMaxUploadSizeMb() + " MB."
            );
            throw new IllegalArgumentException("Single file exceeds max upload size allowed by your package.");
        }
        if (consumption.getDailyUploadCount() >= limits.getDailyUploadLimit()) {
            auditLoggerService.logAction(
                    user,
                    ActionType.FAILED_UPLOAD,
                    "User's daily upload limit exceeded: " + consumption.getDailyUploadCount()
                            + ". Max allowed: " + limits.getDailyUploadLimit() + "."
            );
            throw new IllegalArgumentException("Daily upload limit exceeded.");
        }

        consumption.setMaxUploadSizeMb(consumption.getMaxUploadSizeMb() + uploadSizeMb);
        consumption.setDailyUploadCount(consumption.getDailyUploadCount() + 1);

        consumptionRepository.save(consumption);
    }
}