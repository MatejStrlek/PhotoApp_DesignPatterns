package hr.algebra.photoapp_designpatterns_galic.repository;

import hr.algebra.photoapp_designpatterns_galic.model.Consumption;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface ConsumptionRepository extends JpaRepository<Consumption, Long> {
    Optional<Consumption> findByUserAndDate(User user, LocalDate date);
    @Query("SELECT SUM(c.maxUploadSizeMb) FROM Consumption c WHERE c.user.id = :userId")
    Double getTotalUploadSizeByUserId(@Param("userId") Long userId);
    @Query("SELECT SUM(c.dailyUploadCount) FROM Consumption c WHERE c.user.id = :userId")
    Integer getTotalDailyUploadsByUserId(@Param("userId") Long userId);
}