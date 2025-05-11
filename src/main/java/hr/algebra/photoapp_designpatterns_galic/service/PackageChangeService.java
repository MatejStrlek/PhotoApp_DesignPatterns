package hr.algebra.photoapp_designpatterns_galic.service;

import hr.algebra.photoapp_designpatterns_galic.model.PackageChangeRequest;
import hr.algebra.photoapp_designpatterns_galic.model.PackageType;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.PackageChangeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class PackageChangeService {
    private final UserService userService;
    private final PackageChangeRequestRepository requestRepository;

    @Autowired
    public PackageChangeService(UserService userService, PackageChangeRequestRepository requestRepository) {
        this.userService = userService;
        this.requestRepository = requestRepository;
    }

    public void requestPackageChange(PackageType newType) {
        User user = userService.getCurrentUser();
        LocalDate today = LocalDate.now();

        Optional<PackageChangeRequest> existingRequest = requestRepository.findByUser(user);

        if (existingRequest.isPresent() && existingRequest.get().hasRequestedToday(today)) {
            throw new IllegalStateException("You can only request one package change per day.");
        }

        PackageChangeRequest changeRequest = existingRequest.orElse(new PackageChangeRequest());
        changeRequest.setUser(user);
        changeRequest.setRequestedPackageType(newType);
        changeRequest.setRequestDate(today);

        requestRepository.save(changeRequest);
    }
}