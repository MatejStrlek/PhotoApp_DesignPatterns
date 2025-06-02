package hr.algebra.photoapp_designpatterns_galic.scheduler;

import hr.algebra.photoapp_designpatterns_galic.command.ChangeUserPackageCommand;
import hr.algebra.photoapp_designpatterns_galic.command.ICommand;
import hr.algebra.photoapp_designpatterns_galic.repository.PackageChangeRequestRepository;
import hr.algebra.photoapp_designpatterns_galic.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PackageChangeScheduler {
    private final PackageChangeRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Autowired
    public PackageChangeScheduler(PackageChangeRequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }

    // For testing purposes, this method will be called every minute
    @Scheduled(cron = "0 * * * * *")
    // Runs every day at midnight
    //@Scheduled(cron = "0 0 0 * * *")
    public void processPackageChangeRequests() {
        var requests = requestRepository.findAll();

        for (var request : requests) {
            ICommand cmd = new ChangeUserPackageCommand(
                    request.getUser(),
                    request.getRequestedPackageType(),
                    userRepository
            );

            cmd.execute();
            requestRepository.delete(request);
        }
    }
}