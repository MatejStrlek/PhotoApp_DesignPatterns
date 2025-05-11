package hr.algebra.photoapp_designpatterns_galic.security;

import hr.algebra.photoapp_designpatterns_galic.model.Consumption;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.ConsumptionRepository;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import hr.algebra.photoapp_designpatterns_galic.strategy.PackageLimitStrategy;
import hr.algebra.photoapp_designpatterns_galic.strategy.PackageLimitStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ConsumptionService {
    private final ConsumptionRepository consumptionRepository;
    private final UserService userService;
    private final PackageLimitStrategyFactory strategyFactory;

    @Autowired
    public ConsumptionService(ConsumptionRepository consumptionRepository, UserService userService, PackageLimitStrategyFactory strategyFactory) {
        this.consumptionRepository = consumptionRepository;
        this.userService = userService;
        this.strategyFactory = strategyFactory;
    }

    public void recordUpload(double uploadSizeMb) {
        User user = userService.getCurrentUser();
        PackageLimitStrategy limits = strategyFactory.getPackageLimitStrategy(user.getPackageType());

        LocalDate today = LocalDate.now();
        Consumption consumption = consumptionRepository.findByUserAndDate(user, today)
                .orElseGet(() -> new Consumption(user , today, 0, 0));

        if (uploadSizeMb > limits.getMaxUploadSizeMb()) {
            throw new IllegalArgumentException("Single file exceeds max upload size allowed by your package.");
        }
        if (consumption.getDailyUploadCount() >= limits.getDailyUploadLimit()) {
            throw new IllegalArgumentException("Daily upload limit exceeded.");
        }

        consumption.setMaxUploadSizeMb(consumption.getMaxUploadSizeMb() + uploadSizeMb);
        consumption.setDailyUploadCount(consumption.getDailyUploadCount() + 1);

        consumptionRepository.save(consumption);
    }
}