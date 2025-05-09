package hr.algebra.photoapp_designpatterns_galic.controller;

import hr.algebra.photoapp_designpatterns_galic.model.PackageType;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PackageController {
    private final UserService userService;

    @Autowired
    public PackageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/select-package")
    public String selectPackageForm(Model model) {
        model.addAttribute("packageTypes", PackageType.values());
        return "select-package";
    }

    @PostMapping("/select-package")
    public String selectedPackage(String packageType) {
        PackageType type = PackageType.valueOf(packageType.toUpperCase());
        userService.setCurrentUserPackage(type);
        return "redirect:/photos";
    }
}
