package hr.algebra.photoapp_designpatterns_galic.controller;

import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.service.PackageService;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;
    private final PackageService packageService;

    public AdminController(UserService userService, PackageService packageService) {
        this.userService = userService;
        this.packageService = packageService;
    }

    @GetMapping("/users")
    public String viewAllUsers(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable Long id, Model model) {
        User user = userService.findById(id).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("packages", packageService.getAllPackages());
        return "admin/edit-user";
    }

    @PostMapping("/users/edit")
    public String updateUser(
            @RequestParam Long id,
            @RequestParam String role,
            @RequestParam String packageType,
            RedirectAttributes redirectAttributes) {
        userService.updateUser(id, role, packageType);
        redirectAttributes.addFlashAttribute("uploadSuccess", "User updated successfully!");
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}