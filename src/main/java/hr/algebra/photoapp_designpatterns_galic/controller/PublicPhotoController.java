package hr.algebra.photoapp_designpatterns_galic.controller;

import hr.algebra.photoapp_designpatterns_galic.model.ActionType;
import hr.algebra.photoapp_designpatterns_galic.service.AuditLoggerService;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import org.springframework.core.io.Resource;
import hr.algebra.photoapp_designpatterns_galic.dto.PhotoSearchDTO;
import hr.algebra.photoapp_designpatterns_galic.model.Photo;
import hr.algebra.photoapp_designpatterns_galic.service.PhotoShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class PublicPhotoController {
    private final PhotoShowService photoShowService;
    private final AuditLoggerService auditLoggerService;
    private final UserService userService;

    @Autowired
    public PublicPhotoController(PhotoShowService photoShowService, AuditLoggerService auditLoggerService, UserService userService) {
        this.photoShowService = photoShowService;
        this.auditLoggerService = auditLoggerService;
        this.userService = userService;
    }

    @GetMapping("/public/photos")
    public String showPublicPhotos(Model model) {
        List<Photo> photos = photoShowService.findLast10AllPhotos();
        model.addAttribute("photos", photos);
        model.addAttribute("searchDTO", new PhotoSearchDTO());
        return "public-photos";
    }

    @GetMapping("/photos/view/{id}")
    public String viewPhoto(@PathVariable Long id, Model model) {
        Photo photo = photoShowService.findPhotoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));

        auditLoggerService.logAction(
                userService.getCurrentUser(),
                ActionType.VIEW,
                "Viewed photo with ID: " + id
        );

        model.addAttribute("photo", photo);
        return "photo-view";
    }

    @GetMapping("/public/photos/search")
    public String searchPhotos(@ModelAttribute("searchDTO") PhotoSearchDTO filter, Model model) {
        List<Photo> filteredPhotos = photoShowService.searchPhotos(filter);
        model.addAttribute("photos", filteredPhotos);
        return "public-photos";
    }

    @GetMapping("/public/photos/download/original/{id}")
    public ResponseEntity<Resource> downloadOriginalPhoto(@PathVariable Long id) throws IOException {
        auditLoggerService.logAction(
                userService.getCurrentUser(),
                ActionType.DOWNLOAD,
                "Downloaded original photo with ID: " + id
        );
        return downloadFile(id, true);
    }

    @GetMapping("/public/photos/download/processed/{id}")
    public ResponseEntity<Resource> downloadProcessedPhoto(@PathVariable Long id) throws IOException {
        auditLoggerService.logAction(
                userService.getCurrentUser(),
                ActionType.DOWNLOAD,
                "Downloaded processed photo with ID: " + id
        );
        return downloadFile(id, false);
    }

    private ResponseEntity<Resource> downloadFile(@PathVariable Long id, boolean original) throws IOException {
        Photo photo = photoShowService.findPhotoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));

        String photoPath = original ? photo.getOriginalFilePath() : photo.getProcessedFilePath();
        String photoName = original ? photo.getOriginalFileName() : photo.getProcessedFileName();

        if (photoPath.startsWith("http")) {
            try (InputStream in = new URL(photoPath).openStream()) {
                byte[] bytes = in.readAllBytes();
                ByteArrayResource resource = new ByteArrayResource(bytes);

                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=\"" + photoName + "\"")
                        .contentLength(resource.contentLength())
                        .body(resource);
            } catch (IOException e) {
                throw new IOException("Cloud image not accessible: " + photoPath, e);
            }
        } else {
            Path path = Paths.get(photoPath);
            if (!Files.exists(path)) {
                throw new IOException("File not found: " + photoPath);
            }

            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + photoName + "\"")
                    .contentLength(resource.contentLength())
                    .body(resource);
        }
    }
}