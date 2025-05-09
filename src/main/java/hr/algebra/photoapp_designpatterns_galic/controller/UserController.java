package hr.algebra.photoapp_designpatterns_galic.controller;

import hr.algebra.photoapp_designpatterns_galic.model.AuthProvider;
import hr.algebra.photoapp_designpatterns_galic.model.PackageType;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String redirectToRegister() {
        return "redirect:/register";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("packageTypes", PackageType.values());
        return "register";
    }

    @PostMapping("/register")
    public String registerLocalUser(@RequestParam String email,
                                    @RequestParam String password,
                                    @RequestParam String packageType,
                                    Model model) {
        try {
            PackageType type = PackageType.valueOf(packageType.toUpperCase());
            userService.registerUser(email, password, type, AuthProvider.LOCAL);

            model.addAttribute("successMessage", "Registration successful! You can now log in.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("packageTypes", PackageType.values());
            model.addAttribute("errorMessage", "Email is already registered.");
            return "register";
        }
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        return "profile";
    }

}
