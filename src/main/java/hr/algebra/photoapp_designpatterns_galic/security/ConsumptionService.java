package hr.algebra.photoapp_designpatterns_galic.security;

import hr.algebra.photoapp_designpatterns_galic.model.Consumption;
import hr.algebra.photoapp_designpatterns_galic.model.PackageLimit;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.ConsumptionRepository;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ConsumptionService {
    private final ConsumptionRepository consumptionRepository;
    private final UserService userService;

    @Autowired
    public ConsumptionService(ConsumptionRepository consumptionRepository, UserService userService) {
        this.consumptionRepository = consumptionRepository;
        this.userService = userService;
    }

    public void recordUpload(int uploadSizeMb) {
        User user = userService.getCurrentUser();
        PackageLimit limits = PackageLimit.getPackageLimits(user.getPackageType());

        LocalDate today = LocalDate.now();
        Consumption consumption = consumptionRepository.findByUserAndDate(user, today)
                .orElseGet(() -> new Consumption(user , today, 0, 0));

        if (uploadSizeMb > limits.getMaxUploadSizeMb()) {
            throw new IllegalArgumentException("Single file exceeds max upload size allowed by your package.");
        }
        if (consumption.getDailyUploadCount() + 1 > limits.getDailyUploadLimit()) {
            throw new IllegalArgumentException("Daily upload limit exceeded.");
        }

        consumption.setUploadSizeMb(consumption.getUploadSizeMb() + uploadSizeMb);
        consumption.setDailyUploadCount(consumption.getDailyUploadCount() + 1);

        consumptionRepository.save(consumption);
    }
}