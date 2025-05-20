package hr.algebra.photoapp_designpatterns_galic.repository;

import hr.algebra.photoapp_designpatterns_galic.model.Photo;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findTop10ByOrderByUploadTimeDesc();
    List<Photo> findByAuthor(User user);
    Optional<Photo> findTopByAuthorOrderByUploadTimeDesc(User author);
    @Query("""
    SELECT DISTINCT p FROM Photo p
    JOIN p.author a
    LEFT JOIN p.hashtags h
    WHERE
        (:author IS NULL OR LOWER(a.email) LIKE LOWER(CONCAT('%', :author, '%')))
        AND (:hashtag IS NULL OR LOWER(:hashtag) = LOWER(h))
        AND (:minSize IS NULL OR p.processedFileSizeMb >= :minSize)
        AND (:maxSize IS NULL OR p.processedFileSizeMb <= :maxSize)
        AND (:startDate IS NULL OR p.uploadTime >= :startDate)
        AND (:endDate IS NULL OR p.uploadTime <= :endDate)
    """)
    List<Photo> searchPhotos(
            @Param("author") String author,
            @Param("hashtag") String hashtag,
            @Param("minSize") Double minSize,
            @Param("maxSize") Double maxSize,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    List<Photo> findAllByOrderByUploadTimeDesc();
}