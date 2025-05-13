package hr.algebra.photoapp_designpatterns_galic.controller;

import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PhotoController {
    private final UserService userService;

    @Autowired
    public PhotoController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/photos")
    public String showPhotos(Model model) {
        User currentUser = userService.getCurrentUser();

        if (userService.needsPackageSelection(currentUser)) {
            return "redirect:/select-package";
        }

        // dummy data for demonstration purposes
        List<String> photoNames = List.of("beach.jpg", "mountain.png", "family-photo.jpeg");

        model.addAttribute("user", currentUser);
        model.addAttribute("photos", photoNames);

        return "photos";
    }
}