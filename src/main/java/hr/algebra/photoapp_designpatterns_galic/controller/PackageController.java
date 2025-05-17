package hr.algebra.photoapp_designpatterns_galic.controller;

import hr.algebra.photoapp_designpatterns_galic.model.PackageType;
import hr.algebra.photoapp_designpatterns_galic.service.PackageChangeService;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PackageController {
    private final UserService userService;
    private final PackageChangeService packageChangeService;

    @Autowired
    public PackageController(UserService userService, PackageChangeService packageChangeService) {
        this.userService = userService;
        this.packageChangeService = packageChangeService;
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

    @PostMapping("/change-package")
    public String changePackage(@RequestParam("packageType") String packageType, Model model) {
        try {
            PackageType newType = PackageType.valueOf(packageType.toUpperCase());
            packageChangeService.requestPackageChange(newType);

            model.addAttribute("success", "Your package change to " + newType + " has been scheduled and will take effect tomorrow.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return "redirect:/profile";
    }
}