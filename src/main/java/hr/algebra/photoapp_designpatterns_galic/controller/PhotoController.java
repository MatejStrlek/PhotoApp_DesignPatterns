package hr.algebra.photoapp_designpatterns_galic.controller;

import hr.algebra.photoapp_designpatterns_galic.dto.PhotoUploadDTO;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.service.PhotoUploadService;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class PhotoController {
    private final UserService userService;
    private final PhotoUploadService photoUploadService;

    public static final String PHOTO_UPLOAD_PAGE = "photo-upload";

    @Autowired
    public PhotoController(UserService userService, PhotoUploadService photoUploadService) {
        this.userService = userService;
        this.photoUploadService = photoUploadService;
    }

    @GetMapping("/photos")
    public String showPhotos(Model model) {
        User currentUser = userService.getCurrentUser();
        if (userService.needsPackageSelection(currentUser)) {
            return "redirect:/select-package";
        }

        List<String> photoNames = List.of("beach.jpg", "mountain.png", "family-photo.jpeg");

        model.addAttribute("user", currentUser);
        model.addAttribute("photos", photoNames);

        return "photos";
    }

    @GetMapping("/photos/upload")
    public String showUploadForm(Model model) {
        model.addAttribute("photoUploadDTO", new PhotoUploadDTO());
        return PHOTO_UPLOAD_PAGE;
    }

    @PostMapping("/photos/upload")
    public String handleUpload(@Valid @ModelAttribute PhotoUploadDTO photoUploadDTO,
                               BindingResult bindingResult,
                                 Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return PHOTO_UPLOAD_PAGE;
        }

        try {
            String email = userService.getCurrentUser().getEmail();
            photoUploadService.uploadPhoto(photoUploadDTO, email);
            redirectAttributes.addFlashAttribute("uploadSuccess", "Photo uploaded successfully!");
            return "redirect:/photos";
        } catch (IOException e) {
            model.addAttribute("uploadError", "Error uploading image: " + e.getMessage());
            return PHOTO_UPLOAD_PAGE;
        } catch (IllegalArgumentException e) {
            model.addAttribute("uploadError", e.getMessage());
            return PHOTO_UPLOAD_PAGE;
        }
    }
}