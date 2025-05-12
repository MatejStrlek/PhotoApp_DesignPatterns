package hr.algebra.photoapp_designpatterns_galic.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ExternalLoginController {
    @GetMapping("/external-login")
    public String showExternalLoginPage() {
        return "external-login";
    }
}