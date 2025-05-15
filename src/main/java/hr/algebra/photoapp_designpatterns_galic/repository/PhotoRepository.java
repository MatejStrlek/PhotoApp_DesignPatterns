package hr.algebra.photoapp_designpatterns_galic.repository;

import hr.algebra.photoapp_designpatterns_galic.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
}