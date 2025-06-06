package hr.algebra.photoapp_designpatterns_galic.controller;

import hr.algebra.photoapp_designpatterns_galic.model.AuthProvider;
import hr.algebra.photoapp_designpatterns_galic.model.Consumption;
import hr.algebra.photoapp_designpatterns_galic.model.PackageType;
import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.repository.ConsumptionRepository;
import hr.algebra.photoapp_designpatterns_galic.repository.PackageChangeRequestRepository;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import hr.algebra.photoapp_designpatterns_galic.strategy.package_limit.PackageLimitStrategy;
import hr.algebra.photoapp_designpatterns_galic.strategy.package_limit.PackageLimitStrategyFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class UserController {
    private final UserService userService;
    private final ConsumptionRepository consumptionRepository;
    private final PackageLimitStrategyFactory packageLimitStrategyFactory;
    private final PackageChangeRequestRepository packageChangeRequestRepository;

    private static final String PACKAGE_TYPES = "packageTypes";

    public UserController(UserService userService, ConsumptionRepository consumptionRepository, PackageLimitStrategyFactory packageLimitStrategyFactory, PackageChangeRequestRepository packageChangeRequestRepository) {
        this.userService = userService;
        this.consumptionRepository = consumptionRepository;
        this.packageLimitStrategyFactory = packageLimitStrategyFactory;
        this.packageChangeRequestRepository = packageChangeRequestRepository;
    }

    @GetMapping("/")
    public String redirectToRegister() {
        return "redirect:/register";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute(PACKAGE_TYPES, PackageType.values());
        return "register";
    }

    @PostMapping("/register")
    public String registerLocalUser(@RequestParam String email,
                                    @RequestParam String password,
                                    @RequestParam String packageType,
                                    Model model) {
        try {
            PackageType type = PackageType.valueOf(packageType.toUpperCase());
            userService.registerLocalUser(email, password, type, AuthProvider.LOCAL);
            model.addAttribute("successMessage", "Registration successful! You can now log in.");

            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute(PACKAGE_TYPES, PackageType.values());
            model.addAttribute("errorMessage", "Email is already registered.");

            return "register";
        }
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        User user = userService.getCurrentUser();
        LocalDate today = LocalDate.now();
        Consumption consumption = consumptionRepository
                .findByUserAndDate(user, today)
                .orElse(null);
        PackageLimitStrategy limits = packageLimitStrategyFactory.getPackageLimitStrategy(user.getPackageType());

        packageChangeRequestRepository
                .findByUser(user)
                .ifPresent(request -> model.addAttribute("pendingRequest", request));

        model.addAttribute("user", user);
        model.addAttribute("consumption", consumption);
        model.addAttribute("limits", limits);
        model.addAttribute(PACKAGE_TYPES, PackageType.values());

        return "profile";
    }
}