package hr.algebra.photoapp_designpatterns_galic.controller;

import hr.algebra.photoapp_designpatterns_galic.model.User;
import hr.algebra.photoapp_designpatterns_galic.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OAuthController {
    private final UserService userService;

    @Autowired
    public OAuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/oauth2/post-login")
    public String handlePostLogin() {
        User currentUser = userService.getCurrentUser();
        if (userService.needsPackageSelection(currentUser)) {
            return "redirect:/select-package";
        }
        return "redirect:/photos";
    }
}