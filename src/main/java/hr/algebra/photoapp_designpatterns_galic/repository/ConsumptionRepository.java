package hr.algebra.photoapp_designpatterns_galic.repository;

import hr.algebra.photoapp_designpatterns_galic.model.Consumption;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {
    Optional<Consumption> findByUserAndDate(User user, LocalDate date);
}