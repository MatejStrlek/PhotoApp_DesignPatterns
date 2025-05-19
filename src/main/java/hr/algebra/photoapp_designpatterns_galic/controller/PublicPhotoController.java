package hr.algebra.photoapp_designpatterns_galic.controller;

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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class PublicPhotoController {
    private final PhotoShowService photoShowService;

    @Autowired
    public PublicPhotoController(PhotoShowService photoShowService) {
        this.photoShowService = photoShowService;
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
        return downloadFile(id, true);
    }

    @GetMapping("/public/photos/download/processed/{id}")
    public ResponseEntity<Resource> downloadProcessedPhoto(@PathVariable Long id) throws IOException {
        return downloadFile(id, false);
    }

    private ResponseEntity<Resource> downloadFile(@PathVariable Long id, boolean original) throws IOException {
        Photo photo = photoShowService.findPhotoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));

        String photoPath = original ? photo.getOriginalFilePath() : photo.getProcessedFilePath();
        String photoName = original ? photo.getOriginalFileName() : photo.getProcessedFileName();

        Path path = Paths.get(photoPath);
        if (!path.toFile().exists()) {
            throw new IOException("File not found");
        }

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + photoName + "\"")
                .contentLength(resource.contentLength())
                .body(resource);
    }
}