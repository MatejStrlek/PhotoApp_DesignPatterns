package hr.algebra.photoapp_designpatterns_galic.service;

import hr.algebra.photoapp_designpatterns_galic.dto.PhotoSearchDTO;
import hr.algebra.photoapp_designpatterns_galic.model.ActionType;
import hr.algebra.photoapp_designpatterns_galic.model.Photo;
import hr.algebra.photoapp_designpatterns_galic.model.Role;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.PhotoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class PhotoShowService {
    private final PhotoRepository photoRepository;
    private final UserService userService;
    private final AuditLoggerService auditLoggerService;

    public PhotoShowService(PhotoRepository photoRepository, UserService userService, AuditLoggerService auditLoggerService) {
        this.photoRepository = photoRepository;
        this.userService = userService;
        this.auditLoggerService = auditLoggerService;
    }

    public List<Photo> findLast10AllPhotos() {
        return photoRepository.findTop10ByOrderByUploadTimeDesc();
    }

    public List<Photo> findPhotosByAuthor(User author) {
        return photoRepository.findByAuthor(author);
    }

    public Optional<Photo> findPhotoById(Long id) {
        return photoRepository.findById(id);
    }

    @Transactional
    public void updatePhotoMetadata(Long id, String description, String hashtagsCsv) {
        Photo photo = photoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found."));

        User currentUser = userService.getCurrentUser();
        boolean isOwner = photo.getAuthor().equals(currentUser);
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new SecurityException("You are not authorized to update this photo.");
        }

        photo.setDescription(description);
        List<String> hashtags = Arrays.stream(hashtagsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        photo.setHashtags(new ArrayList<>(hashtags));

        photoRepository.save(photo);

        auditLoggerService.logAction(
                currentUser,
                ActionType.EDIT,
                "Updated photo metadata for photo ID: " + id + ", Description: "
                        + description + ", Hashtag/s: " + hashtags
        );
    }

    public List<Photo> searchPhotos(PhotoSearchDTO filters) {
        String author = normalize(filters.getAuthor());
        String hashtag = normalize(filters.getHashtag());
        Double minSize = filters.getMinSize() != null ? filters.getMinSize() : null;
        Double maxSize = filters.getMaxSize() != null ? filters.getMaxSize() : null;
        LocalDateTime startDate = filters.getStartDate();
        LocalDateTime endDate = filters.getEndDate();

       StringBuilder filtersQuery = new StringBuilder("Filters applied: ");
       if (author != null) { filtersQuery.append("Author: ").append(author).append(", "); }
       if (hashtag != null) { filtersQuery.append("Hashtag: ").append(hashtag).append(", "); }
       if (minSize != null) { filtersQuery.append("Min Size: ").append(minSize).append(", "); }
       if (maxSize != null) { filtersQuery.append("Max Size: ").append(maxSize).append(", "); }
       if (startDate != null) { filtersQuery.append("Start Date: ").append(startDate).append(", "); }
       if (endDate != null) { filtersQuery.append("End Date: ").append(endDate).append(", "); }

        auditLoggerService.logAction(
                userService.getCurrentUser(),
                ActionType.SEARCH,
                filtersQuery.toString()
        );

        return photoRepository.searchPhotos(
                author,
                hashtag,
                minSize,
                maxSize,
                startDate,
                endDate
        );
    }

    private String normalize(String input) {
        return (input == null || input.trim().isEmpty()) ? null : input.trim().toLowerCase();
    }

    public List<Photo> findAllPhotos() {
        return photoRepository.findAllByOrderByUploadTimeDesc();
    }

    public void deletePhotoById(Long id) {
        photoRepository.deleteById(id);
        auditLoggerService.logAction(
                userService.getCurrentUser(),
                ActionType.DELETE,
                "Deleted photo with ID: " + id
        );
    }
}