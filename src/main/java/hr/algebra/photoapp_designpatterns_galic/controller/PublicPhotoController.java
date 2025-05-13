package hr.algebra.photoapp_designpatterns_galic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PublicPhotoController {
    @GetMapping("/public/photos")
    public String showPublicPhotos(Model model) {
        List<String> photos = List.of("sunset.jpg", "city.png", "nature.jpeg");
        model.addAttribute("photos", photos);
        return "public-photos";
    }
}