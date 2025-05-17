package hr.algebra.photoapp_designpatterns_galic.controller;

import hr.algebra.photoapp_designpatterns_galic.model.Photo;
import hr.algebra.photoapp_designpatterns_galic.service.PhotoShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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
        return "public-photos";
    }

    @GetMapping("/photos/view/{id}")
    public String viewPhoto(@PathVariable Long id, Model model) {
        Photo photo = photoShowService.findPhotoById(id)
                .orElseThrow(() -> new IllegalArgumentException("Photo not found"));

        model.addAttribute("photo", photo);
        return "photo-view";
    }
}