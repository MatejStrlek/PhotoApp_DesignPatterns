package hr.algebra.photoapp_designpatterns_galic.repository;

import hr.algebra.photoapp_designpatterns_galic.model.PackageChangeRequest;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PackageChangeRequestRepository extends JpaRepository<PackageChangeRequest, Long> {
    Optional<PackageChangeRequest> findByUser(User user);
}
