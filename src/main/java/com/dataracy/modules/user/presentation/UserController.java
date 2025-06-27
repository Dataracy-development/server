package com.dataracy.modules.user.presentation;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/dev-api/usertest")
    public String testLog() {
        return "OK!";
    }

    @GetMapping("/ping")
    public String ping() {
        return "PONG!";
    }

    @GetMapping("/onboarding")
    public String onboarding(@CookieValue(value = "registerToken", required = false) String registerToken, Model model) {
        model.addAttribute("registerToken", registerToken);
        return "onboarding";
    }

    @GetMapping("/base")
    public String base(@CookieValue(value = "refreshToken", required = false) String refreshToken, Model model) {
        model.addAttribute("refreshToken", refreshToken);
        return "base";  // base.html 반환
    }
}

